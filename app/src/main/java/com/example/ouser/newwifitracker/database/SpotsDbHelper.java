package com.example.ouser.newwifitracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SpotsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "spots.db";

    public SpotsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SPOTS_TABLE = "CREATE TABLE " + SpotContract.SpotEntry.TABLE_NAME + " (" +
                SpotContract.SpotEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SpotContract.SpotEntry.COLUMN_SPOT_NAME + " TEXT NOT NULL, " +
                SpotContract.SpotEntry.COLUMN_SPOT_DATE_CREATED + " TEXT, " +
                SpotContract.SpotEntry.COLUMN_SPOT_WIFI_DATA + " TEXT);";
        db.execSQL(SQL_CREATE_SPOTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
