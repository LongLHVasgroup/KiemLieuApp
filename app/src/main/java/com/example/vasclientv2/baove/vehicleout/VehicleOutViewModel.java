package com.example.vasclientv2.baove.vehicleout;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class VehicleOutViewModel extends ViewModel {

    private MutableLiveData<String> alertResult;

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