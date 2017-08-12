package com.veggedup.veggedup;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.veggedup.veggedup.data.VeggedupContract;
import com.veggedup.veggedup.data.VeggedupDbHelper;

public class RecipeDetailActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VeggedupDbHelper dbHelper = new VeggedupDbHelper(this);
        mDb = dbHelper.getReadableDatabase();

        Cursor recipe = null;
        Intent intent = getIntent();
        if (intent.hasExtra("RECIPE_ID")){
            int recipeId = intent.getIntExtra("RECIPE_ID", 0);
            recipe = getRecipe(recipeId);
            recipe.moveToFirst();
            Log.v("RECIPE_ID", String.valueOf(recipeId));
        }
        
        int recipeId = recipe.getInt(recipe.getColumnIndex(VeggedupContract.Recipe.COLUMN_RECIPE_ID));

        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

}
