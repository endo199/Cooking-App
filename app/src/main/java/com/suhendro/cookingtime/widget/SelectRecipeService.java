package com.suhendro.cookingtime.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;

import com.suhendro.cookingtime.R;
import com.suhendro.cookingtime.model.Recipe;

import timber.log.Timber;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SelectRecipeService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CHOOSE_RECIPE = "com.suhendro.cookingtime.widget.action.CHOOSE_RECIPE";
    private static final String EXTRA_RECIPE = "com.suhendro.cookingtime.widget.extra.RECIPE";

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
            Timber.d("XXX intent action %s", action);
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
