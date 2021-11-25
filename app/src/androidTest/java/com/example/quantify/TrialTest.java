package com.example.quantify;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TrialTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<Trial> rule =
            new ActivityTestRule<>(Trial.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() {
        Trial activity = rule.getActivity();
    }
}

