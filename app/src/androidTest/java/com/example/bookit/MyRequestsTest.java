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
import static org.junit.Assert.assertTrue;

/**
 * This class is used for intent testing of the requested books by the borrower
 */
public class MyRequestsTest {
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
    public void checkMyRequests() {
        // change status from "lender" to "borrower"
        onView(withId(R.id.menu_toggle)).perform(click());

        // Click on the My Requests Button
        onView(withId(R.id.MyRequestsFragment)).perform(click());

        // check if the bottom bar is visible
        assertTrue(solo.searchText("Borrowed"));
        assertTrue(solo.searchText("My Requests"));
        assertTrue(solo.searchText("Search"));

        // check if the user is in "borrower" mode
        assertTrue(solo.searchText("borrower"));

        solo.waitForView(R.id.my_requests_tab_layout);
        onView(withId(R.id.my_requests_tab_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.my_requests_pager)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.pending_requests_borrower_recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        assertTrue(solo.searchText("Pending"));
        assertTrue(solo.searchText("Accepted"));

        // swipe left
        onView(withId(R.id.my_requests_pager)).perform(swipeLeft());
        onView(withId(R.id.accepted_requests_borrower_recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }

    @After
    public void tearDown() {

        solo.finishOpenedActivities();

    }
}
