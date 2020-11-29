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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

/**
 * This class is used for Intent testing for the books of the Lender
 */
public class SignUpTest {
    private Solo solo;

    @Rule
    public ActivityScenarioRule<SignUpActivity> rule =
            new ActivityScenarioRule<>(SignUpActivity.class);

    @Before
    public void setUp() {

        rule.getScenario().onActivity(activity -> solo =
                new Solo(InstrumentationRegistry.getInstrumentation(), activity));
    }

    @Test
    public void checkSignUpButton() {
        onView(withId(R.id.editTextTextUserName)).perform(typeText("BiggerYoda"),closeSoftKeyboard());
        onView(withId(R.id.editTextTextSignUpEmailAddress)).perform(typeText("mictesting111@gmail.com"),closeSoftKeyboard());
        onView(withId(R.id.editTextSignUpPassword)).perform(typeText("1234567"),closeSoftKeyboard());
        onView(withId(R.id.editTextPhone)).perform(typeText("5879370137"),closeSoftKeyboard());
        onView(withId(R.id.editTextTextPersonName)).perform(typeText("Testing123"),closeSoftKeyboard());
        onView(withId(R.id.signUp)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
//        onView(withId(R.id.signUp)).perform(click());
//        assert solo.waitForActivity(MainActivity.class);
    }

    @After
    public void tearDown() {

        solo.finishOpenedActivities();

    }
}
