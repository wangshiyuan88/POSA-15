package vandy.mooc;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by wangshiyuan on 4/20/15.
 */
public class GenericImageFragment extends Fragment{

    GenericImageActivity mActivity;
    Uri url;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (GenericImageActivity)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri url = Uri.parse(getArguments().getString("URL"));
        new GenericImageAsyncTask().execute(url);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    private class GenericImageAsyncTask extends AsyncTask<Uri, Void,  Uri> {

        @Override
        protected Uri doInBackground(Uri... urls) {
            return mActivity.doInBackGroundHook(urls[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Uri url) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("URL", url);
            mActivity.setResult(Activity.RESULT_OK, returnIntent);
            mActivity.finish();
        }

        @Override
        protected void onCancelled(Uri url) {
            super.onCancelled(url);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }

}
