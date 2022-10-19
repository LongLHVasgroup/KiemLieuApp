package com.example.vasclientv2.kiemlieu.rejected;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.GateModel;
import com.example.vasclientv2.model.entities.KLCheckingScrap;
import com.example.vasclientv2.model.entities.WeightScaleModel;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RejectedKLViewModel extends ViewModel {

    private MutableLiveData<ArrayList<CheckingScrapModel>> listCheckingScrap;
    private MutableLiveData<ArrayList<GateModel>> listGate;
//    private MutableLiveData<String> selectedWeightScale;


    public RejectedKLViewModel() {
        listCheckingScrap = new MutableLiveData<>();
        listGate = new MutableLiveData<>();
//        selectedWeightScale = new MutableLiveData<>("0");
    }

    public void setListCheckingScrap(ArrayList<CheckingScrapModel> ls){
        listCheckingScrap.setValue(ls);
    }

    public MutableLiveData<ArrayList<CheckingScrapModel>> getListCheckingScrap() {
        return listCheckingScrap;
    }

//    public LiveData<String> getSelectedWeightScale() {
//        return selectedWeightScale;
//    }

//    public void setSelectedWeightScale(String data) {
//        selectedWeightScale.setValue(data);
//    }

    public LiveData<ArrayList<GateModel>> getListGate() {
        return listGate;
    }

    public void setListGate(ArrayList<GateModel> data) {
        listGate.setValue(data);
    }

//    public void getListWeightScaleByAPI(Call<ListResponeMessage<WeightScaleModel>> callApiGetWeightScale){
//        try {
//
//            // TODO: handle loggedInUser authentication
//            // Call API check vehicle on server
//            callApiGetWeightScale.enqueue(new Callback<ListResponeMessage<WeightScaleModel>>() {
//                @Override
//                public void onResponse(Call<ListResponeMessage<WeightScaleModel>> call, Response<ListResponeMessage<WeightScaleModel>> response) {
//                    try {
//                        if (response.body().getIsSuccess()) {
//                            List<WeightScaleModel> temp = response.body().getData();
//                            ArrayList<WeightScaleModel> t = new ArrayList<>(temp);
//                            listWeightScale.setValue(t);
//                        }
//                    }catch (Exception e){
//                        initWeightScale();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ListResponeMessage<WeightScaleModel>> call, Throwable t) {
//                    initWeightScale();
//                }
//            });
//
//        } catch (Exception e) {
//            initWeightScale();
//        }
//    }
//    private void initWeightScale(){
//        ArrayList<WeightScaleModel> temp = new ArrayList<>();
//        WeightScaleModel t1 = new WeightScaleModel();
//        t1.setSoftCode("0");
//        t1.setSoftName("Tất Cả");
//        temp.add(t1);
//        listWeightScale.setValue(temp);
//    }

    public void getListGateByAPI(Call<ListResponeMessage<GateModel>> callApiGetGate) {

        callApiGetGate.enqueue(new Callback<ListResponeMessage<GateModel>>() {
            @Override
            public void onResponse(Call<ListResponeMessage<GateModel>> call, Response<ListResponeMessage<GateModel>> response) {
                Log.d("TAG", response.code() + "");
                try {
                    if (response.body().getIsSuccess()) {
                        List<GateModel> temp = response.body().getData();
                        ArrayList<GateModel> t = new ArrayList<>(temp);
                        listGate.setValue(t);
                    }
                } catch (Exception e) {
//                        initWeightScale();
                }

            }

            @Override
            public void onFailure(Call<ListResponeMessage<GateModel>> call, Throwable t) {
            }
        });
    }
}