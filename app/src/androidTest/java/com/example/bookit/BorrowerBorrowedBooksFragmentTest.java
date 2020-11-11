package com.example.bookit;

import android.view.View;
import android.widget.EditText;

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

/**
 * This class is used for intent testing of the borrowed books of the borrower
 */
public class BorrowerBorrowedBooksFragmentTest {
    private Solo solo;

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {

        rule.getScenario().onActivity(activity -> solo =
                new Solo(InstrumentationRegistry.getInstrumentation(), activity));


        // enter the login information for the user
        solo.enterText((EditText) solo.getView(R.id.editTextTextEmailAddress), "howard@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.editTextTextPassword), "1234567");
    }

    @Test
    public void checkBorrowerBorrowedBooksFragment() {
        // click on the login button
        solo.clickOnButton("Login");

        // wait for MainActivity to open
        solo.waitForActivity(MainActivity.class, 20000);
        solo.waitForView(R.id.action_settings);
        //solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // check if the bottom bar is visible
//        assertTrue(solo.searchText("Borrowed"));
//        assertTrue(solo.searchText("My Requests"));
//        assertTrue(solo.searchText("Search"));

        // check if the user is in "borrower" mode
//        assertTrue(solo.searchText("Borrower"));

        onView(withId(R.id.borrowed_borrower_recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // check if action_settings is visible
        //onView(withId(R.id.action_settings)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }

    @After
    public void tearDown() {

        // log the user out
        if (solo.getView(R.id.action_settings).getVisibility() == View.VISIBLE) {
            onView(withId(R.id.action_settings)).perform(click());
            onView(withId(R.id.logoutButton)).perform(click());
        }

        solo.finishOpenedActivities();

    }
}
