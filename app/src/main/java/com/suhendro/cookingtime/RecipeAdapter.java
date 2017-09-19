package com.suhendro.cookingtime;

import android.app.Activity;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.suhendro.cookingtime.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Suhendro on 9/3/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> mRecipes;
    private RecipeClickListener mClickListener;
    private Context mContext;

    public interface RecipeClickListener {
        void onClickItem(int position);
        void onLongClickItem(Recipe recipe);
    }

    public RecipeAdapter(List<Recipe> recipes, RecipeClickListener listener) {
        mRecipes = recipes;
        mClickListener = listener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        boolean shouldAttachToParentImmediately = false;

        View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_list_item, parent, shouldAttachToParentImmediately);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.bind(mRecipes.get(position));
    }

    @Override
    public int getItemCount() {
        return (mRecipes == null) ? 0 : mRecipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.img_recipe)
        ImageView mRecipeImg;

        @BindView(R.id.tv_recipe_title)
        TextView mRecipeTitle;

        @BindView(R.id.tv_serving)
        TextView mServing;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            ButterKnife.bind(this, itemView);
        }

        public void bind(Recipe recipe) {
            mRecipeTitle.setText(recipe.getName());
            mServing.setText(recipe.getServings()+"");
            if(recipe.getImageUrl() != null && recipe.getImageUrl().length() > 0) {
                Picasso.with(mRecipeImg.getContext()).load(recipe.getImageUrl()).into(mRecipeImg);
            } else {
                mRecipeImg.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickListener.onClickItem(position);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            Recipe recipe = mRecipes.get(position);
            mClickListener.onLongClickItem(recipe);

            return true;
        }
    }
}
