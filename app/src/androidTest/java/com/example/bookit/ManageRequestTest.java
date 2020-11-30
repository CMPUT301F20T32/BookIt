package com.example.bookit;

import android.widget.SearchView;

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

public class ManageRequestTest {
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
    public void checkManageRequestFragment() {

        // Click on the Manage Requests Button
        onView(withId(R.id.ManageRequestsFragment)).perform(click());

        // check if the bottom bar is visible
        assertTrue(solo.searchText("My Books"));
        assertTrue(solo.searchText("Add Book"));
        assertTrue(solo.searchText("Manage Requests"));

        // check if the user is in "lender" mode
        assertTrue(solo.searchText("Lender"));

        // check if the ACCEPT and DECLINE buttons exist
        onView(withId(R.id.accept_request_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.decline_request_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));


    }

    @After
    public void tearDown() {

        solo.finishOpenedActivities();

    }
}
