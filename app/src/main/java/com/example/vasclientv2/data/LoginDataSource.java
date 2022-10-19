package com.example.vasclientv2.data;


import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.UserModel;
import com.example.vasclientv2.ui.WareHouse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<UserModel> login(String username, String password) {
        try {
            // TODO: handle loggedInUser authentication
//            UserModel user = new UserModel();
//            Exception e = new Exception();
            // Gọi API Login
            ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
            Call<SingleResponeMessage<UserModel>> call3 = apiInterface.GetUser(WareHouse.key,WareHouse.token,username, password);
            call3.enqueue(new Callback<SingleResponeMessage<UserModel>>() {
                @Override
                public void onResponse(Call<SingleResponeMessage<UserModel>> call, retrofit2.Response<SingleResponeMessage<UserModel>> response) {
                    if (response.body().getIsSuccess()){
                        UserModel user = response.body().getItem();
//                        return new Result.Success<>(user);
                    }else{
//                        user =null;
                    }
                }
                @Override
                public void onFailure(Call<SingleResponeMessage<UserModel>> call, Throwable t) {
//                    return new Result.Error(new IOException("Error logging in", t));
                }
            });

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
        return new Result.Success<>(null);
    }

    public void logout() {
        // TODO: revoke authentication
    }
}