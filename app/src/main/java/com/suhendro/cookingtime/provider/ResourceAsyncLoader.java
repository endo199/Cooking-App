package com.suhendro.cookingtime.provider;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Suhendro on 9/21/2017.
 */

public class ResourceAsyncLoader extends AsyncTaskLoader {
    public ResourceAsyncLoader(Context context) {
        super(context);
    }

    @Override
    public Object loadInBackground() {
        return null;
    }
}
