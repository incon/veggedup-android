package com.veggedup.veggedup.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.veggedup.veggedup.data.VeggedupContract.*;

public class VeggedupDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "veggedup.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public VeggedupDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold recipe data
        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + Recipe.TABLE_NAME + " (" +
                Recipe._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Recipe.COLUMN_RECIPE_ID + " INTEGER UNIQUE NOT NULL, " +
                Recipe.COLUMN_FAVOURITE + " DATETIME DEFAULT NULL, " +
                Recipe.COLUMN_TITLE + " TEXT NOT NULL, " +
                Recipe.COLUMN_IMAGE + " TEXT NOT NULL, " +
                Recipe.COLUMN_DURATION + " TEXT NOT NULL, " +
                Recipe.COLUMN_TYPE + " TEXT NOT NULL, " +
                Recipe.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                Recipe.COLUMN_SERVERS + " INTEGER NOT NULL, " +
                Recipe.COLUMN_INGREDIENTS_COUNT + " INTEGER NOT NULL, " +
                Recipe.COLUMN_STEPS_COUNT + " INTEGER NOT NULL, " +
                Recipe.COLUMN_INGREDIENTS + " TEXT NOT NULL, " +
                Recipe.COLUMN_STEPS + " TEXT NOT NULL " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_RECIPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Recipe.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}