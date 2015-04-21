package vandy.mooc;

import android.net.Uri;
import android.util.Log;

/**
 * Created by wangshiyuan on 4/20/15.
 */
public class FilterImageActivity extends GenericImageActivity{

    protected final String TAG = getClass().getSimpleName();

    @Override
    public Uri doInBackGroundHook(Uri url) {
        Log.i(TAG, "filter Image...");
        return Utils.grayScaleFilter(this, url);
    }
}
