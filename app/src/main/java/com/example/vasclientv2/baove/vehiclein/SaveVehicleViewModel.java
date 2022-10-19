package com.example.vasclientv2.baove.vehiclein;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.entities.SaveVehicle;
import com.example.vasclientv2.model.entities.TagInfoModel;
import com.example.vasclientv2.ui.WareHouse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveVehicleViewModel extends ViewModel {

    private MutableLiveData<String> rfid, successText, unSuccessText;
    private MutableLiveData<Boolean> isLoading, isNewTag;
    private MutableLiveData<SaveVehicle> saveVehicleLiveData;
    private ApiInterface apiInterface;
    private Boolean isSaving;

    public SaveVehicleViewModel() {
        rfid = new MutableLiveData<>();
        successText = new MutableLiveData<>();
        unSuccessText = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        isSaving = false;
        isNewTag = new MutableLiveData<>();
        saveVehicleLiveData = new MutableLiveData<>();


        apiInterface = ApiService.getClient().create(ApiInterface.class);
    }

    public void setRfid(MutableLiveData<String> rfid) {
        this.rfid = rfid;
    }

    public MutableLiveData<String> getSuccessText() {
        return successText;
    }

    public void setSuccessText(MutableLiveData<String> successText) {
        this.successText = successText;
    }

    public MutableLiveData<String> getUnSuccessText() {
        return unSuccessText;
    }

    public void setUnSuccessText(MutableLiveData<String> unSuccessText) {
        this.unSuccessText = unSuccessText;
    }

    public MutableLiveData<SaveVehicle> getSaveVehicleLiveData() {
        return saveVehicleLiveData;
    }

    public void setSaveVehicleLiveData(SaveVehicle saveVehicleLiveData) {
        this.saveVehicleLiveData.setValue(saveVehicleLiveData);
    }

    public MutableLiveData<String> getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid.setValue(rfid);
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(MutableLiveData<Boolean> isLoading) {
        this.isLoading = isLoading;
    }

    public MutableLiveData<Boolean> getIsNewTag() {
        return isNewTag;
    }

    public void setIsNewTag(MutableLiveData<Boolean> isNewTag) {
        this.isNewTag = isNewTag;
    }

    public void setRfidForSaveModel() {
        SaveVehicle saveModel = saveVehicleLiveData.getValue();
        saveModel.setRFID(rfid.getValue());
        this.saveVehicleLiveData.setValue(saveModel);
    }

    public void saveVehicle() {
        isSaving = true;
        isLoading.setValue(true);
        Call<AcctionMessage> call3 = apiInterface.SaveVehicleIn(WareHouse.key, WareHouse.token, saveVehicleLiveData.getValue(), WareHouse.UserId);
        call3.enqueue(new Callback<AcctionMessage>() {
            @Override
            public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                try {
                    AcctionMessage responseMessage = response.body();
                    if (responseMessage.getIsSuccess()) {
                        successText.setValue(responseMessage.getErr().getMsgString().toString());
                    } else {
                        unSuccessText.setValue(responseMessage.getErr().getMsgString().toString());
                    }
                } catch (Exception e) {
                    unSuccessText.setValue("Vui lòng thử lại");
                }
                isLoading.setValue(false);
                isSaving = false;
            }

            @Override
            public void onFailure(Call<AcctionMessage> call, Throwable t) {
                call.cancel();
                unSuccessText.setValue(t + "");
                isLoading.setValue(false);
                isSaving = false;
            }

        });


    }

//    public Boolean checkIsNewTag(TagInfoModel tagInfoModel) {
//        return tagInfoModel.getTagNumber().equals("");
//    }

//    public void checkIsNewTag() {
//        if (!isSaving && !isGetingTagInfo) {
//            isGetingTagInfo = true;
//            isLoading.setValue(true);
//            Call<ListResponeMessage<TagInfoModel>> call = apiInterface.GetTagInfo(WareHouse.key, WareHouse.token, rfid.getValue());
//            call.enqueue(new Callback<ListResponeMessage<TagInfoModel>>() {
//                @Override
//                public void onResponse(Call<ListResponeMessage<TagInfoModel>> call, Response<ListResponeMessage<TagInfoModel>> response) {
//                    try {
//                        if (response.body().getIsSuccess()) {
//                            if (response.body().getData().isEmpty()) {
//                                isNewTag.setValue(true);
//                                isLoading.setValue(false);
//                            } else
//                                isNewTag.setValue(false);
//
//                        } else {
//                            unSuccessText.setValue("Không lấy đươc thông tin thẻ");
//                            isLoading.setValue(false);
//                        }
//
//                    } catch (Exception e) {
//                        unSuccessText.setValue("LỖI!. " + e);
//                        isLoading.setValue(false);
//                    }
//                    isGetingTagInfo = false;
//                }
//
//                @Override
//                public void onFailure(Call<ListResponeMessage<TagInfoModel>> call, Throwable t) {
//                    unSuccessText.setValue("Không thể kết nối tới Server");
//                    isLoading.setValue(false);
//                    isGetingTagInfo = false;
//                }
//            });
//        }
//
//    }

}
