package com.example.vasclientv2.ui.login;

import java.io.Serializable;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView implements Serializable {
    private String displayName;
    private String roldCode;
    private String userId;
    //... other data fields that may be accessible to the UI

    public LoggedInUserView() {
    }

    public LoggedInUserView(String displayName, String roldCode, String userId) {
        this.displayName = displayName;
        this.roldCode = roldCode;
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRoldCode() {
        return roldCode;
    }

    public void setRoldCode(String roldCode) {
        this.roldCode = roldCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LoggedInUserView{" +
                "displayName='" + displayName + '\'' +
                ", roldCode='" + roldCode + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
//    LoggedInUserView(String displayName) {
//        this.displayName = displayName;
//    }
//
//    String getDisplayName() {
//        return displayName;
//    }
}