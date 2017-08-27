package com.veggedup.veggedup.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataUtil {

    public static Boolean syncData(Context context) {

        // Create a DB helper (this will create the DB if run for the first time)
        VeggedupDbHelper dbHelper = new VeggedupDbHelper(context);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Boolean success;

        try {
            String json = DataUtil.getJSON();
            DataUtil.insertData(db, json);
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }

        // Return a new random list of cheeses
        return success;
    }

    public static String getJSON() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://veggedup.com/api/recipes")
                .build();
        String results = null;

        Response response = client.newCall(request).execute();
        results = response.body().string();

        return results;
    }

    public static void insertData(SQLiteDatabase db, String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);

        //create a list of fake recipes
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv;


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject recipe = jsonArray.getJSONObject(i);

            cv = new ContentValues();
            cv.put(VeggedupContract.Recipe.COLUMN_RECIPE_ID, recipe.getInt("id"));
            cv.put(VeggedupContract.Recipe.COLUMN_TITLE, recipe.getString("title"));
            cv.put(VeggedupContract.Recipe.COLUMN_IMAGE, recipe.getString("image"));
            cv.put(VeggedupContract.Recipe.COLUMN_DURATION, recipe.getString("duration"));
            cv.put(VeggedupContract.Recipe.COLUMN_TYPE, recipe.getString("type"));
            cv.put(VeggedupContract.Recipe.COLUMN_DESCRIPTION, recipe.getString("description"));
            cv.put(VeggedupContract.Recipe.COLUMN_SERVERS, recipe.getInt("serves"));
            cv.put(VeggedupContract.Recipe.COLUMN_INGREDIENTS_COUNT, recipe.getInt("ingredients_count"));
            cv.put(VeggedupContract.Recipe.COLUMN_STEPS_COUNT, recipe.getInt("steps_count"));
            cv.put(VeggedupContract.Recipe.COLUMN_INGREDIENTS, recipe.getString("ingredients"));
            cv.put(VeggedupContract.Recipe.COLUMN_STEPS, recipe.getString("steps"));
            list.add(cv);

        }

        try {
            db.beginTransaction();
            //go through the list and add one by one
            for (ContentValues c : list) {
                int id = (int) db.insertWithOnConflict(VeggedupContract.Recipe.TABLE_NAME, null, c, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                    db.update(VeggedupContract.Recipe.TABLE_NAME, c, "recipeId=?", new String[]{String.valueOf(c.getAsInteger(VeggedupContract.Recipe.COLUMN_RECIPE_ID))});
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //too bad :(
        } finally {
            db.endTransaction();
        }

    }
}