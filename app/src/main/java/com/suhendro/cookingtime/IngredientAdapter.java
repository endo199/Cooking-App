package com.suhendro.cookingtime;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suhendro.cookingtime.model.Ingredient;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Suhendro on 9/4/2017.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private Ingredient[] mListIngredient;

    public IngredientAdapter(Ingredient[] lstIngrediedt) {
        mListIngredient = lstIngrediedt;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean attachToRoot = false;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_list_item, parent, attachToRoot);

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        holder.bind(mListIngredient[position]);
    }

    @Override
    public int getItemCount() {
        return (mListIngredient == null) ? 0 : mListIngredient.length;
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ingredient_name)
        TextView mName;
        @BindView(R.id.tv_ingredient_qty)
        TextView mQty;
        @BindView(R.id.tv_ingredient_measure)
        TextView mMeasure;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Ingredient ingredient) {
            mName.setText(ingredient.getName());
            mQty.setText(ingredient.getQty()+"");
            mMeasure.setText(ingredient.getMeasure());
        }
    }
}
