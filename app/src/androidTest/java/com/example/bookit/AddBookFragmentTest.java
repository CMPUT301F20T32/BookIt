package com.example.bookit;

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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

/**
 * This class is used for intent testing of the adding Book fragments
 */
public class AddBookFragmentTest {
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
    public void checkAddBook() {
        // Check if the user is in lender mode
        assertTrue(solo.searchText("Lender"));

        // Click on Add Book button
        onView(withId(R.id.AddBookFragment)).perform(click());

        // Check if the bottom bar still exists
        assertTrue(solo.searchText("My Books"));
        assertTrue(solo.searchText("Add Book"));
        assertTrue(solo.searchText("Manage Requests"));

        // check if all the book field headers are visible
        assertTrue(solo.searchText("Book Title"));
        assertTrue(solo.searchText("Author"));
        assertTrue(solo.searchText("ISBN"));
        assertTrue(solo.searchText("Comments"));

        //check if the scan and add button are visible
        onView(withId(R.id.scanButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.addButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //enter book field to add new book
        solo.enterText((EditText) solo.getView(R.id.editTextBookTitle), "Book for Intent Test");
        solo.enterText((EditText) solo.getView(R.id.editTextAuthor), "Phi Long");
        solo.enterText((EditText) solo.getView(R.id.editTextISBN), "1234567890");
        solo.enterText((EditText) solo.getView(R.id.editTextComments), "This is a book created in intent testing.");

    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
