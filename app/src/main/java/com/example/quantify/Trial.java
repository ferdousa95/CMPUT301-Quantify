package com.example.quantify;

import android.app.Activity;

public class Trial extends Activity {
    private String experimenterID;
    private String Date;
    private String result;

    public Trial(String experimenterID, String date, String result) {
        this.experimenterID = experimenterID;
        Date = date;
        this.result = result;
    }

    public String getExperimenterID() {
        return experimenterID;
    }

    public void setExperimenterID(String experimenterID) {
        this.experimenterID = experimenterID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
