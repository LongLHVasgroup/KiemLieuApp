package com.example.vasclientv2.baove;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.vasclientv2.R;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.baove.vehiclein.AddCheckingScrapActivity;
import com.example.vasclientv2.baove.vehiclein.VehicleInViewModel;
import com.example.vasclientv2.baove.vehicleout.VehicleOutFragment;
import com.example.vasclientv2.baove.vehicleout.VehicleOutViewModel;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.RFIDModel;
import com.example.vasclientv2.model.common.SingleResponeMessage;
import com.example.vasclientv2.model.entities.CheckingScrapModel;
import com.example.vasclientv2.model.entities.GateModel;
import com.example.vasclientv2.setting.SettingsActivity;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;
import com.example.vasclientv2.ui.login.LoggedInUserView;
import com.example.vasclientv2.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BaoVeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private VehicleOutFragment vehicleOutFragment;
    private AlertDialog mDialog;
    private AlertDialog dialogCheckingOutVehicle;
    private AlertDialog dialogNotFoundVehicle;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    private String rfid;
    private String labelCurrentFrag;
    private VehicleOutViewModel vehicleOutViewModel;
    private VehicleInViewModel vehicleInViewModel;
    private NavController navController;
    private AlertDialog.Builder builderAlertOut;

    // Out Port
    private String outPortRef;
    private String outPort;
    private ArrayList<String> listGateName, listGateId;
    private int checkedOutPort = 0;


    LoggedInUserView loggedInUserView = new LoggedInUserView();
    private RFIDModel rfidModel = new RFIDModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bao_ve);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_vehicle_in, R.id.nav_vehicle_out, R.id.nav_vehicle_wait, R.id.nav_vehicle_lost_tag)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                labelCurrentFrag = destination.getLabel().toString();
            }
        });


        //Get data Intent -> loggedInUser model
        try {
            loggedInUserView = new LoggedInUserView(getIntent().getStringExtra("FULL_NAME"), getIntent().getStringExtra("ROLE_CODE"), getIntent().getStringExtra("USER_ID"));
        } catch (Exception e) {
            startActivity(new Intent(BaoVeActivity.this, LoginActivity.class));
            finish();
        }

        // get reference
        getDataReference();

        // Set Text Full name
        View headerView = navigationView.getHeaderView(0);
        TextView txtFullName = headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(loggedInUserView.getDisplayName());


        // Button Logout
        Button btnLogOut = drawer.findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                finish();
            }
        });


        // button Setting-> hiện tại dùng để đổi mật khẩu
        ImageButton btnSetting = headerView.findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(v -> startActivity(new Intent(BaoVeActivity.this, SettingsActivity.class)));

        vehicleOutViewModel = new ViewModelProvider(this).get(VehicleOutViewModel.class);
        vehicleInViewModel = new ViewModelProvider(this).get(VehicleInViewModel.class);

        // Alert
        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

        // List Port
        listGateId = new ArrayList<>();
        //
        listGateName = new ArrayList<>();

        // Call api lấy danh sách cổng ra vào
        getListGate();


//        builderAlertOut = new AlertDialog.Builder(BaoVeActivity.this);
//        builderAlertOut.setPositiveButton(android.R.string.yes, (dialog, which) -> onAcceptVehicleOut());
//        builderAlertOut.setCancelable(false);
//        builderAlertOut.setNegativeButton(android.R.string.no, null).create();
//        builderAlertOut.setTitle("Xe ra");

//        dialogCheckingOutVehicle = builderAlertOut.create();


        // Alert Dialog xe ra
//        dialogCheckingOutVehicle = new AlertDialog.Builder(BaoVeActivity.this)
//                .setPositiveButton(android.R.string.yes, (dialog, which) -> onAcceptVehicleOut())
//                .setSingleChoiceItems(adapterListGateName, 0, (dialog, which) -> outPort = listGateId.get(which))
//                .setCancelable(false)
//
//                .setNegativeButton(android.R.string.no, null).create();

        dialogNotFoundVehicle = new AlertDialog.Builder(BaoVeActivity.this)
                .setTitle(R.string.not_found_vehicle).setNegativeButton("OK", null).create();
        // NFC
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        // Check NFC status
        checkNFC();
        // Set pendding Intent
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Push message read form NFC
        mNdefPushMessage = new NdefMessage(new NdefRecord[]{newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true)});

