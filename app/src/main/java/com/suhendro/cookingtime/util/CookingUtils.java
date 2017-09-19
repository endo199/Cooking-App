package com.suhendro.cookingtime.util;

import android.content.Context;

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

/**
 * Created by Suhendro on 9/3/2017.
 */

public class CookingUtils {
    public static List<Recipe> getRecipes(Context context) {
        final File file = new File(context.getFilesDir(), "baking.json");
        try {
            FileInputStream inputStream = context.openFileInput("baking.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<Recipe>>(){}.getType();
            List<Recipe> recipes = gson.fromJson(reader, collectionType);

            return recipes;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
