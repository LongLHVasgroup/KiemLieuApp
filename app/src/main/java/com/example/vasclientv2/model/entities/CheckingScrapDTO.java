package com.example.vasclientv2.model.entities;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CheckingScrapDTO {

    private ArrayList<ScaleTicketPODetailModel> PhanBoData ;
    private ScaleTicketModel ScaleTicket;
    private String RFID ;
    private String VehicleCode ;
    private String NumberCong ;
    private String container1Code ;
    private String container2Code ;
    private Boolean Is20Feet ;
    private int TruKg ;
    private BigDecimal TruPhanTram ;
    private String Note ;
    private Boolean IsEdit ;
    private Boolean IsDuyetPKL ;

    public CheckingScrapDTO(String vehicleCode, String numberCong,
                            String container1Code, String container2Code,
                            Boolean is20Feet, int truKg, BigDecimal truPhanTram,
                            String note, Boolean isEdit, Boolean isDuyetPKL) {
        VehicleCode = vehicleCode;
        NumberCong = numberCong;
        this.container1Code = container1Code;
        this.container2Code = container2Code;
        Is20Feet = is20Feet;
        TruKg = truKg;
        TruPhanTram = truPhanTram;
        Note = note;
        IsEdit = isEdit;
        IsDuyetPKL = isDuyetPKL;
    }

    public CheckingScrapDTO() {
    }

    public ArrayList<ScaleTicketPODetailModel> getPhanBoData() {
        return PhanBoData;
    }

    public void setPhanBoData(ArrayList<ScaleTicketPODetailModel> phanBoData) {
        PhanBoData = phanBoData;
    }

    public ScaleTicketModel getScaleTicket() {
        return ScaleTicket;
    }

    public void setScaleTicket(ScaleTicketModel scaleTicket) {
        ScaleTicket = scaleTicket;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getVehicleCode() {
        return VehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        VehicleCode = vehicleCode;
    }

    public String getNumberCong() {
        return NumberCong;
    }

    public void setNumberCong(String numberCong) {
        NumberCong = numberCong;
    }

    public String getContainer1Code() {
        return container1Code;
    }

    public void setContainer1Code(String container1Code) {
        this.container1Code = container1Code;
    }

    public String getContainer2Code() {
        return container2Code;
    }

    public void setContainer2Code(String container2Code) {
        this.container2Code = container2Code;
    }

    public Boolean getIs20Feet() {
        return Is20Feet;
    }

    public void setIs20Feet(Boolean is20Feet) {
        Is20Feet = is20Feet;
    }

    public int getTruKg() {
        return TruKg;
    }

    public void setTruKg(int truKg) {
        TruKg = truKg;
    }

    public BigDecimal getTruPhanTram() {
        return TruPhanTram;
    }

    public void setTruPhanTram(BigDecimal truPhanTram) {
        TruPhanTram = truPhanTram;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public Boolean getEdit() {
        return IsEdit;
    }

    public void setEdit(Boolean edit) {
        IsEdit = edit;
    }

    public Boolean getDuyetPKL() {
        return IsDuyetPKL;
    }

    public void setDuyetPKL(Boolean duyetPKL) {
        IsDuyetPKL = duyetPKL;
    }
}
