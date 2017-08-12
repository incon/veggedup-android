package com.veggedup.veggedup;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.veggedup.veggedup.data.DataUtil;
import com.veggedup.veggedup.data.VeggedupContract;
import com.veggedup.veggedup.data.VeggedupDbHelper;


public class MainActivity extends AppCompatActivity implements RecipeListAdapter.RecipeListAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView mAdView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressBar mLoadingIndicator;

    private RecipeListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int VEGGEDUP_SYNC_LOADER = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Ads
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Swipe to refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                initiateRefresh();
            }
        });

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        RecyclerView recipeRecyclerView;

        // Set local attributes to corresponding views
        recipeRecyclerView = (RecyclerView) this.findViewById(R.id.recipe_list_view);

        // Best amount of columns based off the screen size
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int columns = (int) Math.ceil(dpWidth / 540);

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        recipeRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        // Create a DB helper (this will create the DB if run for the first time)
        VeggedupDbHelper dbHelper = new VeggedupDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();

        // Create an adapter for that cursor to display the data
        mAdapter = new RecipeListAdapter(this, null, this);

        // Link the adapter to the RecyclerView
        recipeRecyclerView.setAdapter(mAdapter);

        /*
         * Initialize the loader
         */
        getSupportLoaderManager().initLoader(VEGGEDUP_SYNC_LOADER, null, this);

    }

    @Override
    public void onClick(int recipeId) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(recipeId));
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "recipe");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Intent recipeDetailIntent = new Intent(MainActivity.this, RecipeDetailActivity.class);
        recipeDetailIntent.putExtra("RECIPE_ID", recipeId);
        startActivity(recipeDetailIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mRecipeData = null;

            @Override
            protected void onStartLoading() {
                /*
                 * When we initially begin loading in the background, we want to display the
                 * loading indicator to the user
                 */
                if (mRecipeData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mRecipeData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                mLoadingIndicator.setVisibility(View.VISIBLE);
                DataUtil.syncData(mDb);
                return getAllRecipes();
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor recipeData) {
                mRecipeData = recipeData;
                super.deliverResult(recipeData);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor recipeData) {
        /* When we finish loading, we want to hide the loading indicator from the user. */
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        // Get all recipes from the database and save in a cursor
        mAdapter.swapCursor(recipeData);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void initiateRefresh() {
        Log.i(LOG_TAG, "initiateRefresh");

        /**
         * Execute the background task, which uses {@link android.os.AsyncTask} to load the data.
         */
        new veggedupSyncTask().execute();
    }

    private void onRefreshComplete(String result) {
        Log.i(LOG_TAG, "onRefreshComplete");

        Log.i(LOG_TAG, result);

        // Get all recipes from the database and save in a cursor
        mAdapter.swapCursor(getAllRecipes());

        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private class veggedupSyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return String.valueOf(DataUtil.syncData(mDb));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Tell the Fragment that the refresh has completed
            onRefreshComplete(result);
        }

    }


    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getAllRecipes() {
        return mDb.query(
                VeggedupContract.Recipe.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                VeggedupContract.Recipe.COLUMN_RECIPE_ID
        );
    }
}
