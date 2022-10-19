package com.example.vasclientv2.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WarehouseModel implements Serializable {
    @SerializedName("wareHouseId")
    @Expose
    private String wareHouseId;
    @SerializedName("wareHouseCode")
    @Expose
    private String wareHouseCode;
    @SerializedName("wareHouseName")
    @Expose
    private String wareHouseName;

    public WarehouseModel(String wareHouseId, String wareHouseCode, String wareHouseName) {
        this.wareHouseId = wareHouseId;
        this.wareHouseCode = wareHouseCode;
        this.wareHouseName = wareHouseName;
    }

    public WarehouseModel() {
    }

    @Override
    public String toString() {
        return wareHouseName;
    }

    public String getWarehouseId() {
        return wareHouseId;
    }

    public void setWarehouseId(String wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public String getWareHouseCode() {
        return wareHouseCode;
    }

    public void setWareHouseCode(String wareHouseCode) {
        this.wareHouseCode = wareHouseCode;
    }

    public String getWareHouseName() {
        return wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }
}
