package com.example.vasclientv2.baove.vehiclewait;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vasclientv2.R;
import com.example.vasclientv2.adapter.CheckingScrapAdapter;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.baove.BaoVeActivity;
import com.example.vasclientv2.baove.vehiclein.AddCheckingScrapActivity;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.GateModel;
import com.example.vasclientv2.model.entities.WeightScaleModel;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleWaitFragment extends Fragment implements CheckingScrapAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private CheckingScrapAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CheckingScrapModel> list = new ArrayList<>();
    private ArrayList<String> listGateName, listGateId;
    private ApiInterface apiInterface;
    private Call<ListResponeMessage<CheckingScrapModel>> call3;
    private Call<ListResponeMessage<GateModel>> callGate;
    private AlertDialog dialogShowNote;
    private ProgressBar progressBar;
    private Spinner spnSelectGate;

    private VehicleWaitViewModel mViewModel;

    public static VehicleWaitFragment newInstance() {
        return new VehicleWaitFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.vehicle_wait_fragment, container, false);

        progressBar = root.findViewById(R.id.loading);
        recyclerView = root.findViewById(R.id.rcvVehicleWait);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new CheckingScrapAdapter(list, this);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        spnSelectGate = root.findViewById(R.id.spnSelectGate);

        listGateName = new ArrayList<String>();
        listGateId = new ArrayList<String>();

        // API init
        apiInterface = ApiService.getClient().create(ApiInterface.class);
//        call3 = apiInterface.GetListCheckingScrapKL(WareHouse.key, WareHouse.token, "1", 0);
        callGate = apiInterface.GetGateList(WareHouse.key, WareHouse.token);

        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(VehicleWaitViewModel.class);
        // call api get data
//        getDataApi();

        mViewModel.getListGateByAPI(callGate);


        // show data on View
        mViewModel.getList().observe(getViewLifecycleOwner(), new Observer<ArrayList<CheckingScrapModel>>() {
            @Override
            public void onChanged(ArrayList<CheckingScrapModel> checkingScrapModels) {
                // sort list theo thứ tự giờ vào
//                list = sortVehicle(checkingScrapModels);

                list = checkingScrapModels;


                adapter.setList(list);
                recyclerView.setAdapter(adapter);
            }
        });

        mViewModel.getListGate().observe(getViewLifecycleOwner(), new Observer<ArrayList<GateModel>>() {
            @Override
            public void onChanged(ArrayList<GateModel> weightScaleModels) {
                listGateId.clear();
                listGateName.clear();
                if (weightScaleModels.size() >= 1) {
                    for (GateModel gateModel : weightScaleModels) {
                        listGateId.add(gateModel.getGateId());
                        listGateName.add(gateModel.getGateName());
                    }
                    ArrayAdapter gateAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listGateName);
                    gateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnSelectGate.setAdapter(gateAdapter);
                    spnSelectGate.setSelection(selectionDefault(listGateId));
                }

            }
        });

        spnSelectGate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Nếu khác với giá trị lưu trong reference thì cập nhật
                TransferData transferData = TransferData.getInstance(getActivity());
                transferData.saveData("IN_PORT", listGateId.get(position));

                try {
                    getDataApi(listGateId.get(position));
                } catch (Exception e) {
                    Log.e("ERROR", e + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //Lê Hoàng Long
    //Hiển thị note của bảo vệ
    @Override
    public void onItemClick(int position) {
        CheckingScrapModel checkingScrapModel = list.get(position);
        //checkingScrapModel.getNote1().toString()
        new AlertDialog.Builder(getContext())
                .setMessage(Html.fromHtml("<h2> + "+ checkingScrapModel.getVehicleNumber() +"</h2></br>\n" +
                        "<h2> + "+ checkingScrapModel.getDriverName() +"</h2></br>" +
                        "<h1>"+checkingScrapModel.getNote1().toString()+"</h1>"))
                .setPositiveButton("OK", (dialog,which) ->{
                })
                .create().show();
    }

    /**
     * Sort list vehicle theo gio vao cong
     * theo thời gian trễ nhất -> sớm nhất
     *
     * @param list
     * @return
     */
    private ArrayList<CheckingScrapModel> sortVehicle(ArrayList<CheckingScrapModel> list) {
        for (int i = list.size() - 1; i > 0; i--) {
            for (int j = 0; j <= i - 1; j++) {
                try {
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(list.get(j).getInHourGuard());
                    Date date2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(list.get(j + 1).getInHourGuard());
                    if (date1.before(date2)) {
                        // swap list item
                        CheckingScrapModel temp = list.get(j);
                        list.set(j, list.get(j + 1));
                        list.set(j + 1, temp);
                    }

                } catch (Exception e) {
                    Log.e("TAG", e + "");
                }
            }
        }
        return list;
    }

    private void getDataApi(String gate) {

        progressBar.setVisibility(View.VISIBLE);
        call3 = apiInterface.GetListCheckingScrapKL(WareHouse.key, WareHouse.token, "1", 0, gate);
        // TODO: Use the ViewModel
        call3.enqueue(new Callback<ListResponeMessage<CheckingScrapModel>>() {
            @Override
            public void onResponse(Call<ListResponeMessage<CheckingScrapModel>> call, Response<ListResponeMessage<CheckingScrapModel>> response) {
                if (response.body().getIsSuccess()) {
                    List<CheckingScrapModel> temp = response.body().getData();
                    ArrayList<CheckingScrapModel> t = new ArrayList<>(temp);
//                    mViewModel.setListData(t);
                    mViewModel.setListData(checkDuplicateNoiBo(t));
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ListResponeMessage<CheckingScrapModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                try {
                    Toast.makeText(getActivity(), "Không lấy được dữ liệu từ máy chủ", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                }
                mViewModel.setListData(new ArrayList<>());
            }
        });
    }

    // Lấy giá trị bàn cân mặc định từ reference
    private String getWeightScaleCodeFromRef() {
        TransferData transferData = TransferData.getInstance(getActivity());
        return transferData.getData("IN_PORT", "");
    }

    private int selectionDefault(ArrayList<String> listWeightScaleCode) {
        String weightScaleCode = getWeightScaleCodeFromRef();
        for (int i = 0; i < listWeightScaleCode.size(); i++) {
            if (listWeightScaleCode.get(i).equals(weightScaleCode)) {
                return i;
            }
        }
        return 0;
    }

    // Lọc những dòng xe nội bộ trùng biển số xe
    private ArrayList<CheckingScrapModel> checkDuplicateNoiBo(ArrayList<CheckingScrapModel> arrayList) {
        ArrayList<CheckingScrapModel> temp = new ArrayList<CheckingScrapModel>();
        if (!arrayList.isEmpty()) {
            temp.add(arrayList.get(0));
            boolean isDuplicate = false;
            for (int i = 1; i < arrayList.size(); i++) {
                isDuplicate = false;
                for (int j = 0; j < temp.size(); j++) {
                    if (arrayList.get(i).getVehicleNumber().equals(arrayList.get(j).getVehicleNumber())) {
                        isDuplicate = true;
                    }
                }
                if (!isDuplicate) {
                    temp.add(arrayList.get(i));
                }
            }
        }
        return temp;
    }

}