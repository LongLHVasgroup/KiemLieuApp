package com.example.vasclientv2.baove.vehiclein;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.VehicleModel;
import com.example.vasclientv2.ui.WareHouse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

public class VehicleInFragment extends Fragment {

    private VehicleInViewModel vehicleInViewModel;
    private Button btnAddVehicle;
    private ImageButton btnScanByCamera;
    private TextView txtWarnBSXEmpty;
    private TextView txtAlertResult;
    private ProgressDialog progressDialog;
    private AutoCompleteTextView txtBienSoXe;
    private List<String> listBSX = new ArrayList<>();
    private ArrayAdapter<String> listBSXAdapter;
    private String oldVehicleNo = "#";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vehicleInViewModel =
                new ViewModelProvider(getActivity()).get(VehicleInViewModel.class);

        View root = inflater.inflate(R.layout.fragment_vehicle_in, container, false);

        // Khởi tạo các control trên giao diện
        createControls(root);

        // Bắt thay đổi trong dataLive
        createObserveData();

        createEvents();
        return root;
    }

    private Boolean isDiffVehicleNo(String newVehicleNo) {
        if (!newVehicleNo.contains(oldVehicleNo)) {
            oldVehicleNo = newVehicleNo;
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Xóa chữ thông báo mỗi khi fragment mở lại
        vehicleInViewModel.setWarnBSXEmpty("");
        // Xóa chữ thông báo xe ra
        vehicleInViewModel.setTxtAlertResult("");
        // Dissmis Process Dialog
    }

    protected Boolean isRightBSX() {
        String bsx = txtBienSoXe.getText().toString().trim();
        if (!bsx.isEmpty()) {
            if (bsx.length() >= 5) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    protected void addVehicle() {
        if (!isRightBSX()) {
            vehicleInViewModel.setWarnBSXEmpty(getString(R.string.bsx_is_empty));
        } else {
            vehicleInViewModel.setWarnBSXEmpty("");
            //
//            vehicleInViewModel.getVehicleInfo(txtBienSoXe.getText().toString().trim().toUpperCase());
            vehicleInViewModel.getVehicleInfoWeb(txtBienSoXe.getText().toString().trim().toUpperCase());

        }
    }

    private void createControls(View v) {
        txtBienSoXe = v.findViewById(R.id.txtBienSoXe);
        txtAlertResult = v.findViewById(R.id.txtAlertResult);
        txtWarnBSXEmpty = v.findViewById(R.id.txtWarnBSX);
        btnAddVehicle = v.findViewById(R.id.btnAddVehicle);
        btnScanByCamera = v.findViewById(R.id.btnScanBSX);

        // Adapter list select BSX
        listBSXAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, listBSX);
        txtBienSoXe.setAdapter(listBSXAdapter);

        // Dialog Loading
        progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        progressDialog.setMessage(getResources().getString(R.string.please_wait)); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);

    }


    private void createEvents() {
        btnAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVehicle();
            }
        });
        btnScanByCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), ScanBsxActivity.class), 1);
