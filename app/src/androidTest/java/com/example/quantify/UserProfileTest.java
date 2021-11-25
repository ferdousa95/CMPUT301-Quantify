package com.example.quantify;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UserProfileTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<com.example.quantify.ShowUserProfile> rule =
            new ActivityTestRule<>(com.example.quantify.ShowUserProfile.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() {
        com.example.quantify.ShowUserProfile activity = rule.getActivity();
    }

    /*@Test
    public void checkList() {
        solo.assertCurrentActivity("Wrong activity", ShowUserProfile.class);
        solo.clickOnButton("Phone");
        solo.enterText((EditText) solo.getView(R.id.edit_text), "+1-202-555-0157");
        solo.clickOnButton("OK");
    }*/
}



