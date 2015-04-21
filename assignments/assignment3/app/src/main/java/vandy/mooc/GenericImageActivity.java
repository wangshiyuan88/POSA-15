package vandy.mooc;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * Created by wangshiyuan on 4/20/15.
 */
public abstract class GenericImageActivity extends LifecycleLoggingActivity {

    protected final String TAG = getClass().getSimpleName();
    protected Uri url = null;
    protected FragmentManager retainedFragmentManager = getFragmentManager();
    private GenericImageFragment genericImageFragment = null;
    public abstract Uri doInBackGroundHook(Uri url);

    protected void addToRetainedFragmentManager(){
        retainedFragmentManager.beginTransaction().add(genericImageFragment, TAG).commit();
    }

    protected Uri getUrl(){
        return url;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the URL associated with the Intent data.
        url = (Uri) getIntent().getData();

        retainedFragmentManager = getFragmentManager();
        genericImageFragment = (GenericImageFragment) retainedFragmentManager.findFragmentByTag(TAG);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (genericImageFragment == null) {
            genericImageFragment = new GenericImageFragment();
            Bundle args = new Bundle();
            args.putString("URL", url.toString());
            genericImageFragment.setArguments(args);
            addToRetainedFragmentManager();
        }
    }

}
