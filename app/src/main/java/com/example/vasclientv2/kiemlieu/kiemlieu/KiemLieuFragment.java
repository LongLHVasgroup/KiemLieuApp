package com.example.vasclientv2.kiemlieu.kiemlieu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.vasclientv2.R;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.KLCheckingScrap;
import com.example.vasclientv2.model.entities.ProductModel;
import com.example.vasclientv2.model.entities.ScaleTicketPODetailModel;
import com.example.vasclientv2.ui.WareHouse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KiemLieuFragment extends Fragment {

    private KiemLieuViewModel kiemLieuViewModel;
    private TextView txtAlertResult;
    private ApiInterface apiInterface;
    private ProgressDialog progressDialog;
    private Call<SingleResponeMessage<KLCheckingScrap>> callCheckingVehicle;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        kiemLieuViewModel =
                new ViewModelProvider(getActivity()).get(KiemLieuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_kiemlieu, container, false);

        apiInterface = ApiService.getClient().create(ApiInterface.class);

        txtAlertResult = root.findViewById(R.id.txtAlertResult);
        kiemLieuViewModel.getAlertResult().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                txtAlertResult.setText(s);
            }
        });

        kiemLieuViewModel.getRFID().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                callCheckingVehicle = apiInterface.GetCheckingScrapKL(WareHouse.key, WareHouse.token, s);
                checkInfoNFCTag(s);
            }
        });

        //
        progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        progressDialog.setMessage(getResources().getString(R.string.please_wait)); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner


        return root;
    }

    private void checkInfoNFCTag(String rfid) {
        Log.d("checkInfoNFCTag", "rfid: " + rfid);
        if (rfid != null) {
            try {
                progressDialog.show(); // Display Progress Dialog
                // TODO: handle loggedInUser authentication
                // Call API check vehicle on server
//                ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
                callCheckingVehicle.enqueue(new Callback<SingleResponeMessage<KLCheckingScrap>>() {
                    @Override
                    public void onResponse(Call<SingleResponeMessage<KLCheckingScrap>> call, Response<SingleResponeMessage<KLCheckingScrap>> response) {
                        SingleResponeMessage<KLCheckingScrap> responseMessage = response.body();
                        try {
                            if (responseMessage.getIsSuccess()) {
                                // Change Activity
//                            change2AddInfoKLActivity();


                                KLCheckingScrap data = responseMessage.getItem();
                                Intent intent = new Intent(getActivity().getApplicationContext(), AddInfoKLActivity.class);

//                                ProductModel ArrayList<productModel> = new ArrayList<ProductModel>;
//                                productModel = getProductList(data.getProductList(), data.getScaleTicketPODetailList());
                                intent.putExtra("RFID", rfid);
                                intent.putExtra("SCALE_TICKET", data.getScaleTicket());
                                intent.putExtra("SCALE_TICKET_CODE", data.getScaleTicket().getScaleTicketCode());
                                intent.putExtra("VEHICLE_NUMBER", data.getVehicleModel().getVehicleNumber());
                                intent.putExtra("TYPE_TEXT", data.getVehicleModel().getTypeText());
                                intent.putExtra("IN_HOUR", data.getCheckingScrap().getInHourGuard());
                                intent.putExtra("SCALE_TICKET_PODETAIL_LIST", (Serializable) data.getScaleTicketPODetailList());
                                intent.putExtra("HISTORY", (Serializable) data.getHistory());
                                intent.putExtra("DUYET_PKL", data.getIsDaDuyet());
                                intent.putExtra("PRODUCTS", (Serializable) getProductList(data.getProductList(), data.getScaleTicketPODetailList()));
                                intent.putExtra("WAREHOUSE", (Serializable) data.getWarehouse());
                                startActivity(intent);
//                            getActivity().getIntent().removeExtra("RFIDKL");

                            } else {
                                kiemLieuViewModel.setAlertResult(responseMessage.getErr().getMsgString().toString());
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            Log.e("KiemLieuFragment", "" + e);
                            Toast.makeText(getActivity(), "không thể kết nối tới server! " + e, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(Call<SingleResponeMessage<KLCheckingScrap>> call, Throwable t) {
                        kiemLieuViewModel.setAlertResult("Không lấy được thông tin từ máy chủ");
                        progressDialog.dismiss();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Không lấy được thông tin từ máy chủ", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }


    }

    private List<ProductModel> getProductList(List<ProductModel> productList, List<ScaleTicketPODetailModel> scaleTicketPODetailList) {
        List<ProductModel> productModels = productList;
//         productModels.addAll(productList);
        for (ScaleTicketPODetailModel scaleTicketPODetailModel : scaleTicketPODetailList) {
            Boolean dif = false;
            for (ProductModel productListReceive : productList) {
                if (scaleTicketPODetailModel.getProductCode().equals(productListReceive.getProductCode())) {
                    dif = true;
                    break;

                }
            }
            if (!dif) {
                ProductModel productModel = new ProductModel();
                productModel.setProductCode(scaleTicketPODetailModel.getProductCode());
                productModel.setProductName(scaleTicketPODetailModel.getProductName());
                productModels.add(productModel);
            }
        }
        return productModels;
    }

    private void change2AddInfoKLActivity() {
        Intent i = new Intent(getActivity(), AddInfoKLActivity.class);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        kiemLieuViewModel.setAlertResult("");
        kiemLieuViewModel.setRFID(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        callCheckingVehicle.cancel();
        progressDialog.dismiss();
    }
}