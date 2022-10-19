package com.example.vasclientv2.admin.createuser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vasclientv2.R;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.entities.UserModel;
import com.example.vasclientv2.ui.WareHouse;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateUserActivity extends AppCompatActivity {

    final private static String TAG = "CreateUserActivity";

    private Toolbar toolbar;
    private Button btnCreate;
    private Spinner spRole, spGroupUser;
    private ProgressDialog progressDialog;
    private AlertDialog resultDialog;
    private EditText txtUserName, txtFullName;
    private ArrayList<String> roles, groupUser;
    private Call<AcctionMessage> callCreateUser;
    private UserModel userModel;

    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        setupToolBar();

        addItems();
        addControll();

        addEvents();

    }

    private void setupToolBar() {
        toolbar = findViewById(R.id.create_user_toolbar);
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
    }

    private void addItems() {

        roles = new ArrayList<>();
        roles.add("ADMIN");
        roles.add("NHANVIEN");
        roles.add("VANPHONG");
        roles.add("USERCAN");
        roles.add("SUADATACAN");
        roles.add("XEMBAOCAO");
        roles.add("PHONGMUAHANG");
        roles.add("PHONGKINHDOANH");
        roles.add("KETOANNL");
        roles.add("THANHTOAN");
        roles.add("BAOVE");
        roles.add("KIEMLIEU");
        roles.add("TRUONGKIEMLIEU");
        roles.add("NORMAL");

//        // Nhóm người dùng
//        groupUser = new ArrayList<>();
//        groupUser.add("TEST");
    }

    private void addEvents() {
        btnCreate.setOnClickListener(v -> {
            createUser();
        });
    }

    private void createUser() {

        if (isValidData()) {
            progressDialog.show();
            userModel = new UserModel();
            userModel = getDataForm();

            //Api
            apiInterface = ApiService.getClient().create(ApiInterface.class);
            callCreateUser = apiInterface.CreateUser(WareHouse.key, WareHouse.token,WareHouse.UserId, userModel);
            callCreateUser.enqueue(new Callback<AcctionMessage>() {
                @Override
                public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                    try {
                        progressDialog.dismiss();
                        if (response.body().getIsSuccess()) {
                            showResultDialog("Thành công", response.body().getErr().getMsgString().toString());
                            // Xóa trống các text field khi tạo thành công user mới
                            clearTextField();
                        } else {
                            showResultDialog("Thành công", response.body().getErr().getMsgString().toString());
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<AcctionMessage> call, Throwable t) {
                    progressDialog.dismiss();
                    showResultDialog("Không thành công", "Không có kết nối");
                }
            });
            Log.d(TAG, userModel.toString());
        } else {
            Toast.makeText(this, "Vui lòng điền đủ thông tin", Toast.LENGTH_LONG).show();
        }
    }

    private void showResultDialog(String title, String msgString) {
        resultDialog.setTitle(title);
        resultDialog.setMessage(msgString);
        resultDialog.show();
    }

    private void clearTextField() {
        txtFullName.setText("");
        txtUserName.setText("");
    }

    private UserModel getDataForm() {
        UserModel tempUser = new UserModel();
        tempUser.setFullName(txtFullName.getText().toString().trim());
        tempUser.setUserName(txtUserName.getText().toString().trim());
        tempUser.setRoldCode(spRole.getSelectedItem().toString());
//        tempUser.setGroupUser(spGroupUser.getSelectedItem().toString());
        tempUser.setActived(true);
        tempUser.setPasswordEnscrypt(generatePass());

        return tempUser;
    }

    private boolean isValidData() {
        if (txtUserName.getText().toString().trim().equals("")) {
            txtUserName.setError("Vui lòng nhập!");
            txtUserName.requestFocus();
            return false;
        } else if (txtFullName.getText().toString().trim().equals("")) {
            txtFullName.setError("Vui lòng nhập!");
            txtFullName.requestFocus();
            return false;
        }
        return true;
    }

    private void addControll() {
        btnCreate = findViewById(R.id.btn_create);
        txtUserName = findViewById(R.id.txt_user_name);
        txtFullName = findViewById(R.id.txt_fullname);
        spRole = findViewById(R.id.sp_role);
//        spGroupUser = findViewById(R.id.sp_group_user);

        ArrayAdapter roleAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);
        spRole.setSelection(0);

//        ArrayAdapter groupUserAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, groupUser);
//        groupUserAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spGroupUser.setAdapter(groupUserAdapter);
//        spGroupUser.setSelection(0);

        // Dialog chờ tạo mới user
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage(getResources().getString(R.string.please_wait)); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner

        // Dialog thông báo kết quả tạo user
        resultDialog = new AlertDialog.Builder(this)
                .setPositiveButton("OK", (dialog, which) -> {

                })
                .create();

    }

    public String generatePass() {
        // get ramdom string to hash
        long unixTime = System.currentTimeMillis();
        return md5(String.valueOf(unixTime));
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


}