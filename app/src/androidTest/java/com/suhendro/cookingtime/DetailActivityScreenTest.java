package com.suhendro.cookingtime;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ExpandableListView;

import com.suhendro.cookingtime.model.CookingStep;
import com.suhendro.cookingtime.model.Ingredient;
import com.suhendro.cookingtime.model.Recipe;
import com.suhendro.cookingtime.util.CookingUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

/**
 * Created by Suhendro on 9/17/2017.
 */

@RunWith(AndroidJUnit4.class)
public class DetailActivityScreenTest {
    Recipe mRecipe;
    private DetailInstructionFragment instruction;

    @Rule
    public ActivityTestRule<DetailActivity> mActivityTestRule = new ActivityTestRule<DetailActivity>(DetailActivity.class, true, false);

    @Rule
    public IntentsTestRule<DetailActivity> mIntentTestRule = new IntentsTestRule<DetailActivity>(DetailActivity.class, true, false);

    @Before
    public void init() {
        mRecipe = new Recipe();

        Ingredient[] ingredients = new Ingredient[2];
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("Lada");
        ingredient1.setQty(1);
        ingredient1.setMeasure("TBLSPN");
        ingredients[0] = ingredient1;

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Lada");
        ingredient2.setQty(1);
        ingredient2.setMeasure("TBLSPN");
        ingredients[1] = ingredient2;

        mRecipe.setIngredients(ingredients);

        CookingStep[] steps = new CookingStep[3];
        CookingStep step1 = new CookingStep();
        step1.setId(1);
        step1.setShortDescription("short description 1");
        step1.setDescription("description 1");
        steps[0] = step1;

        CookingStep step2 = new CookingStep();
        step2.setId(2);
        step2.setShortDescription("short description 2");
        step2.setDescription("description 2");
        steps[1] = step2;

        CookingStep step3 = new CookingStep();
        step3.setId(3);
        step3.setShortDescription("short description 3");
        step3.setDescription("description 3");
        steps[2] = step3;

        mRecipe.setSteps(steps);


    }

    @Test
    public void initView() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.SELECTED_RECIPE, mRecipe);
        mActivityTestRule.launchActivity(intent);

        ExpandableListView expandableListView = (ExpandableListView) mActivityTestRule.getActivity().findViewById(R.id.elv_ingredient_instruction);
        boolean isInstructionExpanded = expandableListView.isGroupExpanded(1);

        assertEquals(true, isInstructionExpanded);
    }

    @Test
    public void selectFirstInstruction() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.SELECTED_RECIPE, mRecipe);
        mIntentTestRule.launchActivity(intent);

        onData(allOf(is(instanceOf(CookingStep.class))))
                .inAdapterView(withId(R.id.elv_ingredient_instruction))
                .atPosition(0)
                .perform(click());

        instruction = (DetailInstructionFragment) mIntentTestRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_instruction);

        if(instruction == null) {
            // single pane
            intended(allOf(
                    hasComponent(CookingInstructionActivity.class.getName()),
                    hasExtra(DetailActivity.INSTRUCTION_IDX, 0)
            ));
        } else {
            // two pane
            String text = mRecipe.getSteps()[0].getDescription();
            onView(withId(R.id.tv_cooking_description)).check(matches(withText(text)));
        }
    }

    @Test
    public void selectMiddleInstruction() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.SELECTED_RECIPE, mRecipe);
        mIntentTestRule.launchActivity(intent);

        int step = mRecipe.getSteps().length / 2;

        onData(allOf(is(instanceOf(CookingStep.class))))
                .inAdapterView(withId(R.id.elv_ingredient_instruction))
                .atPosition(step)
                .perform(click());

        intended(allOf(
                hasComponent(CookingInstructionActivity.class.getName()),
                hasExtra(DetailActivity.INSTRUCTION_IDX, step)
        ));
    }

    @Test
    public void selectLastInstruction() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.SELECTED_RECIPE, mRecipe);
        mIntentTestRule.launchActivity(intent);

        int step = mRecipe.getSteps().length - 1;

        onData(allOf(is(instanceOf(CookingStep.class))))
                .inAdapterView(withId(R.id.elv_ingredient_instruction))
                .atPosition(step)
                .perform(click());

        intended(allOf(
                hasComponent(CookingInstructionActivity.class.getName()),
                hasExtra(DetailActivity.INSTRUCTION_IDX, step)
        ));
    }
}
