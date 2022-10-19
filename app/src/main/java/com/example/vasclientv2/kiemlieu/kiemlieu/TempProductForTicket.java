package com.example.vasclientv2.kiemlieu.kiemlieu;

public class TempProductForTicket {
    private String productCode;
    private String productName;
    private Boolean isDiff;

    public TempProductForTicket() {
    }

    public TempProductForTicket(String productCode, String productName, Boolean isDiff) {
        this.productCode = productCode;
        this.productName = productName;
        this.isDiff = isDiff;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Boolean getDiff() {
        return isDiff;
    }

    public void setDiff(Boolean diff) {
        isDiff = diff;
    }

    @Override
    public String toString() {
        return "TempProductForTicket{" +
                "productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", isDiff=" + isDiff +
                '}';
    }
}
