package vandy.mooc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A main Activity that prompts the user for a URL to an image and
 * then uses Intents and other Activities to download the image and
 * view it.
 */
public class MainActivity extends LifecycleLoggingActivity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * A value that uniquely identifies the request to download an
     * image.
     */
    private static final int DOWNLOAD_IMAGE_REQUEST = 1;

    /**
     * EditText field for entering the desired URL to an image.
     */
    private EditText mUrlEditText;
    
    private Button mButton;
    /**
     * URL for the image that's downloaded by default if the user
     * doesn't specify otherwise.
     */
    private Uri mDefaultUrl =
        Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");
    private Handler handler;
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
    	super.onCreate(savedInstanceState);
        // Set the default layout.
    	
        // Cache the EditText that holds the urls entered by the user
        // (if any).
    	initUI();
    	handler = new MyHandler(this);
    }

    private void initHandler() {
    	 handler=new Handler(Looper.getMainLooper()) {
    	    @Override
    	    public void handleMessage(Message msg) {
    	      mButton.setEnabled(true);
    	      switch (msg.what){
    	      	case DownloadImageActivity.DOWNLOADACTIVITY:
    	      		((Activity) msg.obj).finish();
    	      }
    	    }
    	  };		
	}

    
    private class MyHandler extends Handler{
    	Activity mActivity;
    	private MyHandler(Activity activity){
    		super(Looper.getMainLooper());
    		mActivity = activity;
    	}
    	
    	
    	@Override
 	    public void handleMessage(final Message msg) {
 	      mButton.setEnabled(true);
 	      switch (msg.what){
 	      	case DownloadImageActivity.DOWNLOADACTIVITY:{
 	      	 if (Looper.getMainLooper() == Looper.myLooper()) {
 	      		 ((Activity) msg.obj).finish();
 	         } 
 	        // Otherwise, create a new Runnable command that's posted to
 	        // the UI Thread to display the image.
 	        else {
 	            mActivity.runOnUiThread(new Runnable() {
 	                public void run() {   
 	                	 ((Activity) msg.obj).finish();
 	            }});
 	        }
 	      	}
 	      }
 	    }
    }
	private void initUI() {
    	setContentView(R.layout.main_activity);
      	mUrlEditText = (EditText) findViewById(R.id.url);
      	mButton = (Button) findViewById(R.id.button1);
      	mButton.setOnClickListener(new View.OnClickListener() {
      	    @Override
      	    public void onClick(View v) {
      	    	downloadImage(v);
      	    }
      	});
	}

	/**
     * Called by the Android Activity framework when the user clicks
     * the "Find Address" button.
     *
     * @param view The view.
     */
    public void downloadImage(View view) {
        try {
            // Hide the keyboard.
            hideKeyboard(this,
                         mUrlEditText.getWindowToken());
            
            Uri url = getUrl();
            if(url ==null)
            	return;

            // Call the makeDownloadImageIntent() factory method to
            // create a new Intent to an Activity that can download an
            // image from the URL given by the user.  In this case
            // it's an Intent that's implemented by the
            // GenericImageActivity.
            Intent mIntent = makeDownloadImageIntent(url);
            
            // Start the Activity associated with the Intent, which
            // will download the image and then return the Uri for the
            // downloaded image file via the onActivityResult() hook
            // method.
            startActivityForResult(
            		mIntent, DOWNLOAD_IMAGE_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hook method called back by the Android Activity framework when
     * an Activity that's been launched exits, giving the requestCode
     * it was started with, the resultCode it returned, and any
     * additional data from it.
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        // Check if the started Activity completed successfully.
        // @@ TODO -- you fill in here, replacing true with the right
        // code.
        if (requestCode == DOWNLOAD_IMAGE_REQUEST) {
            // Check if the request code is what we're expecting.
            // @@ TODO -- you fill in here, replacing true with the
            // right code.
            if (resultCode == RESULT_OK) {
                // Call the makeGalleryIntent() factory method to
                // create an Intent that will launch the "Gallery" app
                // by passing in the path to the downloaded image
                // file.
            	Log.i(TAG, "Ready to display image...");
            	String imagePath = ((Uri) data.getExtras().get("IMAGE_URL")).toString();
            	Intent galleryIntent = makeGalleryIntent(imagePath);
                // @@ TODO -- you fill in here.
            	this.startActivity(galleryIntent);
                // Start the Gallery Activity.
                // @@ TODO -- you fill in here.
            }
        }
        // Check if the started Activity did not complete successfully
        // and inform the user a problem occurred when trying to
        // download contents at the given URL.
        // @@ TODO -- you fill in here, replacing true with the right
        // code.
        else if (true) {
        }
    }    

    /**
     * Factory method that returns an Intent for viewing the
     * downloaded image in the Gallery app.
     */
    private Intent makeGalleryIntent(String pathToImageFile) {
        // Create an intent that will start the Gallery app to view
        // the image.
    	// TODO -- you fill in here, replacing "false" with the proper
    	// code.
    	Intent galleryIntent = new Intent();
    	galleryIntent.setAction(Intent.ACTION_VIEW );
    	galleryIntent.setDataAndType(Uri.parse("file://" + pathToImageFile), "image/*");
    	//intent.setDataAndType(Uri.parse("file://" + pathToImageFile), "image/*");
        return galleryIntent;
   
    }

    /**
     * Factory method that returns an Intent for downloading an image.
     */
    private Intent makeDownloadImageIntent(Uri url) {
        // Create an intent that will download the image from the web.
    	Intent myIntent = new Intent(Intent.ACTION_WEB_SEARCH);
    	myIntent.setData(url);
    	myIntent.putExtra("Messenger", new Messenger(handler));
    	return myIntent;
    }

    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {
        Uri url = null;

        // Get the text the user typed in the edit text (if anything).
        url = Uri.parse(mUrlEditText.getText().toString());

        // If the user didn't provide a URL then use the default.
        String uri = url.toString();
        if (uri == null || uri.equals(""))
            url = mDefaultUrl;
        
        // Do a sanity check to ensure the URL is valid, popping up a
        // toast if the URL is invalid.
        if (checkUrlValidation(url))
            return url;
        else {
            Toast.makeText(this,
                           "Invalid URL",
                           Toast.LENGTH_SHORT).show();
            return null;
        } 
    }
    
    private boolean checkUrlValidation(Uri url){
    	String pattern = "^.*://.*\\.(png||jpg)$";
    	return url.toString().matches(pattern);
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public void hideKeyboard(Activity activity,
                             IBinder windowToken) {
        InputMethodManager mgr =
            (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }
}
