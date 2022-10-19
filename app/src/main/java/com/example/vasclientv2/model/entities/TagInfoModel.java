package com.example.vasclientv2.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TagInfoModel implements Serializable {


    @SerializedName("rfid")
    @Expose
    private String rfid;
    @SerializedName("tagNumber")
    @Expose
    private String tagNumber;
    @SerializedName("tagType")
    @Expose
    private String tagType;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("lost")
    @Expose
    private Boolean lost;


    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Boolean getLost() {
        return lost;
    }

    public void setLost(Boolean lost) {
        this.lost = lost;
    }

    public TagInfoModel(String rfid, String tagNumber, String tagType, String location, String company, Boolean lost) {
        this.rfid = rfid;
        this.tagNumber = tagNumber;
        this.tagType = tagType;
        this.location = location;
        this.company = company;
        this.lost = lost;
    }

    public TagInfoModel() {
    }
}
