package com.example.webbluetoothsample.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper {
    private static final String DATABASE_NAME = "WZ.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteDatabase database;
    private DataBaseHelper dataBaseHelper;
    private Context context;

    private class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DBTableConstant.CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DBTableConstant.TABLENAME);
            onCreate(db);
        }
    }

    public void dropTable() {
        database.execSQL("DROP TABLE IF EXISTS "+DBTableConstant.TABLENAME);
    }

    public DBOpenHelper(Context context){
        this.context = context;
    }

    public DBOpenHelper open() throws SQLException {
        dataBaseHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = dataBaseHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        dataBaseHelper.onCreate(database);
    }

    public void close(){
        database.close();
    }

    public long insert(String key, String data){
        ContentValues values = new ContentValues();
        values.put(DBTableConstant.PKEY, key);
        values.put(DBTableConstant.DATA, data);
        return database.insert(DBTableConstant.TABLENAME, null, values);
    }

    public Cursor select(String key){
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        return db.query(DBTableConstant.TABLENAME, new String[] {"pkey", "data"}, "pkey = '" + key + "'", null, null, null, null);
    }

    public Cursor select2(String key) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        String tempString = "SELECT pkey, data FROM " + DBTableConstant.TABLENAME + " WHERE pkey like '" + key + "'";
        return db.rawQuery(tempString, null);
    }

    public void update(String key, String data){
        ContentValues values = new ContentValues();
        values.put(DBTableConstant.PKEY, key);
        values.put(DBTableConstant.DATA, data);
        database.update(DBTableConstant.TABLENAME, values, "pkey =  '" + key + "'", null);
    }

    public long delete(String key) {
        return database.delete(DBTableConstant.TABLENAME, "pkey = '" + key + "'", null);
    }
}
