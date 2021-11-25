package com.example.quantify;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class                                                                        MainActivityTest_2 {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }
    @Test
    public void checkList(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        /**solo.clickOnMenuItem("Questions");
         solo.clickOnView(solo.getView(R.id.floatingActionButton));
         solo.clickOnMenuItem("Searching");
         solo.clickOnView(solo.getView(R.id.floatingActionButton)); **/

        //Click the floatingActionButton
        solo.clickOnView(solo.getView(R.id.floatingActionButton));
        solo.sendKey(Solo.DOWN);
        solo.sendKey(Solo.ENTER);
        //solo.clickOnText("Experiment");
        solo.clickOnMenuItem("Add New");

        //solo.clickOnButton("Experiment"); //then select Experiment
        //solo.clickOnButton("Add New"); //select Add New
        //Get view for EditText and enter the info
        solo.enterText((EditText) solo.getView(R.id.exp_desc_fragment), "FunctionTest1");
        //solo.enterText((EditText) solo.getView(R.id.exp_user_fragment), "Halo_test_robot");
        //solo.enterText((EditText) solo.getView(R.id.exp_status_fragment), "RUNNING");
        solo.enterText((EditText) solo.getView(R.id.exp_type_fragment), "Binomial");
        //can't find the OK button in AlertDialog
        solo.clickOnText("Ok");

        //solo.clickOnMenuItem("OK");
        /* True if there is a text: Edmonton on the screen, wait at least 2 seconds and find
        minimum one match. */
        assertTrue(solo.waitForText("FunctionTest1", 1, 2000));
        assertTrue(solo.waitForText("Halo", 1, 2000));
        assertTrue(solo.waitForText("RUNNING", 1, 2000));
        solo.clickOnView(solo.getView(R.id.button_end)); //end the experiment
        assertFalse(solo.searchText("END")); //search if the status is END on the screen
        solo.clickOnView(solo.getView(R.id.button_delete)); //delete the experiment
        //True if there is no text: FunctionTest1 on the screen
        assertFalse(solo.searchText("FunctionTest1"));
    }
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
