package com.example.bookit;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * This class is used for Intent testing for the books of the Lender
 */
public class LenderBooksFragment {
    private Solo solo;

    @Rule
    public ActivityScenarioRule<MainActivity> rule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {

        rule.getScenario().onActivity(activity -> solo =
                new Solo(InstrumentationRegistry.getInstrumentation(), activity));
    }

    @Test
    public void checkLenderBooks() {
        // change status from "borrower" to "lender"
        onView(withId(R.id.menu_toggle)).perform(click());
        //assertTrue(solo.searchText("lender"));

        onView(withId(R.id.available_recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        checkBottomBar();

        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.requested_recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        checkBottomBar();

        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.accepted_recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        checkBottomBar();

        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.borrowed_recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        checkBottomBar();

    }

    private void checkBottomBar() {
        onView(withId(R.id.MyBooksFragment)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.AddBookFragment)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.ManageRequestsFragment)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @After
    public void tearDown() {

        solo.finishOpenedActivities();

    }
}
