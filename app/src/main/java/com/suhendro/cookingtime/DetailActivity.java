package com.suhendro.cookingtime;

import android.content.Intent;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.suhendro.cookingtime.model.CookingStep;
import com.suhendro.cookingtime.model.Recipe;

import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements RecipeFragment.CookingInstructionClickListener {
    public static final String INSTRUCTION_KEY = "instructions";
    public static final String INSTRUCTION_IDX = "step";

    private Recipe mRecipe;
    private int mInstructionStep;
    // true if has master-detail pane (two pane)
    private boolean isTwoPane = false;
    private RecipeFragment recipeFragment;
    private DetailInstructionFragment instructionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable("recipe");
            mInstructionStep = savedInstanceState.getInt("step", 0);

            recipeFragment = (RecipeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "recipeFragment");
        } else {

            Intent intentFromCaller = getIntent();
            if (!intentFromCaller.hasExtra(MainActivity.SELECTED_RECIPE)) {
                Timber.d("XXX no selected recipe in intent");
                Snackbar.make(findViewById(R.id.detail_activity), "No Recipe is selected", Snackbar.LENGTH_SHORT).show();
                return;
            }
            mRecipe = intentFromCaller.getParcelableExtra(MainActivity.SELECTED_RECIPE);

            recipeFragment = new RecipeFragment();
        }

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(mRecipe.getName());

        instructionFragment = (DetailInstructionFragment) getSupportFragmentManager().findFragmentById(R.id.frag_instruction);

        recipeFragment = (RecipeFragment) getSupportFragmentManager().findFragmentById(R.id.frag_recipe);
        recipeFragment.setIngredients(mRecipe.getIngredients());
        recipeFragment.setCookingInstruction(mRecipe.getSteps());

        isTwoPane = instructionFragment != null;
        if(isTwoPane) {
            instructionFragment.setInstructions(mRecipe.getSteps());
            instructionFragment.setStep(mInstructionStep);
        }
    }

    @Override
    public void onClick(int position) {
        if(isTwoPane) {
            mInstructionStep = position;
            instructionFragment.setStep(mInstructionStep);
        } else {
            Intent intent = new Intent(this, FullscreenActivity.class);
            intent.putExtra(INSTRUCTION_KEY, mRecipe.getSteps());
            intent.putExtra(INSTRUCTION_IDX, position);

            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("recipe", mRecipe);
        outState.putInt("step", mInstructionStep);

        getSupportFragmentManager().putFragment(outState, "recipeFragment", recipeFragment);
    }
}
