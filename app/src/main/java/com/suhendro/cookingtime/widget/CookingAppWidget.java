package com.suhendro.cookingtime.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.suhendro.cookingtime.R;
import com.suhendro.cookingtime.model.Recipe;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class CookingAppWidget extends AppWidgetProvider {
    private long mRecipeId;
    private Recipe recipe;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            int[] ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_ID);
            recipe = intent.getParcelableExtra("recipe");
            onUpdate(context, AppWidgetManager.getInstance(context), ids);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Recipe recipe) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cooking_app_widget);
        Timber.d("XXX recipe is null: "+(recipe == null));

        if(recipe != null) {
            Intent intent = new Intent(context, CookingWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            intent.putExtra(CookingRemoteViewsFactory.INGREDIENTS_KEY, recipe.getIngredients());
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Construct the RemoteViews object
            RemoteViews title = new RemoteViews(context.getPackageName(), R.layout.widget_title);
            title.setTextViewText(R.id.tv_recipe_title, recipe.getName());

            views.setRemoteAdapter(R.id.lst_ingredients, intent);
            views.addView(R.id.lst_ingredients, title);
            views.setViewVisibility(R.id.lst_ingredients, View.VISIBLE);
            views.setViewVisibility(R.id.empty_view, View.GONE);
        } else {
            views.setViewVisibility(R.id.lst_ingredients, View.INVISIBLE);
            views.setViewVisibility(R.id.empty_view, View.VISIBLE);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
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

