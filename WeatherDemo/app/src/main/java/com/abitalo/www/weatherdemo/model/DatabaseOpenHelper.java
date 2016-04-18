package com.abitalo.www.weatherdemo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lancelot on 2015/11/29.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public DatabaseOpenHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists CITYINFO(" +
                "_ID integer primary key autoincrement," +
                "CITYNAME text not null," +
                "AQI integer not null," +
                "QUALITY text not null," +
                "DATE bigint not null," +
                "WEATHER text not null," +
                "TEMPERATURE_LOW integer not null," +
                "TEMPERATURE_HIGH integer not null)"
        );

    }

    @Override
         public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
