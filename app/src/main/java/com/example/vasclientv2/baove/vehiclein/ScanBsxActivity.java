package com.example.vasclientv2.baove.vehiclein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vasclientv2.R;
import com.example.vasclientv2.model.entities.VehicleModel;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

public class ScanBsxActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Dialog numberPlateDialog;
    private ProgressDialog progressDialog;
    private VehicleInViewModel vehicleInViewModel;
    private ScanBsxViewModel scanBsxViewModel;
    private String numberPlate;
    private Vibrator vibrator;
    private TextView textView;
    private SurfaceView mCameraView;
    private CameraSource mCameraSource;
    private SparseArray<TextBlock> items = new SparseArray<>();
    private TextRecognizer textRecognizer;


    private static final String TAG = "ScanBsxActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bsx);

        vehicleInViewModel = new ViewModelProvider(ScanBsxActivity.this).get(VehicleInViewModel.class);

        scanBsxViewModel = new ViewModelProvider(ScanBsxActivity.this).get(ScanBsxViewModel.class);

        createControls();

        createEvents();

        observeData();

        startCameraSource();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    //.setRequestedPreviewSize(width, height)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(15.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(ScanBsxActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    items = detections.getDetectedItems();
//                    if (items.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 0; i < items.size(); i++) {
                                    String item1 = items.valueAt(i).getValue().replace(" ", "").replace("\n", "-");
                                    String rs = numberPlate(item1);
                                    if (!rs.isEmpty()) {
                                        if (!isShowingDialog()) {
                                                scanBsxViewModel.setNumberPlate(rs);
                                            return;
                                        }
                                    }
//                                        Log.e(TAG, items.size() + "");
                                    try {
                                        String item2 = (items.valueAt(i).getValue() + items.valueAt(i + 1).getValue()).replace(" ", "").replace("\n", "-");
                                        rs = numberPlate(item2);
                                        if (!rs.isEmpty()) {
                                            if (!isShowingDialog()) {
                                                scanBsxViewModel.setNumberPlate(rs);
                                                return;
                                            }
                                        }
                                    } catch (Exception e) {
//                                            Log.e(TAG, e + "");
                                    }
                                }
                            } catch (Exception e) {
//                                    Log.e(TAG, e + "");
                            }
                        }
                    });
