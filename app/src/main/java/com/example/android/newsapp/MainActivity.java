package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private ProgressBar mProgressBar;
    private TextView mEmptyView;
    private ImageView mSearchButton;
    private TextInputEditText mSearchText;
    private NewsAdapter mAdapter;
    private String mQueryText;
    private ListView mListView;
    private LoaderManager mLoaderManager;
    private static final String THE_GUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search?";
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int NEWS_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoaderManager = getLoaderManager();

        if (savedInstanceState != null) {
            mLoaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        mListView = (ListView) findViewById(R.id.list);
        mListView.setEmptyView(mEmptyView);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        mListView.setAdapter(mAdapter);

        mSearchButton = (ImageView) findViewById(R.id.search_icon);
        mSearchText = (TextInputEditText) findViewById(R.id.search_field);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchQuery(mSearchText.getText().toString());
            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    launchQuery(mSearchText.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
    }


    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        Uri baseUri = Uri.parse(THE_GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", mQueryText);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "test");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        mAdapter.clear();

        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setText(R.string.empty_view_text);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    private void launchQuery(String searchText) {
        mSearchText.clearFocus();
        mAdapter.clear();
        mEmptyView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        if (isOnline()) {
            mQueryText = searchText;

            if (!searchText.isEmpty() && searchText != null) {

                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (mLoaderManager.getLoader(NEWS_LOADER_ID) != null) {
                    mLoaderManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                } else {
                    mLoaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
                }
            } else {
                Toast.makeText(this, "You need to introduce some text to search", Toast.LENGTH_LONG).show();
            }
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.no_connection);
        }
    }
}
