package com.example.vasclientv2.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.data.LoginRepository;
import com.example.vasclientv2.data.Result;
import com.example.vasclientv2.data.model.LoggedInUser;
import com.example.vasclientv2.R;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.UserModel;
import com.example.vasclientv2.ui.WareHouse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        try {
            // TODO: handle loggedInUser authentication            // Gọi API Login
            ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
            Call<SingleResponeMessage<UserModel>> call3 = apiInterface.GetUser(WareHouse.key, WareHouse.token, username, password);
            call3.enqueue(new Callback<SingleResponeMessage<UserModel>>() {
                @Override
                public void onResponse(Call<SingleResponeMessage<UserModel>> call, retrofit2.Response<SingleResponeMessage<UserModel>> response) {
                    if (response.body().getIsSuccess()) {
                        UserModel data = response.body().getItem();
                        if (response.body().getErr().getMsgCode() != null) {
                            if (response.body().getErr().getMsgCode().equals("113")) {
                                loginResult.setValue(new LoginResult(true, data));
                            } else {
                                loginResult.setValue(new LoginResult(null, data));
                            }
                        } else {
                            loginResult.setValue(new LoginResult(null, data));
                        }
                    } else {
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                }

                @Override
                public void onFailure(Call<SingleResponeMessage<UserModel>> call, Throwable t) {
                    loginResult.setValue(new LoginResult(R.string.network_error));
                }
            });

        } catch (Exception e) {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String ip, String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, null, R.string.invalid_password));
        } else if (!isIpValid(ip)) {
            loginFormState.setValue(new LoginFormState(R.string.ipError, null, null));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 3;
    }

    // A placeholder ip server validation check
    private boolean isIpValid(String ip) {
        return ip != null;
    }
}