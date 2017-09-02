package com.veggedup.veggedup;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.veggedup.veggedup.data.VeggedupContract;
import com.veggedup.veggedup.module.GlideApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RecipeDetailActivity extends AppCompatActivity {

    private Cursor recipe;
    private boolean favourite;
    private FloatingActionButton fab;
    private int recipeId;
    private FirebaseAnalytics mFirebaseAnalytics;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Lock portrait for mobile
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Ads
        AdView mAdView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        if (intent.hasExtra("RECIPE_ID")) {
            recipeId = intent.getIntExtra("RECIPE_ID", 0);
            recipe = getRecipe(recipeId);
            recipe.moveToFirst();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fab = (FloatingActionButton) findViewById(R.id.fabFavourited);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favourite = !favourite;
                ContentValues c = new ContentValues();
                c.put(VeggedupContract.Recipe.COLUMN_FAVOURITE, favourite ? getDateTime() : null);

                String stringId = Integer.toString(recipeId);
                Uri uri = VeggedupContract.Recipe.CONTENT_URI.buildUpon().appendPath(stringId).build();
                getContentResolver().update(uri, c, null, null);

                Bundle bundle = new Bundle();
                if (favourite) {
                    fab.setImageResource(R.drawable.favourited);
                    fab.setContentDescription(getResources().getString(R.string.favourited));
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(recipeId));
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "favorited_recipe");
                } else {
                    fab.setImageResource(R.drawable.unfavourited);
                    fab.setContentDescription(getResources().getString(R.string.unfavourtied));
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(recipeId));
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "unfavorited_recipe");
                }
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                updateLastFavouriteWidget();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // setupViewPager(viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new IngredientsFragment(), "Ingredients");
        adapter.addFragment(new StepsFragment(), "Steps");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Get details
        String title = recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_TITLE));
        String type = recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_TYPE));
        // String description = recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_DESCRIPTION));
        String time = recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_DURATION));
        String ingredientsCount = String.valueOf(recipe.getInt(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_INGREDIENTS_COUNT)));
        String stepsCount = String.valueOf(recipe.getInt(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_STEPS_COUNT)));
        String serves = String.valueOf(recipe.getInt(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_SERVERS)));
        String recipeImageURL = recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_IMAGE));
        favourite = recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_FAVOURITE)) != null;

        // Get Elements
        TextView detailRecipeTitle = (TextView) findViewById(R.id.detailRecipeTitle);
        TextView detailRecipeType = (TextView) findViewById(R.id.detailRecipeType);
        // TextView detailRecipeDescription = (TextView) findViewById(R.id.detailRecipeDescription);
        TextView detailRecipeTime = (TextView) findViewById(R.id.detailRecipeTime);
        TextView detailRecipeIngredientsCount = (TextView) findViewById(R.id.detailRecipeIngredientsCount);
        TextView detailRecipeStepsCount = (TextView) findViewById(R.id.detailRecipeStepsCount);
        TextView detailRecipeServes = (TextView) findViewById(R.id.detailRecipeServes);
        ImageView detailRecipeImage = (ImageView) findViewById(R.id.recipeDetailImage);

        // Set Views
        detailRecipeTitle.setText(title);
        detailRecipeType.setText(type);
        // detailRecipeDescription.setText(description);
        detailRecipeTime.setText(time);
        detailRecipeIngredientsCount.setText(ingredientsCount);
        detailRecipeStepsCount.setText(stepsCount);
        detailRecipeServes.setText(serves);

        // Fab
        if (favourite) {
            fab.setImageResource(R.drawable.favourited);
        }

        // Display image
        GlideApp.with(this)
                .load(recipeImageURL)
                .error(R.drawable.placeholder)
                .into(detailRecipeImage);
    }

    private Cursor getRecipe(int recipeId) {
        try {
            return getContentResolver().query(VeggedupContract.Recipe.CONTENT_URI,
                    null,
                    VeggedupContract.Recipe.COLUMN_RECIPE_ID + " = " + recipeId,
                    null,
                    VeggedupContract.Recipe.COLUMN_RECIPE_ID + " DESC");

        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }

    private Cursor update(int recipeId) {
        try {
            return getContentResolver().query(VeggedupContract.Recipe.CONTENT_URI,
                    null,
                    VeggedupContract.Recipe.COLUMN_RECIPE_ID + " = " + recipeId,
                    null,
                    VeggedupContract.Recipe.COLUMN_RECIPE_ID + " DESC");

        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }

    private Cursor getLastFavourite() {
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

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        return dateFormat.format(date);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    public String getStepsJSON() {
        return recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_STEPS));
    }

    public String getIngredientsJSON() {
        return recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_INGREDIENTS));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("DO_NOT_RELOAD", true);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateLastFavouriteWidget() {
        Cursor lastFavourite = getLastFavourite();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());
        RemoteViews remoteViews = new RemoteViews(getBaseContext().getPackageName(), R.layout.last_favourite_widget);
        ComponentName lastFavouriteWidget = new ComponentName(getBaseContext(), LastFavouriteWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(lastFavouriteWidget);

        if (lastFavourite.getCount() > 0 && appWidgetIds.length > 0) {
            lastFavourite.moveToFirst();
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(getBaseContext(), R.id.last_favourite_image_view, remoteViews, appWidgetIds);
            GlideApp.with(getBaseContext())
                    .asBitmap()
                    .load(lastFavourite.getString(lastFavourite.getColumnIndex(VeggedupContract.Recipe.COLUMN_IMAGE)))
                    .error(R.drawable.placeholder)
                    .into(appWidgetTarget);
            remoteViews.setTextViewText(R.id.last_favourite_title, lastFavourite.getString(lastFavourite.getColumnIndex(VeggedupContract.Recipe.COLUMN_TITLE)));
            Intent configIntent = new Intent(getBaseContext(), RecipeDetailActivity.class);
            configIntent.putExtra("RECIPE_ID", lastFavourite.getInt(lastFavourite.getColumnIndex(VeggedupContract.Recipe.COLUMN_RECIPE_ID)));
            PendingIntent configPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.appwidget, configPendingIntent);
        } else {
            remoteViews.setTextViewText(R.id.last_favourite_title, getResources().getText(R.string.no_favourites_widget));
            remoteViews.setImageViewBitmap(R.id.last_favourite_image_view, null);
            Intent configIntent = new Intent(getBaseContext(), MainActivity.class);
            PendingIntent configPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.appwidget, configPendingIntent);
        }

        AppWidgetManager.getInstance(getBaseContext()).updateAppWidget(lastFavouriteWidget, remoteViews);
    }

}
