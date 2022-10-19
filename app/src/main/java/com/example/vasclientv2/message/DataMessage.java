package com.example.vasclientv2.message;

import java.io.Serializable;

public class DataMessage implements Serializable {
    private String title;
    private String message;
    private String reciver_token;
    private String verhicle_number;
    private String rfid;
    private String sender_id;

    public DataMessage() {
    }

    public DataMessage(String title, String message, String reciver_token, String verhicle_number, String rfid, String sender_id) {
        this.title = title;
        this.message = message;
        this.reciver_token = reciver_token;
        this.verhicle_number = verhicle_number;
        this.rfid = rfid;
        this.sender_id = sender_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReciver_token() {
        return reciver_token;
    }

    public void setReciver_token(String reciver_token) {
        this.reciver_token = reciver_token;
    }

    public String getVerhicle_number() {
        return verhicle_number;
    }

    public void setVerhicle_number(String verhicle_number) {
        this.verhicle_number = verhicle_number;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    @Override
    public String toString() {
        return "MesageModel{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", reciver_token='" + reciver_token + '\'' +
                ", verhicle_number='" + verhicle_number + '\'' +
                ", rfid='" + rfid + '\'' +
                ", sender_id='" + sender_id + '\'' +
                '}';
    }
}
