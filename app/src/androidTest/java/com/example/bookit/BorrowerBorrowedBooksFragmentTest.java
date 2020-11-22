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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

/**
 * This class is used for intent testing of the borrowed books of the borrower
 */
public class BorrowerBorrowedBooksFragmentTest {
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
    public void checkBorrowerBorrowedBooksFragment() {

        // check if the bottom bar is visible
        assertTrue(solo.searchText("Borrowed"));
        assertTrue(solo.searchText("My Requests"));
        assertTrue(solo.searchText("Search"));

        // check if the user is in "borrower" mode
        assertTrue(solo.searchText("Borrower"));

        onView(withId(R.id.borrowed_borrower_recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // check if action_settings is visible
        onView(withId(R.id.action_settings)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }

    @After
    public void tearDown() {

        solo.finishOpenedActivities();

    }
}