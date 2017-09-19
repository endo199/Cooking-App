package com.suhendro.cookingtime;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suhendro.cookingtime.model.CookingStep;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Suhendro on 9/4/2017.
 */

public class CookingStepAdapter extends RecyclerView.Adapter<CookingStepAdapter.CookingStepViewHolder> {
    private final CookingStep[] mSteps;
    private RecipeFragment.CookingInstructionClickListener mClickListenerActivity;

    public CookingStepAdapter(CookingStep[] steps, FragmentActivity activity) {
        this.mSteps = steps;
        this.mClickListenerActivity = (RecipeFragment.CookingInstructionClickListener) activity;
    }

    @Override
    public CookingStepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cooking_step_list_item, parent, false);

        return new CookingStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CookingStepViewHolder holder, int position) {
        holder.bind(mSteps[position]);
    }

    @Override
    public int getItemCount() {
        return (mSteps == null) ? 0 : mSteps.length;
    }

    class CookingStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_cooking_step)
        TextView mCookingStep;

        public CookingStepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void bind(CookingStep step) {
            mCookingStep.setText(step.getShortDescription());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickListenerActivity.onClick(position);
        }
    }
}
