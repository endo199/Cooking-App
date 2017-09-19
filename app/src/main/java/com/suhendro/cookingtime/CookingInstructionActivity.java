package com.suhendro.cookingtime;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.suhendro.cookingtime.model.CookingStep;

import timber.log.Timber;

public class CookingInstructionActivity extends AppCompatActivity {
    private CookingStep[] mInstructions;
    private int mStep;

    DetailInstructionFragment mInstructionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking_instruction);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null) {
            mStep = savedInstanceState.getInt("currentStep");
            mInstructions = (CookingStep[]) savedInstanceState.getParcelableArray("list");
        } else {
            Intent intentFromCaller = getIntent();

            Parcelable[] tmp = intentFromCaller.getParcelableArrayExtra(DetailActivity.INSTRUCTION_KEY);
            mInstructions = new CookingStep[tmp.length];
            for (int i = 0; i < tmp.length; i++) {
                mInstructions[i] = (CookingStep) tmp[i];
            }

            mStep = intentFromCaller.getIntExtra(DetailActivity.INSTRUCTION_IDX, 0);
        }
        mInstructionFragment = (DetailInstructionFragment) getSupportFragmentManager().findFragmentById(R.id.frag_instruction);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mInstructionFragment.setInstructions(mInstructions, mStep);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("currentStep", mInstructionFragment.getCurrentStep());
        outState.putParcelableArray("list", mInstructions);
    }
}
