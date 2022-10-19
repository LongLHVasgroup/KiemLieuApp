package com.example.vasclientv2.truongkiemlieu.phieucanchuakl;

import android.content.Intent;
import android.content.RestrictionEntry;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vasclientv2.R;
import com.example.vasclientv2.adapter.CheckingScrapAdapter;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.kiemlieu.kiemlieu.AddInfoKLActivity;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.KLCheckingScrap;
import com.example.vasclientv2.ui.WareHouse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhieuCanChuaKLFragment extends Fragment implements CheckingScrapAdapter.OnItemClickListener {
    private static final String TAG = "PhieuCanChuaKL";
    private RecyclerView recyclerView;
    private CheckingScrapAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CheckingScrapModel> list = new ArrayList<>();
    private ApiInterface apiInterface;
    private TextView txtNone;

    private PhieuCanChuaKLViewModel phieuCanChuaKLViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        phieuCanChuaKLViewModel =
                new ViewModelProvider(getActivity()).get(PhieuCanChuaKLViewModel.class);
        View root = inflater.inflate(R.layout.tkl_fragment_chuakl, container, false);
        recyclerView = root.findViewById(R.id.rcvWait);
        apiInterface = ApiService.getClient().create(ApiInterface.class);
        Call<ListResponeMessage<CheckingScrapModel>> call3 = apiInterface.GetListCheckingScrapKL(WareHouse.key, WareHouse.token, "1", 2, "1");
        call3.enqueue(new Callback<ListResponeMessage<CheckingScrapModel>>() {
            @Override
            public void onResponse(Call<ListResponeMessage<CheckingScrapModel>> call, Response<ListResponeMessage<CheckingScrapModel>> response) {
                try {
                    if (response.body().getIsSuccess()) {
                        List<CheckingScrapModel> temp = response.body().getData();
                        ArrayList<CheckingScrapModel> t = new ArrayList<>(temp);
                        phieuCanChuaKLViewModel.setListData(t);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e + "");
                }

            }

            @Override
            public void onFailure(Call<ListResponeMessage<CheckingScrapModel>> call, Throwable t) {

            }
        });
        //
        phieuCanChuaKLViewModel.getList().observe(getViewLifecycleOwner(), new Observer<ArrayList<CheckingScrapModel>>() {
            @Override
            public void onChanged(ArrayList<CheckingScrapModel> checkingScrapModels) {
                if (checkingScrapModels.size() == 0) {
                    txtNone.setVisibility(View.VISIBLE);
                } else txtNone.setVisibility(View.GONE);
                list = checkingScrapModels;
                adapter.setList(list);
                recyclerView.setAdapter(adapter);
            }
        });
        //
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new CheckingScrapAdapter(list, this);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Text thông báo chưa có phiếu kiểm liệu
        txtNone = root.findViewById(R.id.txtNone);

        return root;
    }

    @Override
    public void onItemClick(int position) {
//        String rfid = list.get(position).getRfid();
//        Log.d(TAG, "onItemClick: "+list.get(position).getVehicleNumber());
//        if (rfid != null) {
//            try {
//                // TODO: handle loggedInUser authentication
//                // Call API check vehicle on server
//                ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
//                Call<SingleResponeMessage<KLCheckingScrap>> callCheckingVehicle = apiInterface.GetCheckingScrapTKL(WareHouse.key, WareHouse.token, rfid);
//                callCheckingVehicle.enqueue(new Callback<SingleResponeMessage<KLCheckingScrap>>() {
//                    @Override
//                    public void onResponse(Call<SingleResponeMessage<KLCheckingScrap>> call, Response<SingleResponeMessage<KLCheckingScrap>> response) {
//                        SingleResponeMessage<KLCheckingScrap> responseMessage = response.body();
//                        if (responseMessage.getIsSuccess()) {
//                            // Change Activity
////                            change2AddInfoKLActivity();
//
//                            KLCheckingScrap data = responseMessage.getItem();
//                            Intent intent = new Intent(getActivity().getApplicationContext(), AddInfoKLActivity.class);
//                            intent.putExtra("RFID",rfid);
//                            intent.putExtra("SCALE_TICKET",data.getScaleTicket());
//                            intent.putExtra("SCALE_TICKET_CODE", data.getScaleTicket().getScaleTicketCode());
//                            intent.putExtra("VEHICLE_NUMBER", data.getVehicleModel().getVehicleNumber());
//                            intent.putExtra("TYPE_TEXT", data.getVehicleModel().getTypeText());
//                            intent.putExtra("IN_HOUR", data.getCheckingScrap().getInHourGuard());
//                            intent.putExtra("SCALE_TICKET_PODETAIL_LIST", (Serializable) data.getScaleTicketPODetailList());
//                            intent.putExtra("HISTORY", (Serializable) data.getHistory());
//                            intent.putExtra("DUYET_PKL",data.getIsDaDuyet());
//                            intent.putExtra("PRODUCTS", (Serializable) data.getProductList());
//                            startActivity(intent);
////                            getActivity().getIntent().removeExtra("RFIDKL");
//
//                        } else {
//                            phieuCanChuaKLViewModel.setAlertResult(responseMessage.getErr().getMsgString().toString());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<SingleResponeMessage<KLCheckingScrap>> call, Throwable t) {
//                        phieuCanChuaKLViewModel.setAlertResult("Không lấy được thông tin từ máy chủ");
//                    }
//                });
//
//            } catch (Exception e) {
//                Toast.makeText(getActivity(), "Không lấy được thông tin từ máy chủ", Toast.LENGTH_LONG).show();
//            }
//        }
    }
}