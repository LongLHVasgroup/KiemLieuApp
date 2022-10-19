package com.example.vasclientv2.setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.vasclientv2.R;
import com.example.vasclientv2.userconfig.ChangePasswordActivity;

public class SettingsActivity extends AppCompatActivity {

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
            setPreferencesFromResource(R.xml.user_preferences, rootKey);

            //Khai báo SharedReferences
//            TranferData tranferData = TranferData.getInstance(getActivity());
//            ListPreference selected_type = (ListPreference) getPreferenceManager().findPreference("TYPE_TRU");


//            selected_type.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    if (newValue.toString().equals("kg_and_percent")) {// Lấy cả Kg và phần trăm
//                        tranferData.saveBoolean("IS_GET_BOTH", true);
//                    } else {
//                        tranferData.saveBoolean("IS_GET_BOTH", false);
//                    }
//                    return true;
//                }
//            });

            Preference changPass = getPreferenceManager().findPreference("CHANGE_PASS");
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