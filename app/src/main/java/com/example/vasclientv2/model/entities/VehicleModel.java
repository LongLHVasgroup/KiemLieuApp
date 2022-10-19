package com.example.vasclientv2.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VehicleModel implements Serializable {
    @SerializedName("vehicleId")
    @Expose
    private String vehicleId;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("typeText")
    @Expose
    private String typeText;
    @SerializedName("vehicleNumber")
    @Expose
    private String vehicleNumber;
    @SerializedName("romooc")
    @Expose
    private String romooc;
    @SerializedName("vehicleOwner")
    @Expose
    private List<VehicleOwnerModel> vehicleOwner;
    @SerializedName("driverName")
    @Expose
    private String driverName;
    @SerializedName("driverIdCard")
    @Expose
    private String driverIdCard;

    @SerializedName("vehicleRegisterMobileId")
    @Expose
    private String vehicleRegisterMobileId;
    // Phat thêm chọn nhà cung cấp
    @SerializedName("lstProvider")
    @Expose
    private List<ProviderModel> lstProvider;

    @SerializedName("regStatus")
    @Expose
    private String regStatus;

    @SerializedName("regInt")
    @Expose
    private Integer regInt;

    public String getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(String regStatus) {
        this.regStatus = regStatus;
    }

    public Integer getRegInt() {
        return regInt;
    }

    public String getVehicleRegisterMobileId() {
        return vehicleRegisterMobileId;
    }

    public void setVehicleRegisterMobileId(String vehicleRegisterMobileId) {
        this.vehicleRegisterMobileId = vehicleRegisterMobileId;
    }

    public void setRegInt(Integer regInt) {
        this.regInt = regInt;
    }

    public List<ProviderModel> getProviders() {
        return lstProvider;
    }

    public void setProviders(List<ProviderModel> providers) {
        this.lstProvider = providers;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getRomooc() {
        return romooc;
    }

    public void setRomooc(String romooc) {
        this.romooc = romooc;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public List<VehicleOwnerModel> getVehicleOwner() {
        return vehicleOwner;
    }

    public void setVehicleOwner(List<VehicleOwnerModel> vehicleOwner) {
        this.vehicleOwner = vehicleOwner;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverIdCard() {
        return driverIdCard;
    }

    public void setDriverIdCard(String driverIdCard) {
        this.driverIdCard = driverIdCard;
    }

}