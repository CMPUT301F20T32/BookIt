package com.example.bookit;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<LoginActivity>(LoginActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        // enter the email
        solo.enterText((EditText) solo.getView(R.id.editTextTextEmailAddress), "sheldon@gmail.com");

        //enter the pass
        solo.enterText((EditText) solo.getView(R.id.editTextTextPassword), "1234567");
    }

    @Test
    public void showMainActivity() {
        //click on login
        //solo.clickOnButton("Log");

        // Check if MainActivity is launched
        //solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }
}
