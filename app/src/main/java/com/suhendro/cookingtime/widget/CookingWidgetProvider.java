package com.suhendro.cookingtime.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.suhendro.cookingtime.R;
import com.suhendro.cookingtime.model.Recipe;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class CookingWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cooking_widget_provider);
        views.setEmptyView(R.id.widget_gv_ingredients, R.id.empty_view);

        String title = PreferenceManager.getDefaultSharedPreferences(context).getString("recipeTitle", "");
        views.setTextViewText(R.id.widget_tv_recipe_title, title);

        // set adapter for grid view
        Intent intent = new Intent(context, CookingWidgetService.class);
        views.setRemoteAdapter(R.id.widget_gv_ingredients, intent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SelectRecipeService.chooseRecipe(context);
    }

    public static void updateRecipeWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, widgetId);
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

