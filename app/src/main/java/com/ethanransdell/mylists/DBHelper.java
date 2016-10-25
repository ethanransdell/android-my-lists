package com.ethanransdell.mylists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ethan on 10/25/2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "my_lists", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    }

    public boolean insertList(String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("list_name", listName);
        db.insert("lists", null, contentValues);
        return true;
    }

    public boolean insertListItem(int listId, String listItemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("list_id", listId);
        contentValues.put("list_item_name", listItemName);
        db.insert("list_items", null, contentValues);
        return true;
    }

    public Cursor getLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from lists", null);
        return res;
    }

    public Cursor getListItems(int listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from lists where list_id = " + listId + "", null);
        return res;
    }
}