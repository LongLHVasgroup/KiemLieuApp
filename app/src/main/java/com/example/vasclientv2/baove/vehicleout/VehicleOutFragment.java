package com.example.vasclientv2.baove.vehicleout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.vasclientv2.R;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.data.Result;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.UserModel;
import com.example.vasclientv2.ui.WareHouse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VehicleOutFragment extends Fragment {

    private VehicleOutViewModel vehicleOutViewModel;

    private TextView txtAlertResult;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vehicleOutViewModel =
                new ViewModelProvider(getActivity()).get(VehicleOutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_vehicle_out, container, false);
        txtAlertResult  = root.findViewById(R.id.txtAlertResult);
        vehicleOutViewModel.getAlertResult().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                txtAlertResult.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        vehicleOutViewModel.setAlertResult("");
    }

    protected void checkVehicleOut(String rfid){

        Toast.makeText(getActivity(),rfid,Toast.LENGTH_LONG).show();


//        if (rfid != null){
//            try {
//                // TODO: handle loggedInUser authentication
//                //
//                ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
//                Call<AcctionMessage> call3 = apiInterface.SaveVehicleOut(WareHouse.key,WareHouse.token,rfid,"PORT1",WareHouse.UserId );
//                call3.enqueue(new Callback<AcctionMessage>() {
//                    @Override
//                    public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
//                        try {
//                            AcctionMessage responseMessage = response.body();
//                            if (responseMessage.getIsSuccess()) {
//                                Toast.makeText(getActivity(),responseMessage.getErr().getMsgString().toString(),Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(getActivity(),responseMessage.getErr().getMsgString().toString(),Toast.LENGTH_LONG).show();
//                            }
//                        } catch (Exception e) {
//                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<AcctionMessage> call, Throwable t) {
//                        Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_LONG).show();
//                        call.cancel();
//                    }
//
//                });
//
//            } catch (Exception e) {
//                Toast.makeText(getActivity(),"ERR CALL API",Toast.LENGTH_LONG).show();
//            }
//        }

//
//        if (rfid != null) {
//            Call<SingleResponeMessage<CheckingScrapModel>> call3 = apiInterface.GetVehicleNumber(KeyConst, Token, RFID);
//            call3.enqueue(new Callback<SingleResponeMessage<CheckingScrap>>() {
//                @Override
//                public void onResponse(Call<SingleResponeMessage<CheckingScrap>> call, Response<SingleResponeMessage<CheckingScrap>> response) {
//                    try {
//                        Log.d("TAG", response.code() + "");
//                        SingleResponeMessage<CheckingScrap> responseMessage = response.body();
//                        if (responseMessage.getIsSuccess()) {
//                            CheckingScrap data = responseMessage.getItem();
//                            new AlertDialog.Builder(getContext())
//                                    .setTitle("Xe ra")
//                                    .setMessage("Xe \" " + (data.getVehicleNumber().isEmpty()?"":data.getVehicleNumber()) + " \" đang chuẩn bị ra ?")
//
//                                    // Specifying a listener allows you to take an action before dismissing the dialog.
//                                    // The dialog is automatically dismissed when a dialog button is clicked.
//                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
//                                        Call<AcctionMessage> callOut = apiInterface.SaveVehicleOut(KeyConst,Token,RFID,"PORT1",UserId);
//                                        callOut.enqueue(new Callback<AcctionMessage>() {
//                                            @Override
//                                            public void onResponse(Call<AcctionMessage> call1, Response<AcctionMessage> response1) {
//                                                try {
//                                                    Log.d("TAG", response1.code() + "");
//                                                    AcctionMessage responseMessage = response1.body();
//                                                    if (responseMessage.getIsSuccess()) {
//                                                        tv.setText(responseMessage.getErr().getMsgString().toString());
//                                                    } else {
//                                                        tv.setText(responseMessage.getErr().getMsgString().toString());
//                                                    }
//                                                } catch (Exception e) {
//                                                    tv.setText("Vui lòng thử lại rq2+ "+e.getMessage());
//                                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onFailure(Call<AcctionMessage> call1, Throwable t) {
//                                                tv.setText("Vui lòng thử lại rq2 + "+t.getMessage());
//                                                Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_LONG).show();
//                                                call1.cancel();
//                                            }
//
//                                        });
//                                    })
//
//                                    // A null listener allows the button to dismiss the dialog and take no further action.
//                                    .setNegativeButton(android.R.string.no, null)
//                                    .setIcon(android.R.drawable.ic_dialog_info)
//                                    .show();
//                        } else {
//                            tv.setText(responseMessage.getErr().getMsgString().toString());
//                        }
//                    } catch (Exception e) {
//                        tv.setText("Vui lòng thử lại rq1");
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<SingleResponeMessage<CheckingScrap>> call, Throwable t) {
//                    tv.setText("Vui lòng thử lại");
//                    call.cancel();
//                }
//
//            });
//        } else {
//            tv.setText("");
//        }
    }


}