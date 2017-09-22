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

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {
    public static final String RECIPES = "recipes";
    public static final String SELECTED_RECIPE = "selectedRecipe";

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
            int orientationScrn = StaggeredGridLayoutManager.VERTICAL;

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Timber.d("XXX orientation changed into horizontal");
                numOfSpan = 3;
                orientationScrn = StaggeredGridLayoutManager.HORIZONTAL;
            }

            layoutManager = new StaggeredGridLayoutManager(numOfSpan, orientationScrn);
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
            downloadRecipes();
        }
    }

    private void showLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecipeList.setVisibility(View.INVISIBLE);
    }

    private void showData() {
        mRecipeList.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.GONE);
    }

    private void downloadRecipes() {
        showLoading();
//        final File file = new File(this.getFilesDir(), "baking.json");
//        Timber.d("XXX json path %s", file.getAbsolutePath());
//        Timber.d("XXX file is exist %s", file.exists());
//        Timber.d("XXX used space %d", file.getTotalSpace());

        boolean isJsonAvailable = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("json_available", false);

        if (isJsonAvailable) {
            Timber.d("File json already exist, don't need to download");
            List<Recipe> recipes = CookingUtils.getRecipes(this);

            Timber.d("XXX recipes list is null %s", (recipes == null));
            if (recipes != null) {
                mRecipes.addAll(recipes);
                mRecipeAdapter.notifyDataSetChanged();

                showData();
                return;
            }
        }

        // initiate download manager
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(mEnqueueId);

                    Cursor cursor = mDownloadManager.query(query);
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();

                        int colStatusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(colStatusIdx)) {
                            String fileUri = cursor.getString(
                                    cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                            );
                            // file:///storage/emulated/0/Android/data/com.suhendro.cookingtime/files/Documents/baking.json
                            Timber.d("XXX downloaded URI %s", fileUri);

                            SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                            prefEditor.putBoolean("json_available", true);
                            prefEditor.commit();

                            Timber.d("XXX copyign the file");

                            List<Recipe> recipes = CookingUtils.getRecipes(context);

                            if (recipes != null) {
                                mRecipes.addAll(recipes);
                                mRecipeAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
                    Timber.d("XXX user click notification");
                }

                showData();
            }
        };

        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));

        File file = new File(this.getFilesDir(), "baking.json");

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json"));
//        request.setDestinationUri(Uri.parse(file.toURI().toString()));
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOCUMENTS, "baking.json");
        request.setTitle(getString(R.string.downloading_resources));

        mEnqueueId = mDownloadManager.enqueue(request);
    }

//    private void downloadRecipes() {
//        final Context context = this;
//        final File file = new File(context.getFilesDir(), "baking.json");
//        if(file.exists()) {
//            Timber.d("File json already exist, don't need to download");
//
//            List<Recipe> recipes = CookingUtils.getRecipes(context);
//            if(recipes == null)
//                return;
//
//            Timber.i("list of recipes %s", recipes.toString());
//
//            mRecipes.addAll(recipes);
//            mRecipeAdapter.notifyDataSetChanged();
//
//            showData();
//
//            return;
//        }
//
//        AsyncTask<Void, Void, String> download = new AsyncTask<Void, Void, String>() {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                showLoading();
//            }
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                Timber.i("Downloading resources");
//                try {
//                    URL source = new URL("https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json");
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(source.openStream()));
//
//                    StringBuilder strBuilder = new StringBuilder();
//                    String input;
//                    while((input = reader.readLine()) != null) {
//                        strBuilder.append(input);
//                    }
//
//                    return strBuilder.toString();
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String jsonString) {
//                // write to internal storage
//
//                FileOutputStream outputStream = null;
//                try {
//                    outputStream = context.openFileOutput("baking.json", Context.MODE_PRIVATE);
//                    outputStream.write(jsonString.getBytes());
//                    outputStream.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if(outputStream != null)
//                            outputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                List<Recipe> recipes = CookingUtils.getRecipes(context);
//                mRecipes.addAll(recipes);
//                mRecipeAdapter.notifyDataSetChanged();
//
//                showData();
//            }
//        };
//        download.execute();
//    }

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

        Snackbar.make(mRecipeList, "Recipe has been assigned to widget", Snackbar.LENGTH_SHORT).show();

        Timber.d("XXX choose recipe id %d", recipe.getId());

        SelectRecipeService.chooseRecipe(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPES, (ArrayList<? extends Parcelable>) this.mRecipes);
    }
}
