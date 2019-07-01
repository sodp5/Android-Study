package com.example.webbluetoothsample.db;

public interface DBTableConstant {
    String ID = "id";
    String PKEY = "pkey";
    String DATA = "data";
    String TABLENAME = "WZ_DATA";
    String CREATE = "create table if not exists " + TABLENAME + "("
            + ID   + " integer primary key autoincrement, "
            + PKEY  + " text not null , "
            + DATA + " text not null );";
}
