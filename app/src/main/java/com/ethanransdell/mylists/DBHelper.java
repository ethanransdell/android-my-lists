package com.ethanransdell.mylists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

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

    public boolean insertListItem(String listId, String listItemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("list_id", listId);
        contentValues.put("list_item_name", listItemName);
        db.insert("list_items", null, contentValues);
        return true;
    }

    public boolean deleteList(String listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int r1 = db.delete("list_items", "list_id = ?", new String[]{listId});
        int r2 = db.delete("lists", "_id = ?", new String[]{listId});
        return true;
    }

    public boolean deleteListItem(String listItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int r = db.delete("list_items", "_id = ?", new String[]{listItemId});
        return true;
    }

    public Cursor getLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from lists", null);
        return res;
    }

    public Cursor getListItems(String listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from list_items where list_id = " + listId + "", null);
        return res;
    }

    public boolean firstRun() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("CREATE TABLE IF NOT EXISTS lists(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_name TEXT);", null);
        db.rawQuery("CREATE TABLE IF NOT EXISTS list_items(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_id INTEGER, list_item_name TEXT);", null);
        System.out.println("Created tables.");
        ContentValues sampleListsValues;
        ContentValues sampleListItemsValues;
        Long newListRowId;
        Long newListItemRowId;
        for (int i = 0; i < 3; i++) {
            sampleListsValues = new ContentValues();
            sampleListsValues.put("list_name", "Sample List " + (i + 1));
            newListRowId = db.insert("lists", null, sampleListsValues);
            for (int j = 0; j < 5; j++) {
                sampleListItemsValues = new ContentValues();
                sampleListItemsValues.put("list_id", newListRowId.toString());
                sampleListItemsValues.put("list_item_name", "Sample Item " + (j + 1));
                newListItemRowId = db.insert("list_items", null, sampleListItemsValues);
            }
        }
        return true;
    }
}