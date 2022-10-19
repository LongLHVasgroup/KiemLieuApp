package com.example.vasclientv2.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SaveVehicle implements Serializable {
    @SerializedName("TenTaiXe")
    @Expose
    private String tenTaiXe;
    @SerializedName("BienSoXe")
    @Expose
    private String bienSoXe;
    @SerializedName("CMND")
    @Expose
    private String cMND;
    @SerializedName("SelectedGate")
    @Expose
    private String selectedGate;
    @SerializedName("RFID")
    @Expose
    private String rFID;

    @SerializedName("vehicleRegisterMobileId")
    @Expose
    private String vehicleRegisterMobileId;
    @SerializedName("GiaoNhan")
    @Expose
    private String giaoNhan;
    @SerializedName("Note")
    @Expose
    private String note;
    @SerializedName("DeliveryType")
    @Expose
    private Integer deliveryType;
    @SerializedName("providerName")
    @Expose
    private String providerName;
    @SerializedName("providerCode")
    @Expose
    private String providerCode;


    @SerializedName("romooc")
    @Expose
    private String romooc;

    public String getRomooc() {
        return romooc;
    }

    public void setRomooc(String romooc) {
        this.romooc = romooc;
    }
//    @SerializedName("VehicelOwner")
//    @Expose
//    private String vehicleOwner;


    public String getVehicleRegisterMobileId() {
        return vehicleRegisterMobileId;
    }

    public void setVehicleRegisterMobileId(String vehicleRegisterMobileId) {
        this.vehicleRegisterMobileId = vehicleRegisterMobileId;
    }

    public String getTenTaiXe() {
        return tenTaiXe;
    }

    public void setTenTaiXe(String tenTaiXe) {
        this.tenTaiXe = tenTaiXe;
    }

    public String getBienSoXe() {
        return bienSoXe;
    }

    public void setBienSoXe(String bienSoXe) {
        this.bienSoXe = bienSoXe;
    }

    public String getCMND() {
        return cMND;
    }

    public void setCMND(String cMND) {
        this.cMND = cMND;
    }

    public String getSelectedGate() {
        return selectedGate;
    }

    public void setSelectedGate(String selectedGate) {
        this.selectedGate = selectedGate;
    }

    public String getRFID() {
        return rFID;
    }

    public void setRFID(String rFID) {
        this.rFID = rFID;
    }

    public String getGiaoNhan() {
        return giaoNhan;
    }

    public void setGiaoNhan(String giaoNhan) {
        this.giaoNhan = giaoNhan;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

//    public String getVehicleOwner() {
//        return vehicleOwner;
//    }
//
//    public void setVehicleOwner(String vehicleOwner) {
//        this.vehicleOwner = vehicleOwner;
//    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public SaveVehicle(String tenTaiXe, String bienSoXe, String cMND, String selectedGate, String rFID, String vehicleRegisterMobileId, String giaoNhan, String note, Integer deliveryType, String providerName, String providerCode, String romooc) {
        this.tenTaiXe = tenTaiXe;
        this.bienSoXe = bienSoXe;
        this.cMND = cMND;
        this.selectedGate = selectedGate;
        this.rFID = rFID;
        this.vehicleRegisterMobileId = vehicleRegisterMobileId;
        this.giaoNhan = giaoNhan;
        this.note = note;
        this.deliveryType = deliveryType;
        this.providerName = providerName;
        this.providerCode = providerCode;
        this.romooc = romooc;
    }
}
