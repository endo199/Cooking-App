package com.suhendro.cookingtime.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Suhendro on 9/3/2017.
 */

public class Recipe implements Parcelable {
    private long id;
    private String name;
    private int servings;
    @SerializedName("image")
    private String imageUrl;
    private Ingredient[] ingredients;
    private CookingStep[] steps;

    public Recipe() {}
    private Recipe(Parcel in) {
        id = in.readLong();
        name = in.readString();
        servings = in.readInt();
        imageUrl = in.readString();
        ingredients = in.createTypedArray(Ingredient.CREATOR);
        steps = in.createTypedArray(CookingStep.CREATOR);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    public CookingStep[] getSteps() {
        return steps;
    }

    public void setSteps(CookingStep[] steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "Recipe of "+this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.id);
        parcel.writeString(this.name);
        parcel.writeInt(this.servings);
        parcel.writeString(this.imageUrl);

        parcel.writeTypedArray(this.ingredients, flags);
        parcel.writeTypedArray(this.steps, flags);
    }
}
