package com.veggedup.veggedup;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.veggedup.veggedup.data.VeggedupContract;
import com.veggedup.veggedup.data.VeggedupDbHelper;
import com.veggedup.veggedup.module.GlideApp;

/**
 * Implementation of App Widget functionality.
 */
public class LastFavouriteWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.last_favourite_widget);
        AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.last_favourite_image_view, remoteViews, appWidgetId);

        // Get last favourite
        Cursor lastFavourite = context.getContentResolver().query(VeggedupContract.Recipe.CONTENT_URI,
                null,
                VeggedupContract.Recipe.COLUMN_FAVOURITE + " IS NOT NULL",
                null,
                VeggedupContract.Recipe.COLUMN_RECIPE_ID + " DESC");

        assert lastFavourite != null;
        if (lastFavourite.getCount() > 0) {
            lastFavourite.moveToFirst();

            remoteViews.setTextViewText(R.id.last_favourite_title, lastFavourite.getString(lastFavourite.getColumnIndex(VeggedupContract.Recipe.COLUMN_TITLE)));

            // Display image
            GlideApp.with(context)
                    .asBitmap()
                    .load(lastFavourite.getString(lastFavourite.getColumnIndex(VeggedupContract.Recipe.COLUMN_IMAGE)))
                    .error(R.drawable.placeholder)
                    .into(appWidgetTarget);
            // Click handler
            Intent configIntent = new Intent(context, RecipeDetailActivity.class);
            configIntent.putExtra("RECIPE_ID", lastFavourite.getInt(lastFavourite.getColumnIndex(VeggedupContract.Recipe.COLUMN_RECIPE_ID)));
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.appwidget, configPendingIntent);
        } else {
            remoteViews.setTextViewText(R.id.last_favourite_title, context.getResources().getText(R.string.no_favourites_widget));
            Intent configIntent = new Intent(context, MainActivity.class);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.appwidget, configPendingIntent);
        }

        lastFavourite.close();

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

