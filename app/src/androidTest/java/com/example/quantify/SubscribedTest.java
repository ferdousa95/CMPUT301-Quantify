package com.example.quantify;


import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SubscribedTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SubscribedActivity> rule =
            new ActivityTestRule<>(SubscribedActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() {
        SubscribedActivity activity = rule.getActivity();
    }
}
