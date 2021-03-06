package com.example.bookit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
 * This class is used for intent testing of the my profile and edit profile fragments
 */
public class MyProfileTest {
    private Solo solo;

    @Rule
    public ActivityScenarioRule<ProfileActivity> rule =
            new ActivityScenarioRule<>(ProfileActivity.class);

    @Before
    public void setUp() {

        rule.getScenario().onActivity(activity -> solo =
                new Solo(InstrumentationRegistry.getInstrumentation(), activity));
    }

    @Test
    public void checkMyProfileFragments() {
        
        // click on the profile icon
        //onView(withId(R.id.action_settings)).perform(click());

        // check if all the profile field headers are visible
        assertTrue(solo.searchText("Full Name"));
        assertTrue(solo.searchText("User Name"));
        assertTrue(solo.searchText("Email"));
        assertTrue(solo.searchText("Phone"));

        //check if the toolbar is visible
        onView(withId(R.id.toolbar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //check if the field icons are visible
        onView(withId(R.id.profileIcon)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.emailIcon)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.phoneIcon)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //check if the profile fields are visible
        onView(withId(R.id.profileEmail)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.profileName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.profilePhone)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.profileUsername)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //check if logout button is visible
        onView(withId(R.id.logoutButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // check if edit profile button is visible
        onView(withId(R.id.editProfileButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));


    }

    @After
    public void tearDown() {

        solo.finishOpenedActivities();

    }
}
