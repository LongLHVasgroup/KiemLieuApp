package com.example.vasclientv2.kiemlieu.kiemlieu;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vasclientv2.model.entities.CheckingScrapModel;

import java.util.ArrayList;

public class AddInfoKLViewModel extends ViewModel {
    private MutableLiveData<ArrayList<CheckingScrapModel>> listCheckingScrap;
    private MutableLiveData<String> truKG;
    private MutableLiveData<String> truPhanTram;
    private MutableLiveData<Boolean> enableTruKg;
    private MutableLiveData<Boolean> enableTruPercent;

    public MutableLiveData<String> getTruKG() {
        if (truKG == null) {
            truKG = new MutableLiveData<String>();
        }
        return truKG;
    }

    public void setTruKG(String s) {
        truKG.setValue(s);
    }

    public MutableLiveData<String> getTruPhanTram() {
        if (truPhanTram == null) {
            truPhanTram = new MutableLiveData<String>();
        }
        return truPhanTram;
    }

    public void setTruPhanTram(String s) {
        truPhanTram.setValue(s);
    }

    public MutableLiveData<Boolean> getEnableTruKg() {
        if (enableTruKg == null) {
            enableTruKg = new MutableLiveData<Boolean>();
        }
        return enableTruKg;
    }

    public MutableLiveData<Boolean> getEnableTruPercent() {
        if (enableTruPercent == null) {
            enableTruPercent = new MutableLiveData<Boolean>();
        }
        return enableTruPercent;
    }

    public void setEnableTruKg(Boolean b) {
        enableTruKg.setValue(b);
    }

    public void setEnableTruPercent(Boolean b) {
        enableTruPercent.setValue(b);
    }


    public void checkEnableTruField(Boolean getBoth, String truKg, String truPercent) {
        if (getBoth) {
            enableTruKg.setValue(true);
            enableTruPercent.setValue(true);
        } else {
            float kg;
            try {
                kg = Float.parseFloat(truKg);
            } catch (Exception e) {
                kg = 0;
            }
            float percent;
            try {
                percent = Float.parseFloat(truPercent);
            } catch (Exception e) {
                percent = 0;
            }
            if (kg<=0 && percent <=0){
                enableTruKg.setValue(true);
                enableTruPercent.setValue(true);
            }else if (kg>0){
                enableTruKg.setValue(true);
                enableTruPercent.setValue(false);
            }else if (percent>0){
                enableTruKg.setValue(false);
                enableTruPercent.setValue(true);
            }
        }

    }

}
