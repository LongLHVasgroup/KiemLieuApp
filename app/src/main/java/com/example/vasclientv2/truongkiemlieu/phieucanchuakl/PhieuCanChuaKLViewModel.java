package com.example.vasclientv2.truongkiemlieu.phieucanchuakl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vasclientv2.model.entities.CheckingScrapModel;

import java.util.ArrayList;

public class PhieuCanChuaKLViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<CheckingScrapModel>> listData;

    public PhieuCanChuaKLViewModel() {
        mText = new MutableLiveData<>();
        listData = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<CheckingScrapModel>> getList(){return listData;}

    public void setListData(ArrayList<CheckingScrapModel> data){
        listData.setValue(data);
    }

}