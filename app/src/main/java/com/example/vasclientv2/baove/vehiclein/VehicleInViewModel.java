package com.example.vasclientv2.baove.vehiclein;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.VehicleModel;
import com.example.vasclientv2.ui.WareHouse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleInViewModel extends ViewModel {

    private MutableLiveData<String> txtBienSoXe;
    private MutableLiveData<String> txtWarnBSXEmpty, txtAlertResult;
    private MutableLiveData<List<String>> listBSX;
    private MutableLiveData<Boolean> isloading;
    private MutableLiveData<VehicleModel> vehicleModelLiveData;

    private ApiInterface apiInterface;

    public VehicleInViewModel() {
        txtBienSoXe = new MutableLiveData<>();
        txtWarnBSXEmpty = new MutableLiveData<>();
        txtAlertResult = new MutableLiveData<>();
        listBSX = new MutableLiveData<>();
        isloading = new MutableLiveData<>(false);
        vehicleModelLiveData = new MutableLiveData<>();

        apiInterface = ApiService.getClient().create(ApiInterface.class);
    }

    public MutableLiveData<Boolean> getIsloading() {
        return isloading;
    }

    public void setIsloading(MutableLiveData<Boolean> isloading) {
        this.isloading = isloading;
    }

    public MutableLiveData<VehicleModel> getVehicleModelLiveData() {
        return vehicleModelLiveData;
    }

    public void setVehicleModelLiveData(VehicleModel vehicleModelLiveData) {
        this.vehicleModelLiveData.setValue(vehicleModelLiveData);
    }

    public MutableLiveData<String> getTxtAlertResult() {
        return txtAlertResult;
    }

    public void setTxtAlertResult(String txtAlertResult) {
        this.txtAlertResult.setValue(txtAlertResult);
    }

    public void setBienSoXe(String s) {
        txtBienSoXe.setValue(s);
    }

    public void setWarnBSXEmpty(String s) {
        txtWarnBSXEmpty.setValue(s);
    }

    public LiveData<String> getBienSoXe() {
        return txtBienSoXe;
    }

    public LiveData<String> getWarnBSXEmpty() {
        return txtWarnBSXEmpty;
    }


    public LiveData<List<String>> getlistBSX() {
        return listBSX;
    }

    // API lấy ds xe có trong DB dựa trên số xe nhập vào
    public void getDataBSX(String bsxInput) {
        try {
            // TODO: handle loggedInUser authentication
            // Call API check vehicle on server
            Call<ListResponeMessage<VehicleModel>> callApiGetBSX = apiInterface.getListVehicalNumber(WareHouse.key, WareHouse.token, bsxInput);
            callApiGetBSX.enqueue(new Callback<ListResponeMessage<VehicleModel>>() {
                @Override
                public void onResponse(Call<ListResponeMessage<VehicleModel>> call, Response<ListResponeMessage<VehicleModel>> response) {
                    if (response.body().getIsSuccess()) {
                        ArrayList<String> strings = new ArrayList<>();
                        for (VehicleModel vehicleModel : response.body().getData()) {
                            strings.add(vehicleModel.getVehicleNumber());
                        }
                        listBSX.setValue(strings);
                    }
                }

                @Override
                public void onFailure(Call<ListResponeMessage<VehicleModel>> call, Throwable t) {

                }
            });

        } catch (Exception e) {

        }
    }

    // API lấy thông tin xe
    public void getVehicleInfo(String bienSoXe) {
        isloading.setValue(true);
        Call<SingleResponeMessage<VehicleModel>> callGetVehicleInfo = apiInterface.GetVehicleInfo(WareHouse.key, WareHouse.token, bienSoXe);
        callGetVehicleInfo.enqueue(new Callback<SingleResponeMessage<VehicleModel>>() {
            @Override
            public void onResponse(Call<SingleResponeMessage<VehicleModel>> call, Response<SingleResponeMessage<VehicleModel>> response) {
                SingleResponeMessage<VehicleModel> responseMessage = response.body();
                if (responseMessage.getIsSuccess()) {
                    VehicleModel vehicleModel = responseMessage.getItem();
                    if (vehicleModel != null) {
                        // Check đã có thông tin tài xế hay chưa, có rồi là đã đăng ký rồi
//                        if (vehicleModel.getDriverIdCard() == null || vehicleModel.getDriverName()== null) {
//                            txtAlertResult.setValue("Xe chưa được khai báo giao nhận, Không đươc phép vào cân!");
//                        } else
                        vehicleModelLiveData.setValue(vehicleModel);
                    } else {
                        txtAlertResult.setValue(responseMessage.getErr().getMsgString().toString());
                    }
                }
                isloading.setValue(false);
            }

            @Override
            public void onFailure(Call<SingleResponeMessage<VehicleModel>> call, Throwable t) {
                txtAlertResult.setValue("LỖI!!!" + t);
                isloading.setValue(false);
            }
        });
    }

    // API lấy thông tin xe dã đăng ký trên web
    public void getVehicleInfoWeb(String bienSoXe) {
        isloading.setValue(true);
        Call<SingleResponeMessage<VehicleModel>> callGetVehicleInfo = apiInterface.GetListVehicleRegister(WareHouse.key, WareHouse.token, bienSoXe);
        callGetVehicleInfo.enqueue(new Callback<SingleResponeMessage<VehicleModel>>() {
            @Override
            public void onResponse(Call<SingleResponeMessage<VehicleModel>> call, Response<SingleResponeMessage<VehicleModel>> response) {
                SingleResponeMessage<VehicleModel> responseMessage = response.body();
                try {
                    if (responseMessage.getIsSuccess()) {
                        VehicleModel vehicleModel = responseMessage.getItem();
                        if (vehicleModel != null) {
                            vehicleModelLiveData.setValue(vehicleModel);
                        } else {
                            txtAlertResult.setValue(responseMessage.getErr().getMsgString().toString());
                        }
                    } else {
                        txtAlertResult.setValue(responseMessage.getErr().getMsgString().toString());
                    }
                } catch (Exception e) {
                    txtAlertResult.setValue(e + "");
                }

                isloading.setValue(false);
            }

            @Override
            public void onFailure(Call<SingleResponeMessage<VehicleModel>> call, Throwable t) {
                txtAlertResult.setValue("LỖI!!!" + t);
                isloading.setValue(false);
            }
        });
    }
}