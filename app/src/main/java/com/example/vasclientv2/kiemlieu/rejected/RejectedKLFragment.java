package com.example.vasclientv2.kiemlieu.rejected;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.vasclientv2.R;
import com.example.vasclientv2.adapter.RejectedScaleTicketAdapter;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.kiemlieu.KiemLieuActivity;
import com.example.vasclientv2.kiemlieu.kiemlieu.AddInfoKLActivity;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.GateModel;
import com.example.vasclientv2.model.entities.KLCheckingScrap;
import com.example.vasclientv2.model.entities.ProductModel;
import com.example.vasclientv2.model.entities.ScaleTicketPODetailModel;
import com.example.vasclientv2.model.entities.WeightScaleModel;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RejectedKLFragment extends Fragment implements RejectedScaleTicketAdapter.OnItemClickListener {

    private static final String TAG = "RejectedKLFragment";
    private RejectedKLViewModel rejectedKLViewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private TextView txtNone;
    private ArrayList<CheckingScrapModel> checkingScrapModels;
    private LinearLayoutManager mLayoutManager;
    private RejectedScaleTicketAdapter mAdapter;

    private ApiInterface apiInterface;
    private Call<SingleResponeMessage<KLCheckingScrap>> callApiGetDetail;

    // Reference
    private TransferData transferData;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rejectedKLViewModel =
                new ViewModelProvider(getActivity()).get(RejectedKLViewModel.class);
        View root = inflater.inflate(R.layout.kl_fragment_rejected, container, false);
        progressBar = root.findViewById(R.id.loading);

        // init reference
        transferData = TransferData.getInstance(getActivity());

        // show dialog khi người dùng bấm vào phiếu kiểm liệu
        progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        progressDialog.setMessage(getResources().getString(R.string.please_wait)); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner

        // Vuốt xuống để refresh lại dữ liệu
        swipeRefreshLayout = root.findViewById(R.id.sw_rcvListScaleTicket);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadScaleTicketList();
                // Ngừng refresh
            }
        });

        recyclerView = root.findViewById(R.id.rejected_recylerview);
        recyclerView.setHasFixedSize(true);
        rejectedKLViewModel.getListCheckingScrap().observe(getViewLifecycleOwner(), new Observer<ArrayList<CheckingScrapModel>>() {
            @Override
            public void onChanged(ArrayList<CheckingScrapModel> checkingScrapModels) {
                mAdapter.setList(checkingScrapModels);
                mAdapter.notifyDataSetChanged();
            }

        });
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        checkingScrapModels = new ArrayList<>();
        mAdapter = new RejectedScaleTicketAdapter(checkingScrapModels, this);
        recyclerView.setAdapter(mAdapter);

        apiInterface = ApiService.getClient().create(ApiInterface.class);

        // Thông báo chưa có xe cần kiểm kiệu
        txtNone = root.findViewById(R.id.txtNone);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        rejectedKLViewModel.getListWeightScale().observe(getViewLifecycleOwner(), new Observer<ArrayList<WeightScaleModel>>() {
//            @Override
//            public void onChanged(ArrayList<WeightScaleModel> weightScaleModels) {
//                listWeightScaleCode.clear();
//                listWeightScaleName.clear();
//                if (weightScaleModels.size() >= 1) {
//                    for (WeightScaleModel weightScaleModel : weightScaleModels) {
//                        listWeightScaleCode.add(weightScaleModel.getSoftCode());
//                        listWeightScaleName.add(weightScaleModel.getSoftName());
//                    }
//                    ArrayAdapter WeightScaleadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listWeightScaleName);
//                    WeightScaleadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spnSelectBanCan.setAdapter(WeightScaleadapter);
//                    spnSelectBanCan.setSelection(spinnerPosition());
//                }
//
//            }
//        });
//
//        spnSelectBanCan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // Nếu khác với giá trị lưu trong reference thì cập nhật và load lại danh sách phiếu kiêm liệu
//                if(!listWeightScaleCode.get(position).equals(selectedWeightScale)){
//                    selectedWeightScale = listWeightScaleCode.get(position);
//                    saveRef(listWeightScaleCode.get(position));
//                    loadScaleTicketList(selectedWeightScale);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

    }

    protected void apiGetRejectedTickit() {

    }

    @Override
    public void onItemClick(int position) {
//        callApiGetDetail.cancel();
        progressDialog.show();
        callApiGetDetail = apiInterface.GetCheckingScrapKL(WareHouse.key, WareHouse.token, mAdapter.getList().get(position).getRfid());
        apiGetDetailScaleTicket(mAdapter.getList().get(position).getRfid());

    }

    private void apiGetDetailScaleTicket(String rfid) {
        try {
            //cancel Api get all record
//            callApiGetAll.cancel();
            // TODO: handle loggedInUser authentication
            // Call API check vehicle on server
            callApiGetDetail.enqueue(new Callback<SingleResponeMessage<KLCheckingScrap>>() {
                @Override
                public void onResponse(Call<SingleResponeMessage<KLCheckingScrap>> call, Response<SingleResponeMessage<KLCheckingScrap>> response) {
                    try {
                        SingleResponeMessage<KLCheckingScrap> responseMessage = response.body();
                        if (responseMessage.getIsSuccess()) {
                            KLCheckingScrap data = responseMessage.getItem();
                            Intent intent = new Intent(getActivity().getApplicationContext(), AddInfoKLActivity.class);
                            intent.putExtra("RFID", rfid);
                            intent.putExtra("SCALE_TICKET", data.getScaleTicket());
                            intent.putExtra("SCALE_TICKET_CODE", data.getScaleTicket().getScaleTicketCode());
                            intent.putExtra("VEHICLE_NUMBER", data.getVehicleModel().getVehicleNumber());
                            intent.putExtra("TYPE_TEXT", data.getVehicleModel().getTypeText());
                            intent.putExtra("IN_HOUR", data.getCheckingScrap().getInHourGuard());
                            intent.putExtra("SCALE_TICKET_PODETAIL_LIST", (Serializable) data.getScaleTicketPODetailList());
                            intent.putExtra("HISTORY", (Serializable) data.getHistory());
                            intent.putExtra("DUYET_PKL", data.getIsDaDuyet());
                            intent.putExtra("PRODUCTS", (Serializable) (Serializable) getProductList(data.getProductList(), data.getScaleTicketPODetailList()));
                            intent.putExtra("WAREHOUSE", (Serializable) data.getWarehouse());
                            startActivity(intent);

                        } else {

                            Toast.makeText(getActivity(), responseMessage.getErr().getMsgString().toString(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), e + "", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<SingleResponeMessage<KLCheckingScrap>> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), R.string.fail_connect_server, Toast.LENGTH_LONG).show();
                }
            });
//
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), R.string.fail_connect_server, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.dismiss();
        // Load dữ liệu mới mỗi khi màn hình active
        loadScaleTicketList();

        Log.d(TAG, "onResume");
    }

    private void loadScaleTicketList() {
        try {
            // TODO: handle loggedInUser authentication
            // Call API check vehicle on server
            Call<ListResponeMessage<CheckingScrapModel>> callApiGetAll = apiInterface.GetListCheckingScrapKL(WareHouse.key, WareHouse.token, "1", 2, "1");
            callApiGetAll.enqueue(new Callback<ListResponeMessage<CheckingScrapModel>>() {
                @Override
                public void onResponse(Call<ListResponeMessage<CheckingScrapModel>> call, Response<ListResponeMessage<CheckingScrapModel>> response) {
                    if (response.body().getIsSuccess()) {
                        List<CheckingScrapModel> temp = response.body().getData();

                        setShowNoneTicket(temp.size());

                        ArrayList<CheckingScrapModel> t = new ArrayList<>(temp);
                        rejectedKLViewModel.setListCheckingScrap(t);
                        progressBar.setVisibility(View.GONE);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<ListResponeMessage<CheckingScrapModel>> call, Throwable t) {
                    rejectedKLViewModel.setListCheckingScrap(new ArrayList<>());
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.fail_connect_server, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, e + "");
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fail_connect_server, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
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

    private void setShowNoneTicket(int size) {
        if (size == 0) {
            txtNone.setVisibility(View.VISIBLE);
        } else txtNone.setVisibility(View.GONE);
    }

}