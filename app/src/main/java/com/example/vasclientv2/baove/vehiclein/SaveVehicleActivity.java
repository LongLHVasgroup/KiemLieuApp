package com.example.vasclientv2.baove.vehiclein;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;


import com.example.vasclientv2.apiInterface.ApiInterface;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.vasclientv2.R;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.ListResponeMessage;
import com.example.vasclientv2.model.common.RFIDModel;
import com.example.vasclientv2.model.entities.SaveVehicle;
import com.example.vasclientv2.model.entities.TagInfoModel;
import com.example.vasclientv2.ui.WareHouse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveVehicleActivity extends AppCompatActivity {

    private static final String TAG = "SaveVehicleActivity";
    private Toolbar toolbar;
    private TextView txtAlertResult;
    //    private String vehicleNumber, driver, cmnd, gatePort, received, note, userid, vehicleOwner;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    private AlertDialog mDialog, dialogSuccessSave;
    private Boolean isLoading = false;
    private ProgressDialog progressDialog;

    private RFIDModel rfidModel = new RFIDModel();

    private SaveVehicleViewModel saveVehicleViewModel;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_vehicle);

        saveVehicleViewModel = new ViewModelProvider(SaveVehicleActivity.this).get(SaveVehicleViewModel.class);
        createControls();

        createEvent();

        getDataIntent();

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[]{newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true)});

        saveVehicleViewModel.getRfid().observe(this, s -> {
            if (!s.equals("")) {
//                saveVehicleViewModel.checkIsNewTag();
                saveVehicleViewModel.setRfidForSaveModel();
            }
        });



        saveVehicleViewModel.getSaveVehicleLiveData().observe(this, saveVehicle -> {
            if (!saveVehicle.getRFID().equals(""))
                saveVehicleViewModel.saveVehicle();
        });

        saveVehicleViewModel.getIsLoading().observe(this, aBoolean -> {
            isLoading = aBoolean;
            if (aBoolean) {
                progressDialog.show();
            } else progressDialog.dismiss();
        });
        saveVehicleViewModel.getSuccessText().observe(this, s -> {
            dialogSuccessSave.setTitle(s);
            dialogSuccessSave.show();
        });
        saveVehicleViewModel.getUnSuccessText().observe(this, s -> {
            txtAlertResult.setText(s);
        });
    }

    private void createControls() {
        //Tool Bar
        toolbar = findViewById(R.id.save_vehicle_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);

        txtAlertResult = findViewById(R.id.txtAlertResult);

        // Dialog
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage(getResources().getString(R.string.please_wait)); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);

        mDialog = new AlertDialog.Builder(this).setNeutralButton(android.R.string.ok, null).create();

        dialogSuccessSave = new AlertDialog.Builder(this).setNegativeButton(android.R.string.ok, (dialog, which) -> {
            finish();
        }).setCancelable(false).create();

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            showMessage(R.string.error, R.string.no_nfc);
            finish();
            return;
        }
    }

    private void createEvent() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void getDataIntent() {
        SaveVehicle model = (SaveVehicle) getIntent().getSerializableExtra("SAVE_VEHICLE_DATA");
        if (!model.getGiaoNhan().equals("giao")){
            model.setProviderCode("");
            model.setProviderName("");
        }
        saveVehicleViewModel.setSaveVehicleLiveData(model);
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
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (!isLoading) {
            String rf = rfidModel.getRFIDFromIntent(intent);
            if (!rf.equals(""))
                saveVehicleViewModel.setRfid(rf);
        }
//
//        if (rfid != null) {
//            txtAlertResult.setText("");
//            getTagInfo();
//        } else {
//            txtAlertResult.setText("Không nhận dạng được thẻ");
//        }


    }




//    private void getTagInfo() {
//        progressDialog.show(); // Display Progress Dialog
//        Call<ListResponeMessage<TagInfoModel>> callTagIfo = apiInterface.GetTagInfo(WareHouse.key, WareHouse.token, rfid);
//        callTagIfo.enqueue(new Callback<ListResponeMessage<TagInfoModel>>() {
//            @Override
//            public void onResponse(Call<ListResponeMessage<TagInfoModel>> call, Response<ListResponeMessage<TagInfoModel>> response) {
//                try {
//
//                    if (response.body().getIsSuccess()) {
//                        if (response.body().getData().isEmpty()) {
//                            // the moi, khai bao thong tin the
//                            saveNewTag();
//                        } else {
//                            if (response.body().getData().get(0).getLost() == true) {
//                                // thong bao the bi khoa, can kich hoat lai
//                                txtAlertResult.setText("Thẻ đang được báo mất, vui lòng dùng thẻ khác, hoặc kích hoạt lại thẻ");
//                            } else {
//                                saveVehicleIn();
//                            }
//                        }
//
//                    } else {
//                        txtAlertResult.setText(response.body().getErr().getMsgString().toString());
//                    }
//
//                } catch (Exception e) {
//                    Log.e(TAG, e + "");
//                    txtAlertResult.setText("LỖI!. Không lấy được thông tin thẻ!");
//                }
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onFailure(Call<ListResponeMessage<TagInfoModel>> call, Throwable t) {
//                progressDialog.dismiss();
//                txtAlertResult.setText("LỖI!! Không kết nối được tới Server!");
//            }
//        });
//    }
}
