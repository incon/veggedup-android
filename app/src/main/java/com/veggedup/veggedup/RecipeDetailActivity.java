package com.veggedup.veggedup;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.veggedup.veggedup.data.VeggedupContract;
import com.veggedup.veggedup.data.VeggedupDbHelper;
import com.veggedup.veggedup.module.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private Cursor recipe;
    private boolean favourite;
    private FloatingActionButton fab;
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Lock portrait for mobile
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Ads
        AdView mAdView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        VeggedupDbHelper dbHelper = new VeggedupDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

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
                c.put(VeggedupContract.Recipe.COLUMN_FAVOURITE, favourite ? 1 : 0);
                mDb.update(VeggedupContract.Recipe.TABLE_NAME, c, "recipeId=?", new String[]{String.valueOf(recipeId)});
                if (favourite) {
                    fab.setImageResource(R.drawable.favourited);
                } else {
                    fab.setImageResource(R.drawable.unfavourited);
                }
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
        favourite = recipe.getInt(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_FAVOURITE)) == 1;

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
        return mDb.query(
                VeggedupContract.Recipe.TABLE_NAME,
                null,
                VeggedupContract.Recipe.COLUMN_RECIPE_ID + " = " + recipeId,
                null,
                null,
                null,
                VeggedupContract.Recipe.COLUMN_RECIPE_ID
        );
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle = new Bundle();
                bundle.putBoolean("REFRESH_CURSOR", true);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
