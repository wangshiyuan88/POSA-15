package vandy.mooc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
     * values that uniquely identifies the request to download an
     * image.
     */
    private static final int DOWNLOAD_IMAGE_REQUEST = 1;
    private static final int FILTER_IMAGE_REQUEST = 2;
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
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
    	super.onCreate(savedInstanceState);
        // Set the default layout.
        // Cache the EditText that holds the urls entered by the user
        // (if any).
    	initUI();
    }


	private void initUI() {
        setContentView(R.layout.main_activity);
      	mUrlEditText = (EditText)(findViewById(R.id.url));
      	mButton = (Button)(findViewById(R.id.button1));
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
            // DownloadImageActivity.
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
    private void filterImage(String url){
        try {
            Intent filterImageIntent = makeFilterImageIntent(url);
            startActivityForResult(filterImageIntent, FILTER_IMAGE_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Intent makeFilterImageIntent(String url){
        // Create an intent that will filter the image.
        Intent mIntent = new Intent(this, FilterImageActivity.class);
        mIntent.setData( Uri.parse(url) );
        return mIntent;
    }
    /**
     * Hook method called back by the Android Activity framework when
     * an Activity that's been launched exits, giving the requestCode
     * it was started with, the resultCode it returned, and any
     * additional data from it.
     */
    @Override
    public void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        // Check if the started Activity completed successfully.
        if (requestCode == DOWNLOAD_IMAGE_REQUEST) {
            // Check if the request code is what we're expecting.
            if (resultCode == RESULT_OK) {
                // Call the filterImage to do the filtering
            	Log.i(TAG, "Ready to filter image...");
            	String imagePath = ((Uri) data.getExtras().get("URL")).toString();
            	filterImage(imagePath);

            }else if(requestCode == RESULT_CANCELED){
                Log.e(TAG, "Failed to download image...");
            }
        }
        // Check if the started Activity did not complete successfully
        // and inform the user a problem occurred when trying to
        // download contents at the given URL.
        else if (requestCode == FILTER_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK){
                Log.i(TAG, "Ready to display image...");
                String imagePath = ((Uri) data.getExtras().get("URL")).toString();
                Intent galleryIntent = makeGalleryIntent(imagePath);
                startActivity(galleryIntent);
            }
            else if(requestCode == RESULT_CANCELED){
                Log.e(TAG, "Failed to filer image...");
            }
        }
    }    

    /**
     * Factory method that returns an Intent for viewing the
     * downloaded image in the Gallery app.
     */
    private Intent makeGalleryIntent(String pathToImageFile) {
        // Create an intent that will start the Gallery app to view
        // the image.
    	Intent galleryIntent = new Intent();
    	galleryIntent.setAction(Intent.ACTION_VIEW );
    	galleryIntent.setDataAndType(Uri.parse("file://" + pathToImageFile), "image/*");
        return galleryIntent;
   
    }

    /**
     * Factory method that returns an Intent for downloading an image.
     */
    private Intent makeDownloadImageIntent(Uri url) {
        // Create an intent that will download the image from the web.
    	Intent myIntent = new Intent(Intent.ACTION_WEB_SEARCH);
    	myIntent.setData(url);
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
        return URLUtil.isValidUrl(url.toString());
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
