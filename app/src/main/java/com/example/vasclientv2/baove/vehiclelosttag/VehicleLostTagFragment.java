package com.example.vasclientv2.baove.vehiclelosttag;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.vasclientv2.R;
import com.example.vasclientv2.adapter.CheckingScrapAdapter;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.baove.BaoVeActivity;
import com.example.vasclientv2.baove.vehiclewait.VehicleWaitViewModel;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.GateModel;
import com.example.vasclientv2.ui.TransferData;

import java.io.DataInput;
import java.util.ArrayList;

import retrofit2.Call;

public class VehicleLostTagFragment extends Fragment implements CheckingScrapAdapter.OnItemClickListener {

    private VehicleLostTagViewModel mViewModel;
    private RecyclerView recyclerView;
    private CheckingScrapAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CheckingScrapModel> checkingScrapModelArrayList;
    private ArrayList<String> listGateName, listGateId;

    private ProgressBar progressBar;
    private Spinner spnSelectGate;
    private AlertDialog dialogSuccess, dialogFailed, dialogConfirmSave;
    private AlertDialog.Builder builderAlertOut;
    private int checkedOutPort = 0;
    private String outPortRef;
    private TransferData transferData;


    public static VehicleLostTagFragment newInstance() {
        return new VehicleLostTagFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.vehicle_lost_tag_fragment, container, false);


        progressBar = root.findViewById(R.id.loading);
        recyclerView = root.findViewById(R.id.rcvVehicleLostTag);
        recyclerView.setHasFixedSize(true);

        listGateName = new ArrayList<String>();
        listGateId = new ArrayList<String>();
        checkingScrapModelArrayList = new ArrayList<>();

        transferData = TransferData.getInstance(getActivity());

        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new CheckingScrapAdapter(checkingScrapModelArrayList, this);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        spnSelectGate = root.findViewById(R.id.spnSelectGate);


//        dialogConfirmSave = new AlertDialog.Builder(getActivity())
//                .setPositiveButton("Xác nhận", (dialog, which) -> {
//                    mViewModel.approveVehicleOut();
//                })
//                .setNegativeButton("Hủy", (dialog, which) -> {
//                })
//                .setCancelable(false)
//                .create();

        // Xe ra thành công thì cho load lại danh sách
        dialogSuccess = new AlertDialog.Builder(getActivity()).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            mViewModel.getListVehicleLostTagAPI();
        }).setCancelable(false).setTitle("Thành công").create();

        dialogFailed = new AlertDialog.Builder(getActivity()).setPositiveButton(android.R.string.ok, (dialog, which) -> {
        }).setCancelable(false).setTitle("Không thành công!!!").create();


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VehicleLostTagViewModel.class);
        // TODO: Use the ViewModel

        getOutPortFromRef();
        // gọi lấy danh sách cổng
        mViewModel.getListGateByAPI();

        // show data on View
        mViewModel.getListData().observe(getViewLifecycleOwner(), new Observer<ArrayList<CheckingScrapModel>>() {
            @Override
            public void onChanged(ArrayList<CheckingScrapModel> checkingScrapModels) {
                checkingScrapModelArrayList = checkingScrapModels;
                adapter.setList(checkingScrapModelArrayList);
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
                // out port
                setCheckedOutPort(outPortRef);
                setDialogConfirmSave();
            }
        });

        spnSelectGate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Nếu khác với giá trị lưu trong reference thì cập nhật
                TransferData transferData = TransferData.getInstance(getActivity());
                transferData.saveData("IN_PORT", listGateId.get(position));
                mViewModel.setCurrentGateValue(listGateId.get(position));
                try {
                    mViewModel.getListVehicleLostTagAPI();
                } catch (Exception e) {
                    Log.e("ERROR", e + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // Khi có thay đổi biến isLoading
        mViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                progressBar.setVisibility(View.VISIBLE);
            else
                progressBar.setVisibility(View.GONE);
        });

        mViewModel.getSelectedVehicle().observe(getViewLifecycleOwner(), model -> {
            try {
                if (!model.getRfid().equals("")) {
                    dialogConfirmSave.setTitle("XÁC NHẬN XE " + model.getVehicleNumber() + " RA");
//                    dialogConfirmSave.setMessage("Xe " + model.getVehicleNumber() + " chuẩn bị ra khỏi bãi");
                    dialogConfirmSave.show();
                }
            } catch (Exception e) {
                Log.e("VehicleLostTagFragment", e + "");
            }

        });

        mViewModel.getTextResultSuccess().observe(getViewLifecycleOwner(), s -> {
            dialogSuccess.setMessage(s);
            dialogSuccess.show();
        });
        mViewModel.getTextResultFailed().observe(getViewLifecycleOwner(), s -> {
            dialogFailed.setMessage(s);
            dialogFailed.show();
        });


    }

    private int selectionDefault(ArrayList<String> listPortCode) {
        String s = getInPortFromRef();
        for (int i = 0; i < listPortCode.size(); i++) {
            if (listPortCode.get(i).equals(s)) {
                return i;
            }
        }
        return 0;
    }

    // Lấy giá trị bàn cân mặc định từ reference
    private String getInPortFromRef() {
        return transferData.getData("IN_PORT", "");
    }

    private void getOutPortFromRef() {
        outPortRef = transferData.getData("OUT_PORT", "");
        mViewModel.setOutPortValue(outPortRef);
    }

    private void setCheckedOutPort(String outPortRef){
        for (int i = 0; i<listGateId.size(); i++){
            if (outPortRef.equals(listGateId.get(i))){
                Log.d("AAAAAAAAAA", i+"");
                checkedOutPort = i;
            }
        }
    }

    @Override
    public void onItemClick(int position) {

        // Nhấp chọn xe muốn cho ra khỏi bãi
        mViewModel.setSelectedVehicleValue(checkingScrapModelArrayList.get(position));
    }

    private void setDialogConfirmSave(){

        ArrayAdapter adapterListGateName = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_single_choice, listGateName);
        builderAlertOut = new AlertDialog.Builder(getActivity());
        builderAlertOut.setSingleChoiceItems(adapterListGateName, checkedOutPort, (dialog, which) -> {
            if (!outPortRef.equals(listGateId.get(which))) {
                // Save Shared reference
                transferData.saveData("OUT_PORT", listGateId.get(which));
            }
            mViewModel.setOutPortValue(listGateId.get(which));
        });
        builderAlertOut.setPositiveButton("Xác nhận", (dialog, which) -> {
            mViewModel.approveVehicleOut();
        });
        builderAlertOut.setCancelable(false);
        builderAlertOut.setNegativeButton("Hủy", (dialog, which) -> {
        }).create();

        dialogConfirmSave = builderAlertOut.create();
    }

}