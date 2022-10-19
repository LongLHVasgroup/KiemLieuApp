package com.example.vasclientv2.admin.adminsetting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.vasclientv2.R;
import com.example.vasclientv2.ui.TransferData;
import com.example.vasclientv2.userconfig.ChangePasswordActivity;

public class AdminSettingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        //Tool Bar
        toolbar = findViewById(R.id.setting_toolbar);
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.admin_preferences, rootKey);



            //Khai báo SharedReferences
            TransferData transferData = TransferData.getInstance(getActivity());
            ListPreference selected_type = (ListPreference) getPreferenceManager().findPreference("TYPE_TRU");
            ListPreference allow_confirm_type = (ListPreference) getPreferenceManager().findPreference("TYPE_ALLOW_CONFIRM");
            Preference changPass = getPreferenceManager().findPreference("CHANGE_PASS");

            selected_type.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("kg_and_percent")) {// Lấy cả Kg và phần trăm
                        transferData.saveBoolean("IS_GET_BOTH", true);
                    } else {
                        transferData.saveBoolean("IS_GET_BOTH", false);
                    }
                    return true;
                }
            });

            allow_confirm_type.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("allow_kl_confirm")) {// Lấy cả Kg và phần trăm
                        transferData.saveBoolean("ALLOW_KL_CONFIRM", true);
                    } else {
                        transferData.saveBoolean("ALLOW_KL_CONFIRM", false);
                    }
                    return true;
                }
            });

            changPass.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getContext(), ChangePasswordActivity.class));
                    return true;
                }
            });
        }
    }
}