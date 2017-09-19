package com.suhendro.cookingtime.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.suhendro.cookingtime.R;
import com.suhendro.cookingtime.model.Ingredient;
import com.suhendro.cookingtime.model.Recipe;
import com.suhendro.cookingtime.util.CookingUtils;

import java.io.File;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Suhendro on 9/16/2017.
 */

public class CookingWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CookingRemoteViewsFactory(getApplicationContext());
    }
}

class CookingRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private Recipe mRecipe;
    private Ingredient[] mIngredients;

    public CookingRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        long recipeId = PreferenceManager.getDefaultSharedPreferences(mContext).getLong("recipeId", -1);
        if(recipeId > 0) {
            Timber.d("XXX data set changed %d", (mIngredients == null) ? 0 : mIngredients.length);
            List<Recipe> recipes = CookingUtils.getRecipes(mContext);
            for (Recipe recipe : recipes) {
                if (recipe.getId() == recipeId) {
                    mRecipe = recipe;
                    mIngredients = mRecipe.getIngredients();

                    break;
                }
            }
        } else {
            Timber.d("XXX no recipe is selected");
            mRecipe = null;
            mIngredients = null;
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return (mIngredients == null) ? 0 : mIngredients.length;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        Ingredient ingredient = mIngredients[i];

        RemoteViews views= new RemoteViews(mContext.getPackageName(), R.layout.ingredient_list_item);
        views.setTextViewText(R.id.tv_ingredient_name, ingredient.getName());
        views.setTextViewText(R.id.tv_ingredient_qty, String.valueOf(ingredient.getQty()));
        views.setTextViewText(R.id.tv_ingredient_measure, ingredient.getMeasure());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
