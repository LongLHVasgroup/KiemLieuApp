package com.example.vasclientv2.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VehicleOwnerModel implements Serializable {
    @SerializedName("vehicleOwner")
    @Expose
    private String vehicleOwner;
    @SerializedName("vehicleOwnerName")
    @Expose
    private String vehicleOwnerName;

    public String getVehicleOwner() {
        return vehicleOwner;
    }

    public void setVehicleOwner(String vehicleOwner) {
        this.vehicleOwner = vehicleOwner;
    }

    public String getVehicleOwnerName() {
        return vehicleOwnerName;
    }

    public void setVehicleOwnerName(String vehicleOwnerName) {
        this.vehicleOwnerName = vehicleOwnerName;
    }

    public VehicleOwnerModel() {
    }
}
