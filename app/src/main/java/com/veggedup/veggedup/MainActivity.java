package com.veggedup.veggedup;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.veggedup.veggedup.data.DataUtil;
import com.veggedup.veggedup.data.VeggedupContract;


public class MainActivity extends AppCompatActivity implements RecipeListAdapter.RecipeListAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private FirebaseAnalytics mFirebaseAnalytics;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressBar mLoadingIndicator;

    private RecyclerView recipeRecyclerView;
    private TextView emptyView;
    private TextView noFavouritesView;

    private boolean favouritesOnly = false;

    private RecipeListAdapter mAdapter;
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int VEGGEDUP_SYNC_LOADER = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lock portrait for mobile
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Ads
        AdView mAdView = (AdView) findViewById(R.id.adView);
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

        // Set local attributes to corresponding views
        recipeRecyclerView = (RecyclerView) findViewById(R.id.recipe_list_view);

        // Empty views
        emptyView = (TextView) findViewById(R.id.empty_view);
        noFavouritesView = (TextView) findViewById(R.id.no_favourites_view);

        // Best amount of columns based off the screen size
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int columns = (int) Math.ceil(dpWidth / 540);

        // Set layout for the RecyclerView
        recipeRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        // Create an adapter for that cursor to display the data
        mAdapter = new RecipeListAdapter(this, null, this);

        // Link the adapter to the RecyclerView
        recipeRecyclerView.setAdapter(mAdapter);

        /*
         * Initialize the loader on first load
         */
        Intent intent = getIntent();
        if (intent.hasExtra("REFRESH_CURSOR")) {
            mAdapter.swapCursor(getAllRecipes());
        } else {
            mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
            getSupportLoaderManager().initLoader(VEGGEDUP_SYNC_LOADER, null, this);
        }

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
                DataUtil.syncData(getBaseContext());
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


        // Update list set
        if ((recipeData != null) && (recipeData.getCount() > 0)) {
            recipeRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            mAdapter.swapCursor(recipeData);
        } else {
            recipeRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
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
        // Get all recipes from the database and save in a cursor
        Cursor updatedCursor = getAllRecipes();

        mAdapter.swapCursor(updatedCursor);

        // Update list set
        if ((updatedCursor != null) && (updatedCursor.getCount() > 0)) {
            recipeRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            recipeRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }

        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private class veggedupSyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return String.valueOf(DataUtil.syncData(getBaseContext()));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Tell the Fragment that the refresh has completed
            onRefreshComplete(result);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection


        switch (item.getItemId()) {
            case R.id.menu_favourite:
                favouritesOnly = !favouritesOnly;
                if (favouritesOnly) {
                    Cursor favouriteRecipes = getFavouriteRecipes();
                    item.setIcon(R.drawable.favourited_white);
                    item.setTitle(R.string.show_all);
                    if ((favouriteRecipes != null) && (favouriteRecipes.getCount() > 0)) {
                        recipeRecyclerView.setVisibility(View.VISIBLE);
                        noFavouritesView.setVisibility(View.GONE);
                        mAdapter.swapCursor(favouriteRecipes);
                    } else {
                        recipeRecyclerView.setVisibility(View.GONE);
                        noFavouritesView.setVisibility(View.VISIBLE);
                    }
                } else {
                    recipeRecyclerView.setVisibility(View.VISIBLE);
                    noFavouritesView.setVisibility(View.GONE);
                    item.setIcon(R.drawable.unfavourited_white);
                    item.setTitle(R.string.filter_by_favourites);
                    mAdapter.swapCursor(getAllRecipes());
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Cursor getAllRecipes() {
        try {
            return getContentResolver().query(VeggedupContract.Recipe.CONTENT_URI,
                    null,
                    null,
                    null,
                    VeggedupContract.Recipe.COLUMN_RECIPE_ID + " DESC");

        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }

    private Cursor getFavouriteRecipes() {
        try {
            return getContentResolver().query(VeggedupContract.Recipe.CONTENT_URI,
                    null,
                    VeggedupContract.Recipe.COLUMN_FAVOURITE + " IS NOT NULL",
                    null,
                    VeggedupContract.Recipe.COLUMN_RECIPE_ID + " DESC");

        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }
}
