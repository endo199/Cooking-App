package com.suhendro.cookingtime;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.suhendro.cookingtime.model.CookingStep;

import timber.log.Timber;

public class CookingInstructionActivity extends AppCompatActivity {
    private CookingStep[] mInstructions;
    private int mStep;

    DetailInstructionFragment mInstructionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_cooking_instruction);

//        ActionBar bar = getSupportActionBar();
//        bar.setDisplayHomeAsUpEnabled(true);

//        if(savedInstanceState != null) {
//            mStep = savedInstanceState.getInt("currentStep");
//            mInstructions = (CookingStep[]) savedInstanceState.getParcelableArray("list");
//        } else {
            Intent intentFromCaller = getIntent();

            Parcelable[] tmp = intentFromCaller.getParcelableArrayExtra(DetailActivity.INSTRUCTION_KEY);
            mInstructions = new CookingStep[tmp.length];
            for (int i = 0; i < tmp.length; i++) {
                mInstructions[i] = (CookingStep) tmp[i];
            }

            mStep = intentFromCaller.getIntExtra(DetailActivity.INSTRUCTION_IDX, 0);
//        }
        mInstructionFragment = (DetailInstructionFragment) getSupportFragmentManager().findFragmentById(R.id.frag_instruction);
        mInstructionFragment.setInstructions(mInstructions, mStep);
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putInt("currentStep", mInstructionFragment.getCurrentStep());
//        outState.putParcelableArray("list", mInstructions);
//    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        Timber.d("XXX window focus changed %s", hasFocus);
//        super.onWindowFocusChanged(hasFocus);
//
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//        );
//    }
}
