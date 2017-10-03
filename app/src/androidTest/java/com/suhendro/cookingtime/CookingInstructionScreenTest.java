package com.suhendro.cookingtime;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.suhendro.cookingtime.model.CookingStep;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by Suhendro on 9/18/2017.
 */

@RunWith(AndroidJUnit4.class)
public class CookingInstructionScreenTest {
    @Rule
    public ActivityTestRule<FullscreenActivity> mActivityTestRule =
            new ActivityTestRule<FullscreenActivity>(
                    FullscreenActivity.class,
                    true,
                    false
            );

    private Intent mIntent;
    private CookingStep[] listInstruction;

    @Before
    public void init() {
        listInstruction = new CookingStep[3];
        CookingStep step1 = new CookingStep();
        step1.setId(1);
        step1.setShortDescription("step 1");
        step1.setDescription("description 1");
        listInstruction[0] = step1;

        CookingStep step2 = new CookingStep();
        step2.setId(2);
        step2.setShortDescription("step 2");
        step2.setDescription("description 2");
        listInstruction[1] = step2;

        CookingStep step3 = new CookingStep();
        step3.setId(1);
        step3.setShortDescription("step 3");
        step3.setDescription("description 3");
        listInstruction[2] = step3;

        mIntent = new Intent();
        mIntent.putExtra(DetailActivity.INSTRUCTION_KEY, listInstruction);
    }

    @Test
    public void viewSelectedDefaultOrFirstStep() {
        mActivityTestRule.launchActivity(mIntent);
        onView(withId(R.id.tv_cooking_description)).check(matches(withText("description 1")));

        onView(withId(R.id.btn_prev_instruction)).check(matches(not(isEnabled())));
        onView(withId(R.id.btn_next_instruction)).check(matches(isEnabled()));
    }

    @Test
    public void viewMiddleStep() {
        int step = listInstruction.length / 2;

        mIntent.putExtra(DetailActivity.INSTRUCTION_IDX, step);
        mActivityTestRule.launchActivity(mIntent);

        CookingStep cookingStep = listInstruction[step];
        onView(withId(R.id.tv_cooking_description)).check(matches(withText(cookingStep.getDescription())));

        onView(withId(R.id.btn_prev_instruction)).check(matches(isEnabled()));
        onView(withId(R.id.btn_next_instruction)).check(matches(isEnabled()));
    }

    @Test
    public void viewLastStep() {
        int lastStep = listInstruction.length - 1;

        mIntent.putExtra(DetailActivity.INSTRUCTION_IDX, lastStep);
        mActivityTestRule.launchActivity(mIntent);

        CookingStep cookingStep = listInstruction[lastStep];
        onView(withId(R.id.tv_cooking_description)).check(matches(withText(cookingStep.getDescription())));

        onView(withId(R.id.btn_next_instruction)).check(matches(not(isEnabled())));
        onView(withId(R.id.btn_prev_instruction)).check(matches(isEnabled()));
    }
}