//        resolveIntent(getIntent());
        rfid = rfidModel.getRFIDFromIntent(getIntent());
    }

    // get List Gate
    protected void getListGate() {
        ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
        Call<ListResponeMessage<GateModel>> callGetGateList = apiInterface.GetGateList(WareHouse.key, WareHouse.token);
        callGetGateList.enqueue(new Callback<ListResponeMessage<GateModel>>() {
            @Override
            public void onResponse(Call<ListResponeMessage<GateModel>> call, Response<ListResponeMessage<GateModel>> response) {
                try {
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
                        listGateName.add(gateList.get(i).getGateName());
                        listGateId.add(gateList.get(i).getGateId());
                        if (gateList.get(i).getGateId().equals(outPortRef)) {
                            checkedOutPort = i;
                        }else {
                            outPort = gateList.get(0).getGateId();
                        }
                    }
                    ArrayAdapter adapterListGateName = new ArrayAdapter(BaoVeActivity.this, android.R.layout.simple_list_item_single_choice, listGateName);
                    builderAlertOut = new AlertDialog.Builder(BaoVeActivity.this);
                    builderAlertOut.setSingleChoiceItems(adapterListGateName, checkedOutPort, (dialog, which) -> {
                        if (!outPortRef.equals(listGateId.get(which))) {
                            // Save Shared reference
                            TransferData transferData = TransferData.getInstance(getApplication());
                            transferData.saveData("OUT_PORT", listGateId.get(which));
                        }
                        outPort = listGateId.get(which);
                    });
                    builderAlertOut.setPositiveButton(android.R.string.yes, (dialog, which) -> onAcceptVehicleOut());
                    builderAlertOut.setCancelable(false);
                    builderAlertOut.setNegativeButton(android.R.string.no, null).create();

                    dialogCheckingOutVehicle = builderAlertOut.create();
                } catch (Exception e) {
                    AlertDialog errorDialog = new AlertDialog.Builder(BaoVeActivity.this)
                            .setNeutralButton("Ok", null).setTitle("ERROR!")
                            .setMessage("Không lấy đươc thông tin cổng ra vào từ máy chủ: "+e )
                            .create();
                    errorDialog.show();
                }

            }

            @Override
            public void onFailure(Call<ListResponeMessage<GateModel>> call, Throwable t) {
                AlertDialog errorDialog = new AlertDialog.Builder(BaoVeActivity.this)
                        .setNeutralButton("Ok", null).setTitle("ERROR!")
                        .setMessage("Không lấy đươc thông tin cổng ra vào từ máy chủ. Kiểm tra lại kết nối của bạn " )
                        .create();
                errorDialog.show();
            }
        });
    }

    protected void onAcceptVehicleOut() {
        ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
        Call<AcctionMessage> call3 = apiInterface.SaveVehicleOut(WareHouse.key, WareHouse.token, rfid, outPort, WareHouse.UserId);
        call3.enqueue(new Callback<AcctionMessage>() {
            @Override
            public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                try {
                    AcctionMessage responseMessage = response.body();
                    vehicleOutViewModel.getAlertResult().setValue(responseMessage.getErr().getMsgString().toString());
                    vehicleInViewModel.setTxtAlertResult(responseMessage.getErr().getMsgString().toString());
                } catch (Exception e) {
                    Log.i("responseException", e.toString());
                }
            }

            @Override
            public void onFailure(Call<AcctionMessage> call, Throwable t) {

            }
        });

    }

    private void checkNFC() {
        try{
            if (mAdapter == null) {
                showMessage(R.string.error, R.string.no_nfc);
            }
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
        }catch (Exception  ex){
            showMessage(R.string.error, R.string.no_nfc);
            Log.e("BAOVE_ACTIVITY",ex.toString());
        }

    }

    protected void logout() {
        // Move to Login Activity
        startActivity(new Intent(BaoVeActivity.this, LoginActivity.class));
        // clear shared reference
        removeUserReference();
    }

    protected void removeUserReference() {

        WareHouse.UserId = null;
        TransferData transferData = TransferData.getInstance(this);
        transferData.removeData("IS_LOGGED");
        transferData.removeData("USER_ID");
        transferData.removeData("FULL_NAME");
        transferData.removeData("ROLE_CODE");
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        rfid = null;
        setIntent(intent);
        // Check if the fragment is an instance of the right fragment
//        if (labelCurrentFrag.equals(getString(R.string.menu_vehicle_out))) {
        rfid = rfidModel.getRFIDFromIntent(intent);
        if (rfid != null) {
            try {
                // Switch to fragment vehicle out when read NFC
                navController.navigate(R.id.nav_vehicle_out);
                // Call API check vehicle on server
                ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
                Call<SingleResponeMessage<CheckingScrapModel>> callCheckingVehicle = apiInterface.GetVehicleNumber(WareHouse.key, WareHouse.token, rfid);
                callCheckingVehicle.enqueue(new Callback<SingleResponeMessage<CheckingScrapModel>>() {
                    @Override
                    public void onResponse(Call<SingleResponeMessage<CheckingScrapModel>> call, Response<SingleResponeMessage<CheckingScrapModel>> response) {
                        try {
                            dialogCheckingOutVehicle.dismiss();
                            dialogNotFoundVehicle.dismiss();
                            Log.d("TAG", response.code() + "");
                            SingleResponeMessage<CheckingScrapModel> responseMessage = response.body();
                            if (responseMessage.getIsSuccess()) {
                                CheckingScrapModel data = responseMessage.getItem();
                                if (data != null) {
//                                    dialogCheckingOutVehicle.setTitle("Xe ra");
                                    dialogCheckingOutVehicle.setTitle("Xe \" " + (data.getVehicleNumber().isEmpty() ? "" : data.getVehicleNumber()) + " \" đang chuẩn bị ra ? Vui lòng chọn cổng ra");
                                    dialogCheckingOutVehicle.show();
                                } else {
                                    dialogNotFoundVehicle.show();
                                    vehicleOutViewModel.setAlertResult("");
                                }

                            } else {
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleResponeMessage<CheckingScrapModel>> call, Throwable t) {
                        vehicleOutViewModel.setAlertResult(getResources().getString(R.string.network_error));
                    }
                });

            } catch (Exception e) {
                Toast.makeText(this, R.string.network_error, Toast.LENGTH_LONG).show();
            }
        }

//        }
    }


    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }

    private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
            mAdapter.disableForegroundNdefPush(this);
        }
    }

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    // Lấy dữ liệu cổng ra mặc định
    private void getDataReference() {
        TransferData transferData = TransferData.getInstance(getApplication());
        outPortRef = transferData.getData("OUT_PORT", "");
        if (outPortRef != "") {
            outPort = outPortRef;
        }
    }


}