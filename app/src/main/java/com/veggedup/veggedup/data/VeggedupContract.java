package com.veggedup.veggedup.data;

import android.provider.BaseColumns;

public class VeggedupContract {

    public static final class Recipe implements BaseColumns {
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
