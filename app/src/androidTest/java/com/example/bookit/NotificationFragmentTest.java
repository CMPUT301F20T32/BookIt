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

public class NotificationFragmentTest {
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
    public void checkNotificationFragment() {
        // change status from "lender" to "borrower"
        onView(withId(R.id.notifications_tab)).perform(click());

        // check if it is currently in Notifications tab
        assertTrue(solo.searchText("My Notifications"));
    }

    @After
    public void tearDown() {

        solo.finishOpenedActivities();

    }
}
