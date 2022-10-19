package com.example.vasclientv2.truongkiemlieu.phieudakl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.WeightScaleModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhieuKlDaDuyetViewModel extends ViewModel {
    // TODO: Implement the ViewModel


    private MutableLiveData<ArrayList<CheckingScrapModel>> listData;
    private MutableLiveData<ArrayList<WeightScaleModel>> listWeightScale;

    public PhieuKlDaDuyetViewModel() {
        listData = new MutableLiveData<>();
        listWeightScale = new MutableLiveData<>();
    }

    public LiveData<ArrayList<CheckingScrapModel>> getList() {
        return listData;
    }

    public void setListData(ArrayList<CheckingScrapModel> data) {
        listData.setValue(data);
    }

    public LiveData<ArrayList<WeightScaleModel>> getListWeightScale() {
        return listWeightScale;
    }

    public void setListWeightScale(ArrayList<WeightScaleModel> data) {
        listWeightScale.setValue(data);
    }



    private void initWeightScale() {
        ArrayList<WeightScaleModel> temp = new ArrayList<>();
        WeightScaleModel t1 = new WeightScaleModel();
        t1.setSoftCode("0");
        t1.setSoftName("Tất Cả");
        temp.add(t1);
        listWeightScale.setValue(temp);
    }
}