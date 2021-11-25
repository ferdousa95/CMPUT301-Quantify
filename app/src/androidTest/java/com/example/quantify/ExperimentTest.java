package com.example.quantify;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * VERY IMPORTANT NOTE
 * MANY OF THE TEST CASES ARE BUILT ON SPECIFIC DATA SAVED ON DATABASE AT THE TIME OF TESTING
 * SO THEY MIGHT NOT WORK/GIVE ERROR LATER ON. ALSO MANY OF THE TEST CASES ARE BUILT ON
 * ROBOTIUM SCREEN COORDINATES THAT HELPED TO REAL PLACE WHICH WAS BEING TESTED SO RUNNING THEM
 * LATER WILL GIVE THE SAME ERROR. TO USE THESE SPECIFIC TEST CASES, KNOW WHAT YOU ARE DOING.
 */

public class ExperimentTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * This test checks if has anything on the screen at start (should be nothing as new user),
     * then creates an experiment. Then checks again if he has the experiment now or not.
     */
    @Test
    public void publish() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        Activity activity = (Activity)solo.getCurrentActivity();
        ListView listview = (ListView)solo.getView(R.id.exp_list);
        int countPersonalExp = listview.getAdapter().getCount();

        // The list should be empty
        assertEquals(0, countPersonalExp);

        // Now that we are good upto this point, next, create an experiment.
        solo.clickOnView(solo.getView(R.id.floatingActionButton));
        solo.enterText((EditText) solo.getView(R.id.exp_desc_fragment), "UI Testing");
        solo.clearEditText((EditText) solo.getView(R.id.exp_desc_fragment)); //Clear the EditText
        solo.enterText((EditText) solo.getView(R.id.exp_min_trials), "5");
        solo.clearEditText((EditText) solo.getView(R.id.exp_min_trials)); //Clear the EditText

        // Selecting which experiment type is it
        solo.clickOnView(solo.getView(R.id.exp_type_fragment, 0));
        solo.scrollToTop();
        solo.clickOnView(solo.getView(TextView.class, 0));  // We choose you Binomial!

        //solo.clickOnButton("OK");
        solo.clickOnScreen(866, 1661);
        // Do we return back to main activity?
        solo.assertCurrentActivity("Wrong Activity, need Main activity", MainActivity.class);
        // Check if our experiment is created
        countPersonalExp = listview.getAdapter().getCount();
        assertTrue(solo.waitForText("UI Testing", 1, 2000));
    }

    @Test
    public void unpublish() {
        solo.clickOnScreen(755, 780);
        ListView listview = (ListView)solo.getView(R.id.exp_list);
        int countPersonalExp = listview.getAdapter().getCount();
        // The experiment should be gone
        assertEquals(0, countPersonalExp);

    }

    @Test
    public void end() {
        solo.clickOnScreen(289, 766);
        solo.clickOnText("UI Testing");
        solo.clickOnScreen(366, 1343);
        // the result is already 3 and it should not change
        assertTrue(solo.waitForText("3", 1, 2000));
    }

    @Test
    public void subscribe() {
        solo.clickOnScreen(808, 285);
        ListView listview = (ListView)solo.getView(R.id.exp_list);

        // this subscribes the second row of all experiments
        //String listValue = listview.getItemAtPosition(1).toString();
        solo.clickLongOnScreen(459, 872);

        // lets see if we can find the subscribed experiment
        solo.clickOnScreen(607, 2005);
        assertTrue(solo.waitForText("HaloPhoneTest", 1, 2000));

    }

    @Test
    public void addTrials() {
        solo.clickOnScreen(808, 285);   // all experiment tab
        solo.clickOnScreen(521, 1560);  // experiment from list
        solo.clickOnScreen(848, 1355);  // proceed

        solo.clickOnScreen(541, 1688);  // start
        solo.clickOnScreen(366, 1349);  // success
        solo.clickOnScreen(550, 1563);  // save

        // previous ans was 4, there is one place it should be 5
        assertTrue(solo.searchText("1"));

    }



    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}