//                    }
                }
            });
        }
    }

    private void removeDataCam() {
        // clear để khi bấm quét lại thì lấy dữ liệu mới
        items.clear();
    }

    private String numberPlate(String s) {
        String bsx = "";
        if (s.length() > 6 && s.length() < 11 && s.matches(".*[0-9]$")) {
            // check có - hay không
            if (!s.contains("-")) {
                // nếu không có dấu gạch thì thêm vào
                s = s.substring(0, 3) + "-" + s.substring(3);
            }
            if (!s.contains(".")) {
                // nếu không có dấu chấm
                if (s.length() > 8) {
                    // thêm dấu chấm vào
                    s = s.substring(0, 7) + "." + s.substring(7);
                }
            }

            if (s.matches("[0-9][0-9][A-Z]-[0-9][0-9][0-9][0-9]")
                    || s.matches("[0-9][0-9][A-Z]-[0-9][0-9][0-9]\\.[0-9][0-9]")) {
                bsx = String.valueOf(s);
            }
        }
        return bsx;
    }


    private void createVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(200);
        }
    }

    private void createControls() {
        //Tool Bar
        toolbar = findViewById(R.id.scan_bsx_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);


        mCameraView = findViewById(R.id.surface_view);
        textView = findViewById(R.id.txtText);


        // progressdialog khi call api lấy thong tin biển số xe và chuyển activity
        progressDialog = new ProgressDialog(ScanBsxActivity.this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage(getResources().getString(R.string.please_wait)); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);


        // Dialog Show Number plate: Biển Số Xe
        numberPlateDialog = new AlertDialog.Builder(ScanBsxActivity.this, THEME_HOLO_LIGHT)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    onConfirm();
                })
                .setNegativeButton(R.string.rescan, (dialog, which) -> {
                    onRescan();
                })
                .create();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Lưu text nhận được  từ camera
        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
    }

    private void createEvents() {
        // khi bấm vào nút Back trên toolBar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void observeData() {

        vehicleInViewModel.getVehicleModelLiveData().observe(this, vehicleModel -> {

            if (vehicleModel.getRegInt() == null) return;
            switch (vehicleModel.getRegInt()) {
                case 0:
//
                case 3:
                    // xe chưa khai báo, Xe mới
                    // show thông báo và cho vào
                    progressDialog.dismiss();
                    new AlertDialog.Builder(this)
                            .setPositiveButton("Cho Vào", (dialog, which) -> {
                                onConfirmVehicleIn(vehicleModel);
                            })
                            .setNeutralButton("Huỷ", (dialog, which) -> {
                                finish();
                            })
                            .setCancelable(false)
                            .setTitle(vehicleModel.getRegStatus())
                            .setMessage("Chỉ những xe không giao sắt phế liệu mới được cho phép vào mà không khai báo trước")
                            .create().show();
                    break;
                case 1:
                    // Xe cần kích hoạt trước khi vào
                    progressDialog.dismiss();

                    new AlertDialog.Builder(this)
                            .setPositiveButton("Huỷ", (dialog, which) -> {
                            })
                            .setTitle(vehicleModel.getRegStatus())
                            .setMessage("Xe chưa kích hoạt không cho phép vào")
                            .create().show();
                    break;
                case 4: // xe nội bộ
                case 2:
                    // xe đủ điều kiện vào
                    Intent intent = new Intent(ScanBsxActivity.this, AddCheckingScrapActivity.class);
                    intent.putExtra("VEHICLE_TYPE", vehicleModel.getTypeText());
                    intent.putExtra("VEHICLE_NUMBER", vehicleModel.getVehicleNumber());
                    intent.putExtra("DRIVER_NAME", vehicleModel.getDriverName());
                    intent.putExtra("DRIVER_ID", vehicleModel.getDriverIdCard());
                    intent.putExtra("WEB_REGISTERED_ID", vehicleModel.getVehicleRegisterMobileId());
//            intent.putExtra("VEHICLE_OWNER", (Serializable) vehicleModel.getVehicleOwner());
                    intent.putExtra("PROVIDERS", (Serializable) vehicleModel.getProviders());
                    intent.putExtra("ROMOOC", (Serializable) vehicleModel.getRomooc());

                    startActivity(intent);
                    finish();
                    break;

            }
        });

        // Bắt sự kiện để show dialog
        vehicleInViewModel.getIsloading().observe(this, aBoolean -> {
            if (aBoolean)
                progressDialog.show();
            else
                progressDialog.dismiss();
        });

        vehicleInViewModel.getTxtAlertResult().observe(this, string -> {
            if (!string.isEmpty()) {
                Toast.makeText(this, string, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        scanBsxViewModel.getNumberPlate().observe(this, s -> {
            createVibrate();
            numberPlate = s;
            numberPlateDialog.setTitle(s);
            numberPlateDialog.show();

        });
    }

    // Bấm nút xác nhận
    private void onConfirm() {
        mCameraSource.stop();
        removeDataCam();

//        vehicleInViewModel.getVehicleInfo(numberPlate);
        vehicleInViewModel.getVehicleInfoWeb(numberPlate);
    }


    private void onConfirmVehicleIn(VehicleModel vehicleModel) {
        Intent intent = new Intent(ScanBsxActivity.this, AddCheckingScrapActivity.class);
        intent.putExtra("VEHICLE_TYPE", vehicleModel.getTypeText());
        intent.putExtra("VEHICLE_NUMBER", numberPlate);
        intent.putExtra("DRIVER_NAME", vehicleModel.getDriverName());
        intent.putExtra("DRIVER_ID", vehicleModel.getDriverIdCard());
        intent.putExtra("WEB_REGISTERED_ID", vehicleModel.getVehicleRegisterMobileId());
//            intent.putExtra("VEHICLE_OWNER", (Serializable) vehicleModel.getVehicleOwner());
        startActivity(intent);
        finish();
    }


    private void onRescan() {
        removeDataCam();
    }

    private Boolean isShowingDialog() {
        return numberPlateDialog.isShowing();
    }
}