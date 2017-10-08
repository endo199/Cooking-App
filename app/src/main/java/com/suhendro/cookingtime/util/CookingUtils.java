package com.suhendro.cookingtime.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suhendro.cookingtime.R;
import com.suhendro.cookingtime.model.Recipe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Callable;

import timber.log.Timber;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Suhendro on 9/3/2017.
 */

public class CookingUtils {
    private static long mEnqueueId;

    /**
     * Get recipes in JSON format
     *
     * @param context
     * @return recipe in JSON
     */
    public static List<Recipe> getRecipes(Context context) {
        final File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "baking.json");
        // /storage/emulated/0/Android/data/com.suhendro.cookingtime/files/Documents/baking.json
        Timber.d("XXX file at Utils path %s", file.getAbsolutePath());

        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<Recipe>>(){}.getType();
            List<Recipe> recipes = gson.fromJson(reader, collectionType);

            return recipes;

        } catch (FileNotFoundException e) {
            Timber.e("XXX error file not found %s", e.getMessage());
            e.printStackTrace();

            Timber.d("XXX reset file availability");
            SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            prefEditor.putBoolean("json_available", false);
            prefEditor.commit();
        }
        return null;
    }

    /**
     * Download recipe using download manager and call callable after download finish. Use runtime
     * BroadcastReceiver because it only used once, so that it will be maintained in one place
     *
     * @param context
     * @param uri URI of resource that want to be downloaded
     * @param callable method that will be called after download finish
     */
    public static void downloadRecipe(Context context, Uri uri, final Callable callable) {
        boolean isJsonAvailable = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("json_available", false);

        try {
            if (isJsonAvailable) {
                callable.call();
                return;
            }
        } catch (Exception e) {
            Timber.e("XXX error callable %s", e.getMessage());
        }

        final DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
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

                            Timber.d("XXX download finish");
                            try {
                                callable.call();
                            } catch (Exception e) {
                                Timber.e("XXX error callable in receiver: %s", e.getMessage());
                            }
                        }
                    }
                }
            }
        };

        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // initiate download baking resource
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOCUMENTS, "baking.json");
        request.setTitle(context.getString(R.string.downloading_resources));

        mEnqueueId = mDownloadManager.enqueue(request);
    }
}
