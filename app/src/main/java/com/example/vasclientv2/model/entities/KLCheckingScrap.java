package com.example.vasclientv2.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KLCheckingScrap {
    @SerializedName("scaleTicket")
    @Expose
    private ScaleTicketModel scaleTicket;
    @SerializedName("scaleTicketPODetailList")
    @Expose
    private List<ScaleTicketPODetailModel> scaleTicketPODetailList = null;
    @SerializedName("checkingScrap")
    @Expose
    private CheckingScrapModel checkingScrap;
    @SerializedName("vehicleModel")
    @Expose
    private VehicleModel vehicleModel;
    @SerializedName("productList")
    @Expose
    private List<ProductModel> productList = null;
    @SerializedName("isEdit")
    @Expose
    private Boolean isEdit;
    @SerializedName("history")
    @Expose
    private List<HistoryModel> history = null;
    @SerializedName("isDaDuyet")
    @Expose
    private Boolean isDaDuyet;
    @SerializedName("wareHouses")
    @Expose
    private WarehouseModel warehouse;

    public WarehouseModel getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseModel warehouse) {
        this.warehouse = warehouse;
    }

    public ScaleTicketModel getScaleTicket() {
        return scaleTicket;
    }

    public void setScaleTicket(ScaleTicketModel scaleTicket) {
        this.scaleTicket = scaleTicket;
    }

    public List<ScaleTicketPODetailModel> getScaleTicketPODetailList() {
        return scaleTicketPODetailList;
    }

    public void setScaleTicketPODetailList(List<ScaleTicketPODetailModel> scaleTicketPODetailList) {
        this.scaleTicketPODetailList = scaleTicketPODetailList;
    }

    public CheckingScrapModel getCheckingScrap() {
        return checkingScrap;
    }

    public void setCheckingScrap(CheckingScrapModel checkingScrap) {
        this.checkingScrap = checkingScrap;
    }

    public VehicleModel getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(VehicleModel vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public List<ProductModel> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductModel> productList) {
        this.productList = productList;
    }

    public Boolean getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    public List<HistoryModel> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryModel> history) {
        this.history = history;
    }

    public Boolean getIsDaDuyet() {
        return isDaDuyet;
    }

    public void setIsDaDuyet(Boolean isDaDuyet) {
        this.isDaDuyet = isDaDuyet;
    }

}