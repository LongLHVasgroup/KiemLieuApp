package com.example.vasclientv2.baove.vehiclelosttag;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vasclientv2.R;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.GateModel;
import com.example.vasclientv2.model.entities.VehicleModel;
import com.example.vasclientv2.ui.WareHouse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleLostTagViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> textResultSuccess, textResultFailed, currentGate, outPort;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<ArrayList<CheckingScrapModel>> listData;
    private MutableLiveData<CheckingScrapModel> selectedVehicle;
    private MutableLiveData<ArrayList<GateModel>> listGate;
    private ApiInterface apiInterface;

    public VehicleLostTagViewModel() {
        textResultSuccess = new MutableLiveData<>();
        textResultFailed = new MutableLiveData<>();
        currentGate = new MutableLiveData<>();
        outPort = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        listData = new MutableLiveData<>();
        listGate = new MutableLiveData<>();
        selectedVehicle = new MutableLiveData<>();

        // API init
        apiInterface = ApiService.getClient().create(ApiInterface.class);
    }

    public MutableLiveData<String> getOutPort() {
        return outPort;
    }

    public MutableLiveData<String> getTextResultFailed() {
        return textResultFailed;
    }

    public void setTextResultFailed(MutableLiveData<String> textResultFailed) {
        this.textResultFailed = textResultFailed;
    }

    public MutableLiveData<String> getTextResultSuccess() {
        return textResultSuccess;
    }

    public void setTextResultSuccess(MutableLiveData<String> textResultSuccess) {
        this.textResultSuccess = textResultSuccess;
    }


    public MutableLiveData<CheckingScrapModel> getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(MutableLiveData<CheckingScrapModel> selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }

    public void setOutPort(MutableLiveData<String> outPort) {
        this.outPort = outPort;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(MutableLiveData<Boolean> isLoading) {
        this.isLoading = isLoading;
    }

    public MutableLiveData<ArrayList<CheckingScrapModel>> getListData() {
        return listData;
    }

    public void setListData(MutableLiveData<ArrayList<CheckingScrapModel>> listData) {
        this.listData = listData;
    }

    public MutableLiveData<ArrayList<GateModel>> getListGate() {
        return listGate;
    }

    public void setListGate(MutableLiveData<ArrayList<GateModel>> listGate) {
        this.listGate = listGate;
    }

    public MutableLiveData<String> getCurrentGate() {
        return currentGate;
    }

    public void setCurrentGate(MutableLiveData<String> currentGate) {
        this.currentGate = currentGate;
    }

    public void setCurrentGateValue(String s) {
        currentGate.setValue(s);
    }

    public void setSelectedVehicleValue(CheckingScrapModel model) {
        selectedVehicle.setValue(model);
    }
    public void setOutPortValue(String s){
        outPort.setValue(s);
    }

    public void getListGateByAPI() {
        isLoading.setValue(true);
        Call<ListResponeMessage<GateModel>> call3 = apiInterface.GetGateList(WareHouse.key, WareHouse.token);
        call3.enqueue(new Callback<ListResponeMessage<GateModel>>() {
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
                }
            }

            @Override
            public void onFailure(Call<ListResponeMessage<GateModel>> call, Throwable t) {
            }
        });
    }

    public void getListVehicleLostTagAPI() {
        isLoading.setValue(true);
//        Call<ListResponeMessage<CheckingScrapModel>> call = apiInterface.GetListVehicleLostTag(WareHouse.key, WareHouse.token, 1, false);
        Call<ListResponeMessage<CheckingScrapModel>> call= apiInterface.GetListCheckingScrapKL(WareHouse.key, WareHouse.token, "1", 0, currentGate.getValue());
        call.enqueue(new Callback<ListResponeMessage<CheckingScrapModel>>() {
            @Override
            public void onResponse(Call<ListResponeMessage<CheckingScrapModel>> call, Response<ListResponeMessage<CheckingScrapModel>> response) {
                if (response.body().getIsSuccess()) {
                    List<CheckingScrapModel> temp = response.body().getData();
                    ArrayList<CheckingScrapModel> t = new ArrayList<>(temp);
                    listData.setValue(t);
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<ListResponeMessage<CheckingScrapModel>> call, Throwable t) {
                listData.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        });
    }

    public void approveVehicleOut() {
        isLoading.setValue(true);
        Call<AcctionMessage> call = apiInterface.SaveVehicleOut(WareHouse.key, WareHouse.token, selectedVehicle.getValue().getRfid(), outPort.getValue(), WareHouse.UserId);
        call.enqueue(new Callback<AcctionMessage>() {
            @Override
            public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                try {
                    AcctionMessage responseMessage = response.body();
                    if (responseMessage.getIsSuccess())
                        textResultSuccess.setValue(responseMessage.getErr().getMsgString().toString());
                    else
                        textResultFailed.setValue(responseMessage.getErr().getMsgString().toString());
                } catch (Exception e) {
                    Log.i("responseException", e.toString());
                    textResultFailed.setValue("LỖI !!!, vui lòng thử lại");
                }
                isLoading.setValue(false);
                selectedVehicle.setValue(new CheckingScrapModel());
            }

            @Override
            public void onFailure(Call<AcctionMessage> call, Throwable t) {
                isLoading.setValue(false);
                selectedVehicle.setValue(new CheckingScrapModel());
                textResultFailed.setValue("Không lấy được dữ liệu");
            }
        });
    }

}