package com.example.vasclientv2.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProviderModel implements Serializable {
    @SerializedName("providerCode")
    @Expose
    private String providerCode;
    @SerializedName("providerName")
    @Expose
    private String providerName;

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public ProviderModel(String providerCode, String providerName) {
        this.providerCode = providerCode;
        this.providerName = providerName;
    }

    public ProviderModel() {
    }
}
