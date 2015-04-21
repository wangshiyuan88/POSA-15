package vandy.mooc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();
    public final static int DOWNLOADACTIVITY = 0;
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
    	super.onCreate(savedInstanceState);
        // Get the URL associated with the Intent data.
    	Uri urlToDownload = (Uri) getIntent().getData();
        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.
    	Thread downloadThread = new Thread(new DownloadRunnable(urlToDownload, this));
    	Log.i(TAG,"Launching other thread...");
    	Log.i(TAG,"Download Activity: Am I in main thread? "+ (Looper.myLooper() == Looper.getMainLooper()));
    	downloadThread.start();
    	Log.i(TAG,"After Launching other thread..");
        // @@ TODO -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.
    }
    
    
    private class DownloadRunnable implements Runnable{
    	Uri urlToDownload;
    	Activity context;
    	public DownloadRunnable(Uri urlToDownload, Activity context){
    		this.urlToDownload = urlToDownload;
    		this.context = context;
    	}
		@Override
		public void run() {
			Log.i(TAG, "Start to download image...");
			Log.i(TAG,"Download Runnable: Am I in main thread? "+ (Looper.myLooper() == Looper.getMainLooper()));
	    	Uri urlForImage = DownloadUtils.downloadImage(context, urlToDownload);
	    	Log.i(TAG, "Download completed");
	    	Intent returnIntent = new Intent();
	    	returnIntent.putExtra("DOWNLOAD_IMAGE_REQUEST", 1);
	    	returnIntent.putExtra("IMAGE_URL", urlForImage);
	    	context.setResult(RESULT_OK, returnIntent);
	        Log.i(TAG, "Create Messagea");
	        Messenger msgr = (Messenger) getIntent().getExtras().get("Messenger");
	        Message msg = Message.obtain(null, DOWNLOADACTIVITY, this);
	        
	        try {
	        	msg.obj = context;
	        	msgr.send(msg);
	        	Log.i(TAG, "Sending message to MainActivity");
	        }
	        catch (android.os.RemoteException e1) {
	          Log.w(TAG, "Exception sending message", e1);
	        }
			
		}
    	
    }
    
}
