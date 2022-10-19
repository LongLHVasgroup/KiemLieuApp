package com.example.vasclientv2.admin.resetpass;

import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.UserModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassUserViewModel extends ViewModel {

    private MutableLiveData<ArrayList<UserModel>> listUser;
    private MutableLiveData<Boolean> isSuccess;
    private MutableLiveData<Boolean> isSuccessReset;

    public ResetPassUserViewModel() {
        listUser = new MutableLiveData<>();
        isSuccess = new MutableLiveData<>();
        isSuccessReset =new MutableLiveData<>();
    }
    public MutableLiveData<Boolean> getIsSuccess(){
        return isSuccess;
    }

    public MutableLiveData<Boolean> getIsSuccessReset(){
        return isSuccessReset;
    }

    public MutableLiveData<ArrayList<UserModel>> getListUser() {
        return listUser;
    }

    public void setListUser(ArrayList<UserModel> listUser) {
        this.listUser.setValue(listUser);
    }

    public void getData(Call<ListResponeMessage<UserModel>> callApiGetAllUser){
        try {
            // TODO: handle loggedInUser authentication
            // Call API check vehicle on server
            callApiGetAllUser.enqueue(new Callback<ListResponeMessage<UserModel>>() {
                @Override
                public void onResponse(Call<ListResponeMessage<UserModel>> call, Response<ListResponeMessage<UserModel>> response) {

                    try{
                        if (response.body().getIsSuccess()) {
                            List<UserModel> temp = response.body().getData();
                            listUser.setValue(new ArrayList<>(temp));
                            isSuccess.setValue(true);
                        } else {
                            isSuccess.setValue(false);
                        }
                    }catch (Exception e){

                    }

                }

                @Override
                public void onFailure(Call<ListResponeMessage<UserModel>> call, Throwable t) {
                    isSuccess.setValue(false);
                }
            });

        } catch (Exception e) {
            isSuccess.setValue(false);
        }
    }

    public void resetUser(Call<AcctionMessage> messageCall){
        try {
            // TODO: handle loggedInUser authentication
            // Call API check vehicle on server
            messageCall.enqueue(new Callback<AcctionMessage>() {

                @Override
                public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                    try {
                        isSuccessReset.setValue(response.body().getIsSuccess());
                    }catch (Exception e){
                        isSuccessReset.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<AcctionMessage> call, Throwable t) {
                    isSuccessReset.setValue(false);
                }
            });

        } catch (Exception e) {
            isSuccessReset.setValue(false);
        }
    }

}
