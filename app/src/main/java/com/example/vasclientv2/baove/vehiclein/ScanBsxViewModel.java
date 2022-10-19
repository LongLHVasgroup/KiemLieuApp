package com.example.vasclientv2.baove.vehiclein;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScanBsxViewModel extends ViewModel {
    private MutableLiveData<String> numberPlate;

    public ScanBsxViewModel() {
        numberPlate = new MutableLiveData<>();
    }

    public MutableLiveData<String> getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate.setValue(numberPlate);
    }

}
