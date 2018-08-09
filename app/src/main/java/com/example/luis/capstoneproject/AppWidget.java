package com.example.luis.capstoneproject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        SharedPreferences prefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        final String headlineUrl = prefs.getString("widget_headline_url", "");

        Thread thread = new Thread(){
            public void run(){
                AppDatabase database = AppDatabase.getAppDatabase(context);

                Headline headline = database.headlineDAO().getHeadline(headlineUrl);

                if(headline != null) {

                    views.setTextViewText(R.id.appwidget_text, headline.getTitle());

                    String description = headline.getDescription();

                    if(description.isEmpty())
                        views.setTextViewText(R.id.appwidget_content, context.getString(R.string.no_desc_provided));
                    else
                        views.setTextViewText(R.id.appwidget_content, headline.getDescription());

                    // Instruct the widget manager to update the widget
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }
        };
        thread.start();
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

