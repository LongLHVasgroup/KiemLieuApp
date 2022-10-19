package com.example.vasclientv2.ui.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vasclientv2.R;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.TempTable;
import com.example.vasclientv2.ui.WareHouse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePassViewModel extends ViewModel {

    private MutableLiveData<UpdatePassFormState> updatePassFormState = new MutableLiveData<>();
    private MutableLiveData<AcctionMessage> updatePasswordResult = new MutableLiveData<>();


    public LiveData<UpdatePassFormState> getUpdatePassFormState() {
        return updatePassFormState;
    }

    public LiveData<AcctionMessage> getUpdatePasswordResult() {
        return updatePasswordResult;
    }

    public void updatePassword(TempTable updatePassModel){
        ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
        Call<AcctionMessage> callChangePass = apiInterface.PasswordUpdate(WareHouse.key, WareHouse.token, updatePassModel);
        callChangePass.enqueue(new Callback<AcctionMessage>() {
            @Override
            public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                try {
                    AcctionMessage responseMessage = response.body();
                    updatePasswordResult.setValue(responseMessage);
                } catch (Exception e) {
                    Log.i("responseException", e.toString());
                }
            }

            @Override
            public void onFailure(Call<AcctionMessage> call, Throwable t) {

            }
        });
    }

    public void updatePassDataChanged(String newPass, String repeatPass) {
        if (!isNewPassValid(newPass)) {
            updatePassFormState.setValue(new UpdatePassFormState(R.string.invalid_new_pass, null));
        } else if (!isRepeatPassValid(newPass,repeatPass)) {
            updatePassFormState.setValue(new UpdatePassFormState(null, R.string.invalid_repeat_pass));
        } else {
            updatePassFormState.setValue(new UpdatePassFormState(true));
        }
    }


    // A placeholder repeat password validation check
    private boolean isNewPassValid(String newPass) {
        return newPass != null && newPass.trim().length() >= 3;
    }

    // A placeholder new pass equal repeat pass validation check
    private boolean isRepeatPassValid(String newPass, String repeatPass) {
        return repeatPass != null && repeatPass.equals(newPass.trim());
    }
}
