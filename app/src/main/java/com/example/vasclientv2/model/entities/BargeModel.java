package com.example.vasclientv2.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BargeModel implements Serializable {


    @SerializedName("bargeId")
    @Expose
    private String bargeId;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("bargeNumber")
    @Expose
    private String bargeNumber;
    @SerializedName("bargeOwner")
    @Expose
    private String bargeOwner;

    public String getBargeId() {
        return bargeId;
    }

    public void setBargeId(String bargeId) {
        this.bargeId = bargeId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBargeNumber() {
        return bargeNumber;
    }

    public void setBargeNumber(String bargeNumber) {
        this.bargeNumber = bargeNumber;
    }

    public String getBargeOwner() {
        return bargeOwner;
    }

    public void setBargeOwner(String bargeOwner) {
        this.bargeOwner = bargeOwner;
    }
}