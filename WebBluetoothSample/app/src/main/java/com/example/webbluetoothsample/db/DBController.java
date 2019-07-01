package com.example.webbluetoothsample.db;

import android.content.Context;
import android.database.Cursor;

public class DBController {
    private DBOpenHelper dbOpenHelper;

    private DBController() {
    }

    private static final class Singleton {
        private static final DBController instance = new DBController();
    }

    public static DBController getInstance() {
        return Singleton.instance;
    }

    public void initController(Context context) {
        dbOpenHelper = new DBOpenHelper(context);

        dbOpenHelper.open();
        dbOpenHelper.create();

        String temp = "";
        for (int i = 1; i < 100; i++) {
            if (i < 10)
                temp = "search00" + i;
            else
                temp = "search0" + i;
            put(temp, "NONE");
        }
        put("search100", "NONE");
    }

    private void insertData(String key, String data) {
        dbOpenHelper.insert(key, data);
    }

    private void updateData(String key, String data) {
        dbOpenHelper.update(key, data);
    }

    public void put(String key, String data) {
        if (dbOpenHelper.select(key).getCount() == 0) {
            insertData(key, data);
        }
        else {
            updateData(key, data);
        }
    }

    public String get(String key) {
        Cursor c = dbOpenHelper.select2(key);
        StringBuilder sb = new StringBuilder();
        while(c.moveToNext())
            sb.append(c.getString(c.getColumnIndex(DBTableConstant.PKEY))).append(", ").append(c.getString(c.getColumnIndex(DBTableConstant.DATA))).append("\n");
        return sb.toString();
    }

    public void del(String key) {
        dbOpenHelper.delete(key);
    }
}
