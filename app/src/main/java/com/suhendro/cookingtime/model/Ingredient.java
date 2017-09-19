package com.suhendro.cookingtime.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Suhendro on 9/3/2017.
 */

public class Ingredient implements Parcelable {
    @SerializedName("quantity")
    private float qty;
    private String measure;
    @SerializedName("ingredient")
    private String name;

    public Ingredient() {}
    private Ingredient(Parcel in) {
        qty = in.readFloat();
        measure = in.readString();
        name = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(qty);
        parcel.writeString(measure);
        parcel.writeString(name);
    }

    @Override
    public String toString() {
        return this.name+" ("+this.qty+" "+this.measure+")";
    }
}
