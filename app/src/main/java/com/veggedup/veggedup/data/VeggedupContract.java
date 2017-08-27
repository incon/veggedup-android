package com.veggedup.veggedup.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class VeggedupContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.veggedup.veggedup";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_RECIPES = "recipes";

    public static final class Recipe implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        public static final String TABLE_NAME = "recipes";

        public static final String COLUMN_RECIPE_ID = "recipeId";
        public static final String COLUMN_FAVOURITE = "favourite";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_SERVERS = "serves";
        public static final String COLUMN_INGREDIENTS_COUNT = "ingredients_count";
        public static final String COLUMN_STEPS_COUNT = "steps_count";
        public static final String COLUMN_INGREDIENTS = "ingredients";
        public static final String COLUMN_STEPS = "steps";
    }

}
