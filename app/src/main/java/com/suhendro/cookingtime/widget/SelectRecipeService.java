package com.suhendro.cookingtime.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.suhendro.cookingtime.R;

import timber.log.Timber;

public class SelectRecipeService extends IntentService {
    private static final String ACTION_CHOOSE_RECIPE = "com.suhendro.cookingtime.widget.action.CHOOSE_RECIPE";

    public SelectRecipeService() {
        super("SelectRecipeService");
    }

    public static void chooseRecipe(Context context) {
        Intent intent = new Intent(context, SelectRecipeService.class);
        intent.setAction(ACTION_CHOOSE_RECIPE);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if(ACTION_CHOOSE_RECIPE.equals(action)) {
                handleActionSelectedRecipe();
            }
        }
    }

    private void handleActionSelectedRecipe() {
        Timber.d("XXX calling update widget");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, CookingWidgetProvider.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_gv_ingredients);

        CookingWidgetProvider.updateRecipeWidget(this, appWidgetManager, ids);
    }
}
