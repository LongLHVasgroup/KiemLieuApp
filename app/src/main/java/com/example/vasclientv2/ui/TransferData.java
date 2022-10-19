package com.example.vasclientv2.ui;

import android.content.Context;
import android.content.SharedPreferences;

public class TransferData {

    private static TransferData transferData;
    private SharedPreferences sharedPreferences;

    public static TransferData getInstance(Context context) {
        if (transferData == null) {
            transferData = new TransferData(context);
        }
        return transferData;
    }

    private TransferData(Context context) {
        sharedPreferences = context.getSharedPreferences("Ref", Context.MODE_PRIVATE);
    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
        prefsEditor.commit();
    }
    public void removeData(String key) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.remove(key);
        prefsEditor.apply();
        prefsEditor.commit();
    }
    public void saveBoolean(String key, Boolean value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public Boolean getBoolean(String key, Boolean defValue) {
        if (sharedPreferences!= null) {
            return sharedPreferences.getBoolean(key,defValue);
        }
        return false;
    }

    public String getData(String key, String defValue) {
        if (sharedPreferences!= null) {
            return sharedPreferences.getString(key,defValue);
        }
        return "";
    }

}
