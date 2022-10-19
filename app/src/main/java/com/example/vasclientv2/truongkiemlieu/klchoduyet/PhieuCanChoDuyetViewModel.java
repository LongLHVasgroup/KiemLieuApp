package com.example.vasclientv2.truongkiemlieu.klchoduyet;

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

public class PhieuCanChoDuyetViewModel extends ViewModel {
    private MutableLiveData<ArrayList<CheckingScrapModel>> listData;
    private MutableLiveData<ArrayList<WeightScaleModel>> listWeightScale;

    public PhieuCanChoDuyetViewModel() {
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

    public void getListWeightScaleByAPI(Call<ListResponeMessage<WeightScaleModel>> callApiGetWeightScale) {
        try {

            // TODO: handle loggedInUser authentication
            // Call API check vehicle on server
            callApiGetWeightScale.enqueue(new Callback<ListResponeMessage<WeightScaleModel>>() {
                @Override
                public void onResponse(Call<ListResponeMessage<WeightScaleModel>> call, Response<ListResponeMessage<WeightScaleModel>> response) {
                    try {
                        if (response.body().getIsSuccess()) {
                            List<WeightScaleModel> temp = response.body().getData();
                            ArrayList<WeightScaleModel> t = new ArrayList<>(temp);
                            listWeightScale.setValue(t);
                        }
                    } catch (Exception e) {
                        initWeightScale();
                    }
                }

                @Override
                public void onFailure(Call<ListResponeMessage<WeightScaleModel>> call, Throwable t) {
                    initWeightScale();
                }
            });

        } catch (Exception e) {
            initWeightScale();
        }
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