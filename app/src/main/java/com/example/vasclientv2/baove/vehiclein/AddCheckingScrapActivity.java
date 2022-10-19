package com.example.vasclientv2.baove.vehiclein;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vasclientv2.R;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.entities.GateModel;
import com.example.vasclientv2.model.entities.ProviderModel;
import com.example.vasclientv2.model.entities.SaveVehicle;
import com.example.vasclientv2.model.entities.VehicleOwnerModel;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCheckingScrapActivity extends AppCompatActivity {
    private TextView edtPlateNumber, edtTypeText, edt_NameDriver, edt_IdentityCard, tv_provider, txtProviderName;
    private Button btnReadNFC;
    private Spinner spn_Delivery, spn_Port, spn_VehicleOwner, spnProvider;
    private EditText edt_Note;
    private ArrayList listGate, listVehicleOwnerName, listProviderName;
    private ApiInterface apiInterface;
    private List<VehicleOwnerModel> vehicleOwnerModelList;
    // danh sách nhà cung cấp
    private List<ProviderModel> providerModelList;
    // cho romooc
    private TextView tvRomooc;
    //
    private ProviderModel selectedProvider;

    public String nameGate, vehicleOwner;
    public int selectedInPort;
    public String received, inPortRef, inPort, webRegisteredId;
    public static String DEFAULT_PROVIDER_CODE = "0000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        //Khai báo thành phần giao diện
        btnReadNFC = findViewById(R.id.btnReadNFC);
        edtPlateNumber = findViewById(R.id.edtPlateNumber);
        edtTypeText = findViewById(R.id.edtTypeText);
        edt_NameDriver = findViewById(R.id.edt_NameDriver);
        edt_IdentityCard = findViewById(R.id.edt_IdentityCard);
        edt_Note = findViewById(R.id.edt_Note);
        spn_Delivery = findViewById(R.id.edt_Delivery);
        spn_Port = findViewById(R.id.edt_Port);
        spn_VehicleOwner = findViewById(R.id.spnVehicleOwner);
        txtProviderName = findViewById(R.id.txtProviderName);
        //
        tv_provider = findViewById(R.id.tv_provider);
//        spnProvider = findViewById(R.id.edt_provider);

        // Romooc
        tvRomooc = findViewById(R.id.tv_Romooc);

        //Tool Bar
        Toolbar toolbar = findViewById(R.id.add_vehicle_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //API interface
        apiInterface = ApiService.getClient().create(ApiInterface.class);

        //
        listVehicleOwnerName = new ArrayList<>();
        //
        listProviderName = new ArrayList();

        //Get info Intent
        edtPlateNumber.setText(getIntent().getStringExtra("VEHICLE_NUMBER"));
        edtTypeText.setText(getIntent().getStringExtra("VEHICLE_TYPE"));
        try {
            tvRomooc.setText(getIntent().getStringExtra("ROMOOC"));
        }catch (Exception ex){
            Log.e("AddCheckingScrap", ex + "NULL ROMOOC");
        }

        try {// Không chắc có data nên quăng vào try
            edt_NameDriver.setText(getIntent().getStringExtra("DRIVER_NAME"));
            edt_IdentityCard.setText(getIntent().getStringExtra("DRIVER_ID"));
            webRegisteredId = getIntent().getStringExtra("WEB_REGISTERED_ID");
            // Lấy ds nhà cc từ intent
            providerModelList = (List<ProviderModel>) getIntent().getSerializableExtra("PROVIDERS");
            if (providerModelList.size() == 1) {
                txtProviderName.setText(providerModelList.get(0).getProviderName());
            }
//            getProviders(providerModelList);
            // Chuyeern thông tin nhận được vào 2 array
            getVehicleOwner(vehicleOwnerModelList);

            //Xử lý nhận thoogn tin romooc


        } catch (Exception e) {
            Log.e("AddCheckingScrap", e + "");
        }

        //List spin
        listGate = new ArrayList<>();
        // Get inPort from reference
        getDataReference();

        // setup spinner vehicle owner
        setupSpinnerVehicleOwner();

        // setup spinner nhà cung cấp
//        setupSpinnerProvider();
        //get list gate by api
        try {
            getListGate();
        } catch (Exception e) {
            Log.e("ERROR_GET_LIST_GATE", e.toString());
        }

        final List<String> list_1 = new ArrayList<>();
        list_1.add("giao");
        list_1.add("nhan");
        list_1.add("chuyen");
        ArrayAdapter adapter = new ArrayAdapter(AddCheckingScrapActivity.this, R.layout.spinner_item_delivery, list_1);
        adapter.setDropDownViewResource(R.layout.spinner_item_delivery);
        spn_Delivery.setAdapter(adapter);
        spn_Delivery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                received = list_1.get(position);
                // ẩn nhà cung cấp khi chọn kiểu Giao
//                if (position == 0) {
//                    tv_provider.setVisibility(View.VISIBLE);
//                    spnProvider.setVisibility(View.VISIBLE);
//                } else {
//                    tv_provider.setVisibility(View.GONE);
//                    spnProvider.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnReadNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plateNumber = edtPlateNumber.getText().toString();
                String note = edt_Note.getText().toString();
                String nameDriver = edt_NameDriver.getText().toString();
                String identityCard = edt_IdentityCard.getText().toString();
                String romooc = tvRomooc.getText().toString();

                if (isEmptyInput(plateNumber, nameDriver, identityCard, received, inPort, selectedProvider)) {
//                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(AddCheckingScrapActivity.this, SaveVehicleActivity.class);
//                    intent.putExtras(bundle);
                    intent.putExtra("SAVE_VEHICLE_DATA", new SaveVehicle(nameDriver, plateNumber, identityCard, inPort, "", webRegisteredId, received, note, null, "", "", romooc));
//                    intent.putExtra("VEHICLE_NUMBER", plateNumber);
//                    intent.putExtra("DRIVER", nameDriver);
//                    intent.putExtra("CMND", identityCard);
//                    intent.putExtra("GATE_PORT", inPort);
//                    intent.putExtra("RECEIVED", received);
//                    intent.putExtra("NOTE", note);
//                    intent.putExtra("VEHICLE_OWNER", vehicleOwner);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    protected void getListGate() {
        Call<ListResponeMessage<GateModel>> callGetGateList = apiInterface.GetGateList(WareHouse.key, WareHouse.token);
        callGetGateList.enqueue(new Callback<ListResponeMessage<GateModel>>() {
            @Override
            public void onResponse(Call<ListResponeMessage<GateModel>> call, Response<ListResponeMessage<GateModel>> response) {
                Log.d("TAG", response.code() + "");
                ListResponeMessage<GateModel> responseMessage = response.body();
                if (!responseMessage.getIsSuccess()) {
                    return;
                }

                final List<GateModel> gateList = responseMessage.getData();
                if (gateList == null) {
                    return;
                }
                for (int i = 0; i < gateList.size(); i++) {
                    listGate.add(gateList.get(i).getGateName());
//                    gateId = gateList.get(i).getGateId();
                    if (inPortRef.equals(gateList.get(i).getGateId()))
                        selectedInPort = i;
                }
                ArrayAdapter adapter = new ArrayAdapter(AddCheckingScrapActivity.this, android.R.layout.simple_spinner_item, listGate);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn_Port.setAdapter(adapter);
                spn_Port.setSelection(selectedInPort);
                spn_Port.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        inPort = gateList.get(position).getGateId();
                        // Nếu khác với giá trị lưu trong reference thì cập nhật
                        if (!inPort.equals(inPortRef)) {
                            TransferData transferData = TransferData.getInstance(getApplication());
                            transferData.saveData("IN_PORT", inPort);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ListResponeMessage<GateModel>> call, Throwable t) {
            }
        });
    }

    protected Boolean isEmptyInput(String plateNumber, String nameDriver, String identityCard, String received, String gatePort, ProviderModel selectedProvider) {
        try {
            if (plateNumber.isEmpty()) {
                edtPlateNumber.setError("Thông tin trống, hãy thử quay lại bước phía trước");
                edtPlateNumber.requestFocus();
                return false;
            } else if (nameDriver.isEmpty()) {
                edt_NameDriver.setError("Vui lòng nhập tên tài xế");
                edt_NameDriver.requestFocus();
                return false;
            } else if (identityCard.isEmpty()) {
                edt_IdentityCard.setError("Thông tin trống!");
                edt_IdentityCard.requestFocus();
                return false;
            } else if (gatePort.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn lại Cổng vào", Toast.LENGTH_LONG).show();
                return false;
            }
//            else if (received.equals("giao") && selectedProvider.getProviderCode().equals(DEFAULT_PROVIDER_CODE)) {
//                Toast.makeText(this, "Vui lòng chọn nhà cung cấp", Toast.LENGTH_LONG).show();
//                return false;
//            }
            return true;
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi! Hãy thử kiểm tra lại thông tin", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    // Lấy dữ liệu cổng vào mặc định
    private void getDataReference() {
        TransferData transferData = TransferData.getInstance(getApplication());
        inPortRef = transferData.getData("IN_PORT", "");
        if (!inPortRef.equals("")) {
            inPort = inPortRef;
        }
    }

    private void getVehicleOwner(List<VehicleOwnerModel> list) {
        for (VehicleOwnerModel model : list) {
            listVehicleOwnerName.add(model.getVehicleOwnerName());
        }
    }

    // Đưa danh sách tên của nhà cc nhận được vào array đẻ hiện lên spinner
    private void getProviders(List<ProviderModel> list) {
        for (ProviderModel model : list) {
            listProviderName.add(model.getProviderName());
        }
    }

    private void setupSpinnerVehicleOwner() {
        if (listVehicleOwnerName.size() != 0) {
            ArrayAdapter vehicleOwnerAdapter = new ArrayAdapter(AddCheckingScrapActivity.this, android.R.layout.simple_spinner_item, listVehicleOwnerName);
            vehicleOwnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_VehicleOwner.setAdapter(vehicleOwnerAdapter);
            spn_VehicleOwner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    vehicleOwner = vehicleOwnerModelList.get(position).getVehicleOwner();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }




//    private void setupSpinnerProvider() {
//        if (listProviderName.size() != 0) {
//            ArrayAdapter providerAdapter = new ArrayAdapter(AddCheckingScrapActivity.this, android.R.layout.simple_spinner_item, listProviderName);
//            providerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spnProvider.setAdapter(providerAdapter);
//            spnProvider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    selectedProvider = providerModelList.get(position);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//        }
//    }
}
