package com.example.vasclientv2.model.entities;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WeightScaleModel implements Serializable {
    @SerializedName("softCode")
    @Expose
    private String softCode;
    @SerializedName("softName")
    @Expose
    private String softName;
    @SerializedName("companyCode")
    @Expose
    private String companyCode;
    @SerializedName("phone")
    @Expose
    private String phone;

    public String getSoftCode() {
        return softCode;
    }

    public void setSoftCode(String softCode) {
        this.softCode = softCode;
    }

    public String getSoftName() {
        return softName;
    }

    public void setSoftName(String softName) {
        this.softName = softName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