//                startActivity(new Intent(getActivity(),ScanBsxActivity.class));
            }
        });

        txtBienSoXe.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = txtBienSoXe.getText().toString().replaceAll("[^a-zA-Z0-9]", "");
                if (value.length() >= 3 && isDiffVehicleNo(value)) {
                    // Nếu nhập từ 3 số và khác với 3 số lần trước thì gọi api lấy danh sách
                    vehicleInViewModel.getDataBSX(value);
                } else if (value.length() >= 3 && !listBSX.isEmpty()) {
                    // Trường hợp người dùng nhập tiếp dựa trên 3 ký tự đầu thì chỉ cần lọc lại kết quả của lần trước
                    // update lại adapter
                    ArrayList<String> strings = new ArrayList<>();
                    for (String s1 : listBSX) {
                        String s2 = s1.replaceAll(" ", "").replaceAll("\\.", "").replaceAll("-", "");
                        if (s2.contains(value)) {
                            strings.add(s1);
                        }
                    }
                    listBSXAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, strings);
                    txtBienSoXe.setAdapter(listBSXAdapter);
                    listBSXAdapter.notifyDataSetChanged();
                }
            }
        });

        // Nhấn Khi nhập xong biển số xe
        txtBienSoXe.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addVehicle();
                }
                return false;
            }
        });

    }

    // Observer Data
    private void createObserveData() {

        vehicleInViewModel.getBienSoXe().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                txtBienSoXe.setText(s);
            }
        });

        // Cảnh báo BSX đúng chuẩn
        vehicleInViewModel.getWarnBSXEmpty().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                txtWarnBSXEmpty.setText(s);
            }
        });

        // Xử lý dữ liệu danh sách bsx
        vehicleInViewModel.getlistBSX().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                listBSX = strings;
                listBSXAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, listBSX);
                txtBienSoXe.setAdapter(listBSXAdapter);
                listBSXAdapter.notifyDataSetChanged();
            }
        });

        vehicleInViewModel.getIsloading().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                progressDialog.show();
            else
                progressDialog.dismiss();
        });

        // Nếu nhận được thông tin xe thì chuyển sang Activity nhập thông tin xe vào
        vehicleInViewModel.getVehicleModelLiveData().observe(getViewLifecycleOwner(), vehicleModel -> {

            if (vehicleModel.getRegInt() == null) return;
            // check null thông tin tài xế
            if (vehicleModel.getRegInt() == 0 || vehicleModel.getRegInt() == 3) {
                // xe chưa khai báo
                // show thông báo và cho vào
                progressDialog.dismiss();

                new AlertDialog.Builder(getActivity())
                        .setPositiveButton("Cho Vào", (dialog, which) -> {
                            onConfirm(vehicleModel);
                        })
                        .setNeutralButton("Huỷ", (dialog, which) -> {
                            vehicleInViewModel.setVehicleModelLiveData(new VehicleModel());
                        })
                        .setTitle(vehicleModel.getRegStatus())
                        .setMessage("Chỉ những xe không giao sắt phế liệu mới được cho phép vào mà không khai báo trước")
                        .create().show();

            } else if (vehicleModel.getRegInt() == 1) {
                // Xe cần kích hoạt trước khi vào
                progressDialog.dismiss();

                new AlertDialog.Builder(getActivity())
                        .setPositiveButton("Huỷ", (dialog, which) -> {
                        })
                        .setTitle(vehicleModel.getRegStatus())
                        .setMessage("Xe chưa kích hoạt không cho phép vào")
                        .create().show();
            } else if (vehicleModel.getRegInt() == 2) { // Xe đã đăng ký và thông tin kích hoạt OK
                Log.d("TAG", vehicleModel.getDriverIdCard() + vehicleModel.getVehicleNumber() + vehicleModel.getRomooc());
                Intent intent = new Intent(getActivity(), AddCheckingScrapActivity.class);
                intent.putExtra("VEHICLE_TYPE", vehicleModel.getTypeText());
                intent.putExtra("VEHICLE_NUMBER", vehicleModel.getVehicleNumber());
                intent.putExtra("DRIVER_NAME", vehicleModel.getDriverName());
                intent.putExtra("DRIVER_ID", vehicleModel.getDriverIdCard());
                intent.putExtra("WEB_REGISTERED_ID", vehicleModel.getVehicleRegisterMobileId());
                intent.putExtra("PROVIDERS", (Serializable) vehicleModel.getProviders());
                intent.putExtra("ROMOOC", (Serializable) vehicleModel.getRomooc());
//            intent.putExtra("VEHICLE_OWNER", (Serializable) vehicleModel.getVehicleOwner());
                startActivity(intent);
                vehicleInViewModel.setVehicleModelLiveData(new VehicleModel());
            } else if (vehicleModel.getRegInt() == 4) { // Xe nội bộ
                onConfirm(vehicleModel); // cho vào luôn
            }
        });

        // Thông báo khi lỗi gọi API
        vehicleInViewModel.getTxtAlertResult().observe(getViewLifecycleOwner(), s -> {
            txtAlertResult.setText(s);
        });
    }

    // Cho xe không phải giao sắt phế liệu vào bãi
    private void onAcceptVehicleInWithoutRegisted() {


    }

    private void onConfirm(VehicleModel vehicleModel) {
        Intent intent = new Intent(getActivity(), AddCheckingScrapActivity.class);
        intent.putExtra("VEHICLE_TYPE", vehicleModel.getTypeText());
        intent.putExtra("VEHICLE_NUMBER", vehicleModel.getVehicleNumber());
        intent.putExtra("DRIVER_NAME", vehicleModel.getDriverName());
        intent.putExtra("DRIVER_ID", vehicleModel.getDriverIdCard());
        intent.putExtra("WEB_REGISTERED_ID", vehicleModel.getVehicleRegisterMobileId());
//        intent.putExtra("PROVIDERS", (Serializable) vehicleModel.getProviders());
//            intent.putExtra("VEHICLE_OWNER", (Serializable) vehicleModel.getVehicleOwner());
        startActivity(intent);
        vehicleInViewModel.setVehicleModelLiveData(new VehicleModel());
    }
}