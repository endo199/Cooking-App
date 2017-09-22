package com.suhendro.cookingtime.util;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suhendro.cookingtime.model.Recipe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Suhendro on 9/3/2017.
 */

public class CookingUtils {
    public static List<Recipe> getRecipes(Context context) {
        final File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "baking.json");
        // /storage/emulated/0/Android/data/com.suhendro.cookingtime/files/Documents/baking.json
        Timber.d("XXX file at Utils path %s", file.getAbsolutePath());

        try {
//            FileInputStream inputStream = context.openFileInput("baking.json");
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<Recipe>>(){}.getType();
            List<Recipe> recipes = gson.fromJson(reader, collectionType);

            Timber.d("XXX hasil proses gson %s", (recipes == null));

            return recipes;

        } catch (FileNotFoundException e) {
            Timber.e("XXX error file not found %s", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Timber.e("XXX IOException %s", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
