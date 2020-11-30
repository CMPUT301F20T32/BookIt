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

/**
 * This class is used for intent testing of the SearchFragment
 */

public class SearchFragmentTest {
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
    public void checkSearchFragment() {
        // change status from "lender" to "borrower"
        onView(withId(R.id.menu_toggle)).perform(click());

        // Click on the Search Button
        onView(withId(R.id.SearchFragment)).perform(click());

        // check if the bottom bar is visible
        assertTrue(solo.searchText("Borrowed"));
        assertTrue(solo.searchText("My Requests"));
        assertTrue(solo.searchText("Search"));

        // check if the user is in "borrower" mode
        assertTrue(solo.searchText("borrower"));

        // check the elements of the fragment
        onView(withId(R.id.search_box)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.my_search_list)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // click on the searchView
        SearchView searchBox = (SearchView) solo.getView("search_box");
        solo.clickOnView(searchBox);
        assertTrue(solo.searchText(""));

    }

    @After
    public void tearDown() {

        solo.finishOpenedActivities();

    }
}
