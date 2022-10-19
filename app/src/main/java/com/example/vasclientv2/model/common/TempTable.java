package com.example.vasclientv2.model.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class TempTable {
    @SerializedName("kL_ID")
    @Expose
    private String kLID;
    @SerializedName("token_KL")
    @Expose
    private String tokenKL;
    @SerializedName("tkL_ID")
    @Expose
    private String tkLID;
    @SerializedName("token_TKL")
    @Expose
    private String tokenTKL;
    @SerializedName("UserID")
    @Expose
    private String UserID;
    @SerializedName("OldPass")
    @Expose
    private String OldPass;
    @SerializedName("NewPass")
    @Expose
    private String NewPass;
    @SerializedName("NewPass1")
    @Expose
    private String NewPass1;

    public String getkLID() {
        return kLID;
    }

    public void setkLID(String kLID) {
        this.kLID = kLID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getOldPass() {
        return OldPass;
    }

    public void setOldPass(String oldPass) {
        OldPass = oldPass;
    }

    public String getNewPass() {
        return NewPass;
    }

    public void setNewPass(String newPass) {
        NewPass = newPass;
    }

    public String getNewPass1() {
        return NewPass1;
    }

    public void setNewPass1(String newPass1) {
        NewPass1 = newPass1;
    }

    public String getKLID() {
        return kLID;
    }

    public void setKLID(String kLID) {
        this.kLID = kLID;
    }

    public String getTokenKL() {
        return tokenKL;
    }

    public void setTokenKL(String tokenKL) {
        this.tokenKL = tokenKL;
    }

    public String getTkLID() {
        return tkLID;
    }

    public void setTkLID(String tkLID) {
        this.tkLID = tkLID;
    }

    public String getTokenTKL() {
        return tokenTKL;
    }

    public void setTokenTKL(String tokenTKL) {
        this.tokenTKL = tokenTKL;
    }
}
