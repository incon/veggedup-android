package com.veggedup.veggedup;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.veggedup.veggedup.data.VeggedupDbHelper;
import com.veggedup.veggedup.module.GlideApp;

/**
 * Implementation of App Widget functionality.
 */
public class LastFavouriteWidget extends AppWidgetProvider {

    private AppWidgetTarget appWidgetTarget;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = "Last recipe";
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.last_favourite_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Create a DB helper (this will create the DB if run for the first time)
        VeggedupDbHelper dbHelper = new VeggedupDbHelper(context);
        AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.last_favourite_image_view, views, appWidgetId);

        // Display image
        GlideApp.with(context)
                .asBitmap()
                .load("https://veggedup.com/img/test1.png")
                .error(R.drawable.placeholder)
                .into(appWidgetTarget);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get last favourite

        // Click handler
        Intent configIntent = new Intent(context, RecipeDetailActivity.class);
        configIntent.putExtra("RECIPE_ID", 2);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget, configPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
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

