package com.ethanransdell.mylists;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ethan on 10/25/2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context){
        super(context,"my_lists",null,1);
    }
    public void onCreate(SQLiteDatabase db) {}
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}
}