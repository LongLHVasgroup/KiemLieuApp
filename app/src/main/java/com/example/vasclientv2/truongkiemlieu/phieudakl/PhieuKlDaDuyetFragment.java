package com.example.vasclientv2.truongkiemlieu.phieudakl;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vasclientv2.R;
import com.example.vasclientv2.adapter.RejectedScaleTicketAdapter;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.KLCheckingScrap;
import com.example.vasclientv2.model.entities.ProductModel;
import com.example.vasclientv2.model.entities.ScaleTicketPODetailModel;
import com.example.vasclientv2.truongkiemlieu.DestroyPhieuKLActivity;
import com.example.vasclientv2.truongkiemlieu.TruongKiemLieuConfirmActivity;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhieuKlDaDuyetFragment extends Fragment implements RejectedScaleTicketAdapter.OnItemClickListener {

    private static final String TAG = "PhieuCanDaDuyetFrag";
    private RecyclerView recyclerView;
    private RejectedScaleTicketAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private ArrayList<CheckingScrapModel> list = new ArrayList<>();
    private ApiInterface apiInterface;
    private TextView txtNone;

    private PhieuKlDaDuyetViewModel phieuKlDaDuyetViewModel;
    private TransferData transferData;

    public static PhieuKlDaDuyetFragment newInstance() {
        return new PhieuKlDaDuyetFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        phieuKlDaDuyetViewModel = new ViewModelProvider(getActivity()).get(PhieuKlDaDuyetViewModel.class);
        View root = inflater.inflate(R.layout.phieu_kl_da_duyet_fragment, container, false);


        recyclerView = root.findViewById(R.id.rcvChecked);
        // init reference
        transferData = TransferData.getInstance(getActivity());

        apiInterface = ApiService.getClient().create(ApiInterface.class);

        phieuKlDaDuyetViewModel.getList().observe(getViewLifecycleOwner(), new Observer<ArrayList<CheckingScrapModel>>() {
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
        // show dialog khi người dùng bấm vào phiếu kiểm liệu
        progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        progressDialog.setMessage(getResources().getString(R.string.please_wait)); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        // Vuốt xuống để refresh lại dữ liệu
        swipeRefreshLayout = root.findViewById(R.id.sw_rcvListScaleTicketConfirmed);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getlistPhieuCan();
                // Ngừng refresh
            }
        });
        //
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new RejectedScaleTicketAdapter(list, this);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // textview show when not have Phiếu kiểm liệu cần duyệt
        txtNone = root.findViewById(R.id.txtNone);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(PhieuKlDaDuyetViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onItemClick(int position) {
        progressDialog.show();
        callApiGetScaleATickitDetails(list.get(position).getRfid(), list.get(position).getUser3Id(), list.get(position).getScaleTicketCode(), list.get(position).getScaleTicketMobileId());

    }

    public void onResume() {
        super.onResume();
        // Lấy dánh sách phiếu cân
        getlistPhieuCan();
        progressDialog.dismiss();
    }

    private void getlistPhieuCan() {
        // Step 4: Phiếu KL đã duyệt
        Call<ListResponeMessage<CheckingScrapModel>> call3 = apiInterface.GetListCheckingScrapKL(WareHouse.key, WareHouse.token, "1", 4, "1");
        call3.enqueue(new Callback<ListResponeMessage<CheckingScrapModel>>() {
            @Override
            public void onResponse(Call<ListResponeMessage<CheckingScrapModel>> call, Response<ListResponeMessage<CheckingScrapModel>> response) {
                try {
                    if (response.body().getIsSuccess()) {
                        List<CheckingScrapModel> temp = response.body().getData();
                        ArrayList<CheckingScrapModel> t = new ArrayList<>(temp);
                        phieuKlDaDuyetViewModel.setListData(t);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e + "");
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ListResponeMessage<CheckingScrapModel>> call, Throwable t) {

                phieuKlDaDuyetViewModel.setListData(new ArrayList<>());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private List<ProductModel> getProductList(List<ProductModel> productList, List<ScaleTicketPODetailModel> scaleTicketPODetailList) {
        List<ProductModel> productModels = productList;
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

    private void callApiGetScaleATickitDetails(String rfid, String userId3, String scaleTicketCode, String scaleTicketMobileId) {
        Call<SingleResponeMessage<KLCheckingScrap>> callCheckingVehicle = apiInterface.GetCheckingScrapTKL(WareHouse.key, WareHouse.token, rfid, scaleTicketCode);
        callCheckingVehicle.enqueue(new Callback<SingleResponeMessage<KLCheckingScrap>>() {
            @Override
            public void onResponse(Call<SingleResponeMessage<KLCheckingScrap>> call, Response<SingleResponeMessage<KLCheckingScrap>> response) {
                SingleResponeMessage<KLCheckingScrap> responseMessage = response.body();
                try {
                    if (responseMessage.getIsSuccess()) {

                        KLCheckingScrap data = responseMessage.getItem();
                        Intent intent = new Intent(getActivity(), DestroyPhieuKLActivity.class);
                        intent.putExtra("RFID", rfid);
                        intent.putExtra("SCALE_TICKET", data.getScaleTicket());
                        intent.putExtra("SCALE_TICKET_CODE", data.getScaleTicket().getScaleTicketCode());
                        intent.putExtra("VEHICLE_NUMBER", data.getVehicleModel().getVehicleNumber());
                        intent.putExtra("TYPE_TEXT", data.getVehicleModel().getTypeText());
                        intent.putExtra("IN_HOUR", data.getCheckingScrap().getInHourGuard());
                        intent.putExtra("SCALE_TICKET_PODETAIL_LIST", (Serializable) data.getScaleTicketPODetailList());
                        intent.putExtra("HISTORY", (Serializable) data.getHistory());
                        intent.putExtra("DUYET_PKL", data.getIsDaDuyet());
//                        intent.putExtra("PRODUCTS", (Serializable) data.getProductList());
                        intent.putExtra("PRODUCTS", (Serializable) (Serializable) getProductList(data.getProductList(), data.getScaleTicketPODetailList()));
                        intent.putExtra("USER_ID_3", userId3);
                        intent.putExtra("SCALE_TICKET_MOBILE_ID", scaleTicketMobileId);
                        intent.putExtra("WAREHOUSE", (Serializable) data.getWarehouse());
                        startActivity(intent);

                    } else {
                        Toast.makeText(getActivity(), responseMessage.getErr().getMsgString() + "", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    Log.e("KiemLieuFragment", "" + e);
                    Toast.makeText(getActivity(), "không thể kết nối tới server!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<SingleResponeMessage<KLCheckingScrap>> call, Throwable t) {
                Toast.makeText(getActivity(), "không thể kết nối tới server!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }
}