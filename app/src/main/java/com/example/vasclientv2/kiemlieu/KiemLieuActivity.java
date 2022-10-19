package com.example.vasclientv2.kiemlieu;

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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.example.vasclientv2.kiemlieu.kiemlieu.KiemLieuViewModel;
import com.example.vasclientv2.setting.SettingsActivity;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;
import com.example.vasclientv2.ui.login.LoggedInUserView;
import com.example.vasclientv2.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;

import java.nio.charset.Charset;
import java.util.Locale;

public class KiemLieuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String labelCurrentFrag;
    private NfcAdapter mAdapter;
    private AlertDialog mDialog;
    private String rfid;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    private NavController navController;

    private KiemLieuViewModel kiemLieuViewModel;

    LoggedInUserView loggedInUserView = new LoggedInUserView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiem_lieu);
        Toolbar toolbar = findViewById(R.id.kl_toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.kl_drawer_layout);
        NavigationView navigationView = findViewById(R.id.kl_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_scan_nfc_kl, R.id.nav_list_kl, R.id.nav_kl_da_duyet)
                .setDrawerLayout(drawer)
                .build();

        // Lấy thông tin cài đặt xem nếu có quyền duyệt thì hiển thị thêm tab hủy
        if (getReferenceTypeConfirm()) {
            navigationView.getMenu().getItem(02).setVisible(true);
        } else {
            navigationView.getMenu().getItem(2).setVisible(false);
        }

        navController = Navigation.findNavController(this, R.id.kl_nav_host_fragment);
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
            startActivity(new Intent(KiemLieuActivity.this, LoginActivity.class));
            finish();
        }
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
        btnSetting.setOnClickListener(v -> startActivity(new Intent(KiemLieuActivity.this, SettingsActivity.class)));

        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

        kiemLieuViewModel = new ViewModelProvider(this).get(KiemLieuViewModel.class);


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

        resolveIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.kiem_lieu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.kl_nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        rfid = null;
        setIntent(intent);
        // Check if the fragment is an instance of the right fragment
        if (labelCurrentFrag.equals(getString(R.string.list_kl_chưa_duyet))) {
            // Switch to Scan NFC fragment
            navController.navigate(R.id.nav_scan_nfc_kl);

//            if (rfid != null) {
//
        }
        resolveIntent(intent);

//        }
    }

    protected void logout() {
        // Move to Login Activity
        startActivity(new Intent(KiemLieuActivity.this, LoginActivity.class));
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

    private void checkNFC() {
        try {
            if (mAdapter == null) {
                showMessage(R.string.error, R.string.no_nfc);
            }
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
        } catch (Exception ex) {
            Log.e("ERROR NFC", ex.toString());
        }

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

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        try {
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                    || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                    || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            NdefMessage[] msgs;
//            if (rawMsgs != null) {
//                msgs = new NdefMessage[rawMsgs.length];
//                for (int i = 0; i < rawMsgs.length; i++) {
//                    msgs[i] = (NdefMessage) rawMsgs[i];
//                }
//            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});

//            }
            }
        } catch (Exception e) {
            Log.e("NFC Exception: ", e + "");
        }
    }

    private String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append(toDec(id));
        kiemLieuViewModel.setRFID("0" + sb.toString());
        return rfid = "0" + sb.toString();
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    /**
     * lấy dữ liệu Setting có cho phép kiểm liệu duyệt phiếu hay không
     * Cho phép kiểm liệu duyệt phiếu: true
     * Không cho phép kiểm liệu duyệt phiếu: false
     *
     * @return
     */
    private Boolean getReferenceTypeConfirm() {
        TransferData transferData = TransferData.getInstance(this);
        return transferData.getBoolean("ALLOW_KL_CONFIRM", false);

    }

}