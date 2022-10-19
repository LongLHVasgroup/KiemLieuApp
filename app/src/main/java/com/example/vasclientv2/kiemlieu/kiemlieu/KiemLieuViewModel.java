package com.example.vasclientv2.kiemlieu.kiemlieu;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class KiemLieuViewModel extends ViewModel {

    private MutableLiveData<String> alertResult;
    private MutableLiveData<String> rfid;

    public MutableLiveData<String> getRFID() {
        if (rfid == null) {
            rfid = new MutableLiveData<String>();
        }
        return rfid;
    }
    public void setRFID(String s) {
        rfid.setValue(s);
    }

    public MutableLiveData<String> getAlertResult() {
        if (alertResult == null) {
            alertResult = new MutableLiveData<String>();
        }
        return alertResult;
    }
    public void setAlertResult(String s) {
        alertResult.setValue(s);
    }
}