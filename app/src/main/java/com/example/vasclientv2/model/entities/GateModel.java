package com.example.vasclientv2.model.entities;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GateModel implements Serializable {
    @SerializedName("gateId")
    @Expose
    private String gateId;
    @SerializedName("gateName")
    @Expose
    private String gateName;

    public String getGateId() {
        return gateId;
    }

    public void setGateId(String gateId) {
        this.gateId = gateId;
    }

    public String getGateName() {
        return gateName;
    }

    public void setGateName(String gateName) {
        this.gateName = gateName;
    }
}
