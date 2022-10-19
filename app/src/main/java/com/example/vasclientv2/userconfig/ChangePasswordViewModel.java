package com.example.vasclientv2.userconfig;

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


public class ChangePasswordViewModel extends ViewModel {

    private MutableLiveData<ChangePassFormState> changePassFormState = new MutableLiveData<>();
    private MutableLiveData<AcctionMessage> changePasswordResult = new MutableLiveData<>();


    LiveData<ChangePassFormState> getChangePassFormState() {
        return changePassFormState;
    }

    LiveData<AcctionMessage> getChangePasswordResult() {
        return changePasswordResult;
    }

    public void changePassword(TempTable changPassModel){


        ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
        Call<AcctionMessage> callChangePass = apiInterface.PasswordChange(WareHouse.key, WareHouse.token, changPassModel);
        callChangePass.enqueue(new Callback<AcctionMessage>() {
            @Override
            public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                try {
                    AcctionMessage responseMessage = response.body();
                    changePasswordResult.setValue(responseMessage);
                } catch (Exception e) {
                    Log.i("responseException", e.toString());
                }
            }

            @Override
            public void onFailure(Call<AcctionMessage> call, Throwable t) {

            }
        });
    }


    public void changePassDataChanged(String currentPass, String newPass, String repeatPass) {
        if (!isCurrentPassValid(currentPass)) {
            changePassFormState.setValue(new ChangePassFormState(R.string.invalid_current_pass, null, null));
        } else if (!isNewPassValid(newPass)) {
            changePassFormState.setValue(new ChangePassFormState(null, R.string.invalid_new_pass, null));
        } else if (!isRepeatPassValid(newPass, repeatPass)) {
            changePassFormState.setValue(new ChangePassFormState(null, null, R.string.invalid_repeat_pass));
        } else {
            changePassFormState.setValue(new ChangePassFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isCurrentPassValid(String currentPass) {
        return currentPass != null && currentPass.trim().length() >= 3;
    }

    // A placeholder password validation check
    private boolean isNewPassValid(String newPass) {
        return newPass != null && newPass.trim().length() >= 3;
    }

    // A placeholder ip server validation check
    private boolean isRepeatPassValid(String newPass, String repeatPass) {
        return repeatPass != null && repeatPass.equals(newPass.trim());
    }
}
