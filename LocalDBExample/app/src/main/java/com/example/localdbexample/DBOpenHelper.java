package com.example.localdbexample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper {

    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DataBaseHelper mDBHelper;
    private Context mCtx;

    private class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(LocalDataBase.CreateDB._CREATE1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+LocalDataBase.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public DBOpenHelper(Context context){
        this.mCtx = context;
    }

    public DBOpenHelper open() throws SQLException {
        mDBHelper = new DataBaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    public long insertColumn(String userid, String name, long age , String gender){
        ContentValues values = new ContentValues();
        values.put(LocalDataBase.CreateDB.USERID, userid);
        values.put(LocalDataBase.CreateDB.NAME, name);
        values.put(LocalDataBase.CreateDB.AGE, age);
        values.put(LocalDataBase.CreateDB.GENDER, gender);
        values.put(LocalDataBase.CreateDB.MYTEXT, userid);
        return mDB.insert(LocalDataBase.CreateDB._TABLENAME1, null, values);
    }

    public Cursor selectColumns(){
        return mDB.query(LocalDataBase.CreateDB._TABLENAME1, null, null, null, null, null, null);
    }

    public Cursor sortColumn(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM usertable ORDER BY " + sort + ";", null);
        return c;
    }

    public boolean updateColumn(long id, String userid, String name, long age , String gender){
        ContentValues values = new ContentValues();
        values.put(LocalDataBase.CreateDB.USERID, userid);
        values.put(LocalDataBase.CreateDB.NAME, name);
        values.put(LocalDataBase.CreateDB.AGE, age);
        values.put(LocalDataBase.CreateDB.GENDER, gender);
        return mDB.update(LocalDataBase.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }
    // Delete All
    public void deleteAllColumns() {
        mDB.delete(LocalDataBase.CreateDB._TABLENAME0, null, null);
    }

    // Delete Column
    public boolean deleteColumn(long id){
        return mDB.delete(LocalDataBase.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }
}
