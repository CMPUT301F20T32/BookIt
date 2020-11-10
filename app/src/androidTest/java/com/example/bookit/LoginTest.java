package com.example.bookit;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mLoginActivityTestRule
            = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void checkViews() {
        onView(withId(R.id.editTextTextEmailAddress))
                .check(matches(isDisplayed()));

        onView(withId(R.id.editTextTextPassword))
                .check(matches(isDisplayed()));

        onView(withId(R.id.button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.imageView))
                .check(matches(isDisplayed()));

        onView(withId(R.id.textView))
                .check(matches(isDisplayed()));

        onView(withId(R.id.manage_request_recycler))
                .check(matches(isDisplayed()));

        onView(withId(R.id.accept_request_button))
                .check(matches(isDisplayed()));

    }
}
