package com.veggedup.veggedup.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        //create a list of fake recipes
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(VeggedupContract.Recipe.COLUMN_RECIPE_ID, 1);
        cv.put(VeggedupContract.Recipe.COLUMN_TITLE, "Mexican Chilli");
        cv.put(VeggedupContract.Recipe.COLUMN_IMAGE, "https://veggedup.com/img/test1.png");
        cv.put(VeggedupContract.Recipe.COLUMN_DURATION, "1hr 15m");
        cv.put(VeggedupContract.Recipe.COLUMN_TYPE, "Main");
        cv.put(VeggedupContract.Recipe.COLUMN_DESCRIPTION, "Description 1");
        cv.put(VeggedupContract.Recipe.COLUMN_SERVERS, 2);
        cv.put(VeggedupContract.Recipe.COLUMN_INGREDIENTS_COUNT, 2);
        cv.put(VeggedupContract.Recipe.COLUMN_STEPS_COUNT, 2);
        cv.put(VeggedupContract.Recipe.COLUMN_INGREDIENTS, "[{\n" +
                "        \"qty\": \"20\",\n" +
                "        \"uom\": \"grams\",\n" +
                "        \"name\": \"Chili Flakes\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"qty\": \"1\",\n" +
                "        \"uom\": \"liter\",\n" +
                "        \"name\": \"Grounded Cumin\"\n" +
                "      }\n" +
                "    ]");
        cv.put(VeggedupContract.Recipe.COLUMN_STEPS, "[\n" +
                "      \"Mix Chili Flakes and Groun Cumin\",\n" +
                "      \"Another demo step\"\n" +
                "    ]");
        list.add(cv);

        cv = new ContentValues();
        cv.put(VeggedupContract.Recipe.COLUMN_RECIPE_ID, 2);
        cv.put(VeggedupContract.Recipe.COLUMN_TITLE, "Coconut Ice Cream");
        cv.put(VeggedupContract.Recipe.COLUMN_IMAGE, "https://veggedup.com/img/test2.png");
        cv.put(VeggedupContract.Recipe.COLUMN_DURATION, "1hr 15m");
        cv.put(VeggedupContract.Recipe.COLUMN_TYPE, "Dessert");
        cv.put(VeggedupContract.Recipe.COLUMN_DESCRIPTION, "Description 1");
        cv.put(VeggedupContract.Recipe.COLUMN_SERVERS, 2);
        cv.put(VeggedupContract.Recipe.COLUMN_INGREDIENTS_COUNT, 2);
        cv.put(VeggedupContract.Recipe.COLUMN_STEPS_COUNT, 2);
        cv.put(VeggedupContract.Recipe.COLUMN_INGREDIENTS, "[{\n" +
                "        \"qty\": \"20\",\n" +
                "        \"uom\": \"grams\",\n" +
                "        \"name\": \"Chili Flakes\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"qty\": \"1\",\n" +
                "        \"uom\": \"liter\",\n" +
                "        \"name\": \"Grounded Cumin\"\n" +
                "      }\n" +
                "    ]");
        cv.put(VeggedupContract.Recipe.COLUMN_STEPS, "[\n" +
                "      \"Mix Chili Flakes and Groun Cumin\",\n" +
                "      \"Another demo step\"\n" +
                "    ]");
        list.add(cv);

        //insert all recipes in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (VeggedupContract.Recipe.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(VeggedupContract.Recipe.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}