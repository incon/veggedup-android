package com.veggedup.veggedup;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.veggedup.veggedup.data.VeggedupContract;
import com.veggedup.veggedup.data.VeggedupDbHelper;
import com.veggedup.veggedup.module.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private Cursor recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VeggedupDbHelper dbHelper = new VeggedupDbHelper(this);
        mDb = dbHelper.getReadableDatabase();

        Intent intent = getIntent();
        if (intent.hasExtra("RECIPE_ID")){
            int recipeId = intent.getIntExtra("RECIPE_ID", 0);
            recipe = getRecipe(recipeId);
            recipe.moveToFirst();
        }

        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        String recipeTitle = recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_TITLE));
        String recipeType = recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_TYPE));
        String recipeImageURL = recipe.getString(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_IMAGE));

        // Get Elements
        TextView detailRecipeTitle = (TextView) findViewById(R.id.detailRecipeTittle);
        TextView detailRecipeType = (TextView) findViewById(R.id.detailRecipeType);
        ImageView detailRecipeImage = (ImageView) findViewById(R.id.recipeDetailImage);

        // Set Views
        detailRecipeTitle.setText(recipeTitle);
        detailRecipeType.setText(recipeType);

        // Display image
        GlideApp.with(this)
                .load(recipeImageURL)
                .placeholder(R.mipmap.test1)
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
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
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
