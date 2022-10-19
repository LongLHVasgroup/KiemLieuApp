package com.example.vasclientv2.message;

import java.io.Serializable;

public class NotificationSender implements Serializable {
    private MesageModel data;
    private String to;

    public NotificationSender(MesageModel data, String to) {
        this.data = data;
        this.to = to;
    }

    public NotificationSender() {
    }

    public MesageModel getData() {
        return data;
    }

    public void setData(MesageModel data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
