package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Vicuko on 10/8/17.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null || mUrl == "") {
            return null;
        }
        return QueryUtils.fetchNewsData(mUrl);
    }
}
