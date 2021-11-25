package com.example.quantify;

import java.util.UUID;

public class BinomialTrial {
    private UUID trialID;
    private String result;
    private UUID experimenterID;
    private String location; // turn it into actual location
    private String QRCode;

    public BinomialTrial(UUID trialID, String result, UUID experimenterID) {
        this.trialID = trialID;
        this.result = result;
        this.experimenterID = experimenterID;
    }

    public UUID getTrialID() {
        return trialID;
    }

    public void setTrialID(UUID trialID) {
        this.trialID = trialID;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public UUID getExperimenterID() {
        return experimenterID;
    }

    public void setExperimenterID(UUID experimenterID) {
        this.experimenterID = experimenterID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }
}
