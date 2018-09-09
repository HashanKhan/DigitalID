package com.example.asus.nfc_qr_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.sql.Blob;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "create table "+ DBContract.TABLE_NAME + "(" + DBContract.NAME +" text,"
            + DBContract.REGNO +" text primary key,"+ DBContract.NIC +" text,"+ DBContract.PHOTO +" blob,"
            + DBContract.SYNC_STATUS +" integer)";

    private static final String DROP_TABLE = "drop table if exists "+DBContract.TABLE_NAME;

    public DbHelper(Context context){
        super(context,DBContract.DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void saveToLocaleDataBase(String name, String regno, String nic, String photo, int sync_status,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.NAME,name);
        contentValues.put(DBContract.REGNO,regno);
        contentValues.put(DBContract.NIC,nic);
        contentValues.put(DBContract.PHOTO,photo);
        contentValues.put(DBContract.SYNC_STATUS,sync_status);
        database.insert(DBContract.TABLE_NAME,null,contentValues);
    }

    public Cursor readFromLocaleDataBase(SQLiteDatabase database){
        String[] projection = {DBContract.NAME,DBContract.REGNO,DBContract.NIC,DBContract.PHOTO,DBContract.SYNC_STATUS};
        return (database.query(DBContract.TABLE_NAME,projection,null,null,null,null,null));
    }

    /*public void updateLocaleDataBase(String regno, String name, String nic, Blob photo, int sync_status,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();

    }*/
}
