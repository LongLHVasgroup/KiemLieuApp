package com.example.vasclientv2.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.vasclientv2.R;
import com.example.vasclientv2.admin.AdminActivity;
import com.example.vasclientv2.apiInterface.ApiInterface;
import com.example.vasclientv2.apiInterface.ApiService;
import com.example.vasclientv2.baove.BaoVeActivity;
import com.example.vasclientv2.kiemlieu.KiemLieuActivity;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.TempTable;
import com.example.vasclientv2.model.entities.UserModel;
import com.example.vasclientv2.truongkiemlieu.TruongKiemLieuActivity;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.ui.WareHouse;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private LoginViewModel loginViewModel;

    private LayoutInflater inflater;
    private View alertLayout;
    private UpdatePassViewModel updatePassViewModel;
    private AlertDialog.Builder alert;
    private AlertDialog dialog;

    //element update password
    private EditText txtNewPass, txtRepeatPass;
    private CheckBox cbShowPassword;
    private Button btnSave;
    // Copyright
    private TextView txtCopyright;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText txtIp = findViewById(R.id.txtIp);
        txtCopyright = findViewById(R.id.txtCopyright);

        final Button loginButton = findViewById(R.id.login);
        final Button setIPButton = findViewById(R.id.btnSetIP);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        // Get ip from reference
        TransferData transferData = TransferData.getInstance(getApplication());
        final String ipFromRef = transferData.getData("IP_SERVER", null);
        if (ipFromRef != null) {
            txtIp.setText(ipFromRef);
        } else {
            txtIp.setText(WareHouse.UrlOrgin);
        }

        // Set the copyright
        try {
            txtCopyright.setText(getResources().getString(R.string.copyright, getPackageManager().getPackageInfo(getPackageName(), 0).versionName) +" "+  getString(R.string.copyright_date));
        } catch (Exception e) {
            txtCopyright.setText(getResources().getString(R.string.copyright, "1.0.0"));
        }

        // element update Password
        inflater = getLayoutInflater();
        alertLayout = inflater.inflate(R.layout.dialog_update_password, null);
        updatePassViewModel = new ViewModelProvider(this).get(UpdatePassViewModel.class);


        alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        dialog = alert.create();

        txtNewPass = alertLayout.findViewById(R.id.txt_new_pass);
        txtRepeatPass = alertLayout.findViewById(R.id.txt_repeat__pass);
        cbShowPassword = alertLayout.findViewById(R.id.cb_show_pass);
        btnSave = alertLayout.findViewById(R.id.btnSaveChangePass);


        // Bắt sự kiện thay đổi edittext
        TextWatcher afterTextPasswordChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePassViewModel.updatePassDataChanged(txtNewPass.getText().toString(), txtRepeatPass.getText().toString());
            }
        };


        txtNewPass.addTextChangedListener(afterTextPasswordChangedListener);
        txtRepeatPass.addTextChangedListener(afterTextPasswordChangedListener);
        //Chọn hiện mật khẩu
        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtNewPass.setTransformationMethod(null);
                    txtRepeatPass.setTransformationMethod(null);
                } else {
                    txtNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txtRepeatPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        updatePassViewModel.getUpdatePassFormState().observe(this, new Observer<UpdatePassFormState>() {
            @Override
            public void onChanged(UpdatePassFormState updatePassFormState) {
                if (updatePassFormState == null) {
                    return;
                }
                btnSave.setEnabled(updatePassFormState.isDataValid());
                if (updatePassFormState.getNewPassError() != null) {
                    txtNewPass.setError(getString(updatePassFormState.getNewPassError()));
                }
                if (updatePassFormState.getRepeatPassError() != null) {
                    txtRepeatPass.setError(getString(updatePassFormState.getRepeatPassError()));
                }
            }

        });


        updatePassViewModel.getUpdatePasswordResult().observe(this, new Observer<AcctionMessage>() {
            @Override
            public void onChanged(AcctionMessage acctionMessage) {
                try {
                    if (acctionMessage.getIsSuccess()) {
                        Toast.makeText(getApplicationContext(), acctionMessage.getErr().getMsgString().toString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), acctionMessage.getErr().getMsgString().toString(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                } catch (Exception e) {
                    Log.e("ERROR_UPDATE_PASS", e + "");
                }
            }
        });
        ////////////////////////////////end


        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getIpError() != null) {
                    txtIp.setError(getString(loginFormState.getIpError()));
                }
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getMustUpdatePass() != null) { // User bị reset pass buộc phải cập nhật mật khẩu mới

                    if (loginResult.getSuccess() != null) {
//                            Log.e("ERROR",loginResult.getMustUpdatePass()+"");
                        showUpdatePassword(loginResult.getSuccess());
                    }

                } else {
                    if (loginResult.getSuccess() != null) {
                        updateUiWithUser(loginResult.getSuccess());
                        //Complete and destroy login activity once successful
                        finish();
                    }
                }

                setResult(Activity.RESULT_OK);

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(txtIp.getText().toString(), usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        txtIp.addTextChangedListener(afterTextChangedListener);
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loadingProgressBar.setVisibility(View.VISIBLE);

                    // Lưu địa chỉ ip xuống Reference
                    TransferData transferData = TransferData.getInstance(getApplication());
                    transferData.saveData("IP_SERVER", txtIp.getText().toString());
                    // Lưu ip vào Warehouse để gửi APi
                    WareHouse.Url = txtIp.getText().toString();

                    // Gọi Login
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                // Lưu địa chỉ ip xuống Reference
                TransferData transferData = TransferData.getInstance(getApplication());
                transferData.saveData("IP_SERVER", txtIp.getText().toString());
                // Lưu ip vào Warehouse để gửi APi
                WareHouse.Url = txtIp.getText().toString();

                // Gọi Login
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        setIPButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                txtIp.setVisibility(View.VISIBLE);
                return false;
            }
        });
        setIPButton.setOnClickListener(v -> {
            txtIp.setVisibility(View.INVISIBLE);
        });
    }

    private void showUpdatePassword(UserModel success) {
        createDialogUpdatePassword(success);
        dialog.show();
    }

    private void updateUiWithUser(UserModel model) {
        String welcome = getString(R.string.welcome) + model.getFullName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();

        // Save WareHouse
        WareHouse.UserId = model.getUserId();
        WareHouse.groupUser = model.getGroupUser();

        // shared reference

//        tranferData.saveData("GROUP_USER", model.getGroupUser());
        switch (model.getRoldCode()) {
            case "BAOVE":
            case "KIEMLIEU":
            case "TRUONGKIEMLIEU":
            case "ADMIN":
                TransferData transferData = TransferData.getInstance(this);
                transferData.saveBoolean("IS_LOGGED", true);
                transferData.saveData("USER_ID", model.getUserId());
                transferData.saveData("FULL_NAME", model.getFullName());
                transferData.saveData("ROLE_CODE", model.getRoldCode());
                break;
        }

        //Update token user device

        // Get token device
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    Log.i("GetToken", task.getResult());
                    sendRegistrationToServer(task.getResult());
                }
            }
        });


        // Mở activiy Home
        switch (model.getRoldCode()) {
            case "BAOVE":
                Intent baoveIntent = new Intent(LoginActivity.this, BaoVeActivity.class);
                baoveIntent.putExtra("USER_ID", model.getUserId());
                baoveIntent.putExtra("FULL_NAME", model.getFullName());
                baoveIntent.putExtra("ROLE_CODE", model.getRoldCode());

                startActivity(baoveIntent);
                break;
            case "KIEMLIEU":
                Intent kiemlieuIntent = new Intent(LoginActivity.this, KiemLieuActivity.class);
                kiemlieuIntent.putExtra("USER_ID", model.getUserId());
                kiemlieuIntent.putExtra("FULL_NAME", model.getFullName());
                kiemlieuIntent.putExtra("ROLE_CODE", model.getRoldCode());
                startActivity(kiemlieuIntent);
                break;
            case "TRUONGKIEMLIEU":
                Intent truongkiemlieuIntent = new Intent(LoginActivity.this, TruongKiemLieuActivity.class);
                truongkiemlieuIntent.putExtra("USER_ID", model.getUserId());
                truongkiemlieuIntent.putExtra("FULL_NAME", model.getFullName());
                truongkiemlieuIntent.putExtra("ROLE_CODE", model.getRoldCode());
                startActivity(truongkiemlieuIntent);
                break;
            case "ADMIN":
                Intent adminIntent = new Intent(LoginActivity.this, AdminActivity.class);
                adminIntent.putExtra("USER_ID", model.getUserId());
                adminIntent.putExtra("FULL_NAME", model.getFullName());
                adminIntent.putExtra("ROLE_CODE", model.getRoldCode());
                startActivity(adminIntent);
                break;
            default:
                Toast.makeText(this, "Người dùng không có quyền sử dụng ứng dụng này", Toast.LENGTH_LONG).show();
                break;
        }

    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void sendRegistrationToServer(String token) {
        // gọi api lưu token lên server

        UserModel userModel = new UserModel();
        userModel.setUserId(WareHouse.UserId);
        userModel.setDeviceToken(token);

        // Call API
        ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
        Call<AcctionMessage> updateToken = apiInterface.UpdateToken(WareHouse.key, WareHouse.token, userModel);
        try {
            updateToken.enqueue(new Callback<AcctionMessage>() {
                @Override
                public void onResponse(Call<AcctionMessage> call, Response<AcctionMessage> response) {
                    Log.d(TAG, response.body().getErr().getMsgString() + "");
                }

                @Override
                public void onFailure(Call<AcctionMessage> call, Throwable t) {
                    Log.e(TAG, t + "");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e + "");
        }
    }

    private void createDialogUpdatePassword(UserModel success) {


        btnSave.setOnClickListener(v -> {
            TempTable tempTable = new TempTable();
            tempTable.setUserID(success.getUserId());
            tempTable.setNewPass(txtNewPass.getText().toString());
            tempTable.setNewPass1(txtRepeatPass.getText().toString());

            //Call update password
            updatePassViewModel.updatePassword(tempTable);

        });

    }
}