package com.suhendro.cookingtime;

import android.app.DownloadManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ProgressBar;

import com.suhendro.cookingtime.model.Recipe;
import com.suhendro.cookingtime.util.CookingUtils;
import com.suhendro.cookingtime.widget.CookingWidgetProvider;
import com.suhendro.cookingtime.widget.SelectRecipeService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener, Callable {
    public static final String RECIPES = "recipes";
    public static final String SELECTED_RECIPE = "selectedRecipe";
    private static final String JSON_RESOURCE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    @BindView(R.id.rv_recipe_list)
    RecyclerView mRecipeList;

    @BindView(R.id.loading_indicator)
    ProgressBar mLoadingIndicator;

    private List<Recipe> mRecipes = new ArrayList<Recipe>();
    private RecipeAdapter mRecipeAdapter;
    private DownloadManager mDownloadManager;
    private long mEnqueueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);

        RecyclerView.LayoutManager layoutManager;
        if (isTablet) {
            int numOfSpan = 2;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Timber.d("XXX orientation changed into horizontal");
                numOfSpan = 3;
            }

            layoutManager = new GridLayoutManager(this, numOfSpan);
        } else {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }

        mRecipeList.setHasFixedSize(true);
        mRecipeList.setLayoutManager(layoutManager);

        mRecipeAdapter = new RecipeAdapter(mRecipes, this);
        mRecipeList.setAdapter(mRecipeAdapter);

        Timber.plant(new Timber.DebugTree());

        if (savedInstanceState != null) {
            List<Recipe> recipes = savedInstanceState.getParcelableArrayList(RECIPES);
            if (recipes != null) {
                this.mRecipes.addAll(recipes);
                this.mRecipeAdapter.notifyDataSetChanged();
            }
        } else {
            // download recipes from network
            showLoading();
            Uri uri = Uri.parse(JSON_RESOURCE_URL);
            CookingUtils.downloadRecipe(this, uri, this);
        }
    }

    private void showLoading() {
        Timber.d("XXX loading indicator");
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecipeList.setVisibility(View.INVISIBLE);
    }

    private void showData() {
        mRecipeList.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onClickItem(int position) {
        Recipe recipe = mRecipes.get(position);
        Intent intentThatCallDetail = new Intent(this, DetailActivity.class);
        intentThatCallDetail.putExtra(SELECTED_RECIPE, recipe);

        startActivity(intentThatCallDetail);
    }

    @Override
    public void onLongClickItem(Recipe recipe) {
        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefEditor.putLong("recipeId", recipe.getId());
        prefEditor.putString("recipeTitle", recipe.getName());
        prefEditor.commit();

        Snackbar.make(mRecipeList, getString(R.string.assign_recipe_to_widget), Snackbar.LENGTH_SHORT).show();

        SelectRecipeService.chooseRecipe(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPES, (ArrayList<? extends Parcelable>) this.mRecipes);
    }

    @Override
    public Void call() throws Exception {
        List<Recipe> recipes = CookingUtils.getRecipes(this);

        if (recipes != null) {
            mRecipes.addAll(recipes);
            mRecipeAdapter.notifyDataSetChanged();
        }

        showData();

        return null;
    }
}
