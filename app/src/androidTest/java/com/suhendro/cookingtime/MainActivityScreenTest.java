package com.suhendro.cookingtime;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by Suhendro on 9/16/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void checkRecipeIsDisplayed() {
        // check in main activity if there is a recipe with name Nutella Pie
        onView(withText("Nutella Pie")).check(matches(isDisplayed()));
    }

    @Test
    public void clickRecipeGridViewItem_OpenDetailActivity() {

        // click the first recipe
        onView(withId(R.id.rv_recipe_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withText("Ingredients:")).check(matches(isDisplayed()));

    }
}
