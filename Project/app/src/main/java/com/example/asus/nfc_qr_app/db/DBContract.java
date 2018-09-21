package com.example.asus.nfc_qr_app.db;

public class DBContract {
    public static final int sync_status_ok = 0;
    public static final int sync_status_fail = 1;
    public static final String SERVER_URL = "http://192.168.8.100:8080/user/";


    public static final String DATABASE_NAME = "cdap.db";
    public static final String TABLE_NAME = "users";


    public static final String NAME = "name";
    public static final String REGNO = "regno";
    public static final String NIC = "nic";
    public static final String PHOTO = "photo";
    public static final String SYNC_STATUS = "sync_status";
}
