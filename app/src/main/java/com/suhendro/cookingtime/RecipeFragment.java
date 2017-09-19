package com.suhendro.cookingtime;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.suhendro.cookingtime.model.CookingStep;
import com.suhendro.cookingtime.model.Ingredient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Suhendro on 9/4/2017.
 */

public class RecipeFragment extends Fragment {

    private Ingredient[] ingredients;
    private CookingStep[] cookingInstruction;

    @BindView(R.id.elv_ingredient_instruction)
    ExpandableListView ingredientsAndInstructionListView;
    private DetailExpandableListAdapter adapter;

    private Unbinder mBinder;
    private CookingInstructionClickListener mListener;

    public interface CookingInstructionClickListener {
        void onClick(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (CookingInstructionClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RecipeFragment.CookingInstructionClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_fragment, container, false);
        mBinder = ButterKnife.bind(this, view);

        String[] titles = {
                getString(R.string.ingredients_label),
                getString(R.string.instruction_label)
        };

        adapter = new DetailExpandableListAdapter(this.getActivity().getApplicationContext(), titles);
        ingredientsAndInstructionListView.setAdapter(adapter);
        ingredientsAndInstructionListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                mListener.onClick(childPosition);
                Timber.d("XXX group %d at item %d", groupPosition, childPosition);
                return true;
            }
        });
        // expand Instruction group
        ingredientsAndInstructionListView.expandGroup(1);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    public void setIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
        adapter.setIngredients(this.ingredients);
        adapter.notifyDataSetChanged();
    }

    public void setCookingInstruction(CookingStep[] cookingInstruction) {
        this.cookingInstruction = cookingInstruction;
        adapter.setCookingInstructions(this.cookingInstruction);
        adapter.notifyDataSetChanged();
    }
}
