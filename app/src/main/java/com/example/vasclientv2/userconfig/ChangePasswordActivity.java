package com.example.vasclientv2.userconfig;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vasclientv2.R;
import com.example.vasclientv2.model.common.AcctionMessage;
import com.example.vasclientv2.model.common.TempTable;
import com.example.vasclientv2.ui.WareHouse;


public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPass, newPass, repeatPass;
    private Button btnSave;
    private Toolbar toolbar;
    private ChangePasswordViewModel changePasswordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        changePasswordViewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);

        changePasswordViewModel.getChangePasswordResult().observe(this, new Observer<AcctionMessage>() {
            @Override
            public void onChanged(AcctionMessage acctionMessage) {
                if (acctionMessage != null) {
                    if (acctionMessage.getIsSuccess()) {
                        AlertDialog alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this)
                                .setTitle("Thành công")
                                .setMessage(acctionMessage.getErr().getMsgString().toString())
                                .setPositiveButton("OK", (dialog, which) -> {
                                    finish();
                                }).show();

                    } else {
                        /**
                         *  Đổi mk không thành công
                         *  Show alert dialog
                         */

                        AlertDialog alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this)
                                .setTitle("Không thành công!!!")
                                .setMessage(acctionMessage.getErr().getMsgString().toString())
                                .setPositiveButton("OK", (dialog, which) -> {

                                }).show();

                    }
                }
            }
        });
        addControlls();

        addEvents();


    }

    private void addEvents() {
        btnSave.setOnClickListener(v -> {

            TempTable changePassModel = new TempTable();
            changePassModel.setUserID(WareHouse.UserId);
            changePassModel.setOldPass(currentPass.getText().toString());
            changePassModel.setNewPass(newPass.getText().toString());
            changePassModel.setNewPass1(repeatPass.getText().toString());
            changePasswordViewModel.changePassword(changePassModel);
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
                changePasswordViewModel.changePassDataChanged(currentPass.getText().toString(), newPass.getText().toString(),
                        repeatPass.getText().toString());
            }
        };

        currentPass.addTextChangedListener(afterTextChangedListener);
        newPass.addTextChangedListener(afterTextChangedListener);
        repeatPass.addTextChangedListener(afterTextChangedListener);
    }

    private void addControlls() {
        currentPass = findViewById(R.id.txt_current_pass);
        newPass = findViewById(R.id.txt_new_pass);
        repeatPass = findViewById(R.id.txt_repeat__pass);
        btnSave = findViewById(R.id.btnSaveChangePass);
        //Tool Bar
        toolbar = findViewById(R.id.change_password_toolbar);
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


        changePasswordViewModel.getChangePassFormState().observe(this, new Observer<ChangePassFormState>() {
            @Override
            public void onChanged(ChangePassFormState changePassFormState) {
                if (changePassFormState == null) {
                    return;
                }
                btnSave.setEnabled(changePassFormState.isDataValid());
                if (changePassFormState.getCurrentPassError() != null) {
                    currentPass.setError(getString(changePassFormState.getCurrentPassError()));
                }
                if (changePassFormState.getNewPassError() != null) {
                    newPass.setError(getString(changePassFormState.getNewPassError()));
                }
                if (changePassFormState.getRepeatPassError() != null) {
                    repeatPass.setError(getString(changePassFormState.getRepeatPassError()));
                }
            }
        });



    }

}