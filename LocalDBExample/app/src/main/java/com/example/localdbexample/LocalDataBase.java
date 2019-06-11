package com.example.localdbexample;

import android.provider.BaseColumns;

public class LocalDataBase {
    public static final class CreateDB implements BaseColumns {
        public static final String USERID = "userid";
        public static final String NAME = "name";
        public static final String AGE = "age";
        public static final String GENDER = "gender";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists " + _TABLENAME0 + "("
                + _ID + " integer primary key autoincrement, "
                + USERID + " text not null , "
                + NAME + " text not null , "
                + AGE + " integer not null , "
                + GENDER + " text not null );";

        public static final String MYTEXT = "mytext";
        public static final String _TABLENAME1 = "mytable";
        public static final String _CREATE1 = "create table if not exists " + _TABLENAME1 + "("
                + _ID + " integer primary key autoincrement, "
                + MYTEXT + " text not null );";
    }
}