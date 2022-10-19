package com.example.vasclientv2.baove.vehiclewait;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vasclientv2.baove.vehiclein.AddCheckingScrapActivity;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.GateModel;
import com.example.vasclientv2.model.entities.VehicleModel;
import com.example.vasclientv2.model.entities.WeightScaleModel;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleWaitViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<ArrayList<CheckingScrapModel>> listData;
    private MutableLiveData<ArrayList<GateModel>> listGate;

    public VehicleWaitViewModel() {
        listData = new MutableLiveData<>();
        listGate = new MutableLiveData<>();
    }

    public LiveData<ArrayList<CheckingScrapModel>> getList() {
        return listData;
    }

    public void setListData(ArrayList<CheckingScrapModel> data) {
        listData.setValue(data);
    }

    public LiveData<ArrayList<GateModel>> getListGate() {
        return listGate;
    }

    public ArrayList<CheckingScrapModel> getListDataValue(){
        return listData.getValue();
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
//                            WeightScaleModel ws = new WeightScaleModel();
//                            ws.setSoftCode("0");
//                            ws.setSoftName("Tất Cả");
//                            temp.add(ws);
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
//
//
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