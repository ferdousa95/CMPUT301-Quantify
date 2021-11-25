package com.example.quantify;

import java.io.Serializable;

public class User implements Serializable, Comparable<User> {

    private String userID;
    private String phoneNum;

    public User(String userID, String phoneNum) {
        this.userID = userID;
        this.phoneNum = phoneNum;
    }

    @Override
    public int compareTo(User o) {
        return 0;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
