package com.example.vasclientv2.ui;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class WareHouse {
    // Lưu ip để gọi API
    public static String Url = "";
    // Ip Mặc đinh cho lần đầu tiên cài ứng dụng
    public static String UrlOrgin = "10.0.6.191:90";
    //User
    public static String UserId;
    public static String groupUser;
    //end user
    public static String token = "45FCC8F419313AZ559E2DED09B9AF94";
    public static String key = "19ac858c-6517-45ce-a22e-095aff5cffff";
    public static String fcmServer = "https://fcm.googleapis.com/";
    public static String serverKey = "key=AAAAKbjamX8:APA91bHnZEJZ6TVCz7pTu7xisG0FZjO93RHtTMHBQnoJhZ26Z75DEtXrmDdfl2tYOUhxkrZ1Ra6h30T_1aYrtVlMcVJGDT3hpAbs959aWQ1Uvq4Tng8AkZvwSdNeZ60BA_ic_rx6u5dB";
    public static String contentType = "application/json";
    // thong tin company
    public static ArrayList<String> listCompanyName = new ArrayList<String>() {
        {
            add("1000 - NIS - Nghi Sơn");
            add("2000 - NSIP - Cảng Nghi Sơn");
            add("3000 - NSS - Thép Nghi Sơn");
            add("4000 - AHT - An Hưng Tường");
            add("5000 - VMS - Thép Việt Mỹ");
            add("6000 - TMS - Tuệ Minh");
            add("7000 - DNS - Thép Đà Nẵng");
        }
    };
    public static ArrayList<String> listCompanyCode = new ArrayList<String>() {
        {
            add("1000");
            add("2000");
            add("3000");
            add("4000");
            add("5000");
            add("6000");
            add("7000");
        }
    };
}
