package com.example.vasclientv2.message;

import java.io.Serializable;

public class MesageModel implements Serializable {
    private String to;
    private DataMessage data;

    public MesageModel() {
    }

    public MesageModel(String to, DataMessage data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public DataMessage getData() {
        return data;
    }

    public void setData(DataMessage data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MesageModel{" +
                "to='" + to + '\'' +
                ", dataMessage=" + data +
                '}';
    }
}
