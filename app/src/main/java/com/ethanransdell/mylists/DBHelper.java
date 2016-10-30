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
        super(context, "my_lists", null, 2);
    }

    public void onCreate(SQLiteDatabase db) {
        firstRun(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            upgradeTo2(db);
        }
    }

    public void upgradeTo2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE lists ADD COLUMN priority INTEGER;");
        db.execSQL("ALTER TABLE list_items ADD COLUMN priority INTEGER;");
        db.execSQL("UPDATE lists SET priority = 1;");
        db.execSQL("UPDATE list_items SET priority = 1;");
        Cursor lists = db.rawQuery("SELECT * FROM lists ORDER BY _id;", null);
        int listPriority = 0;
        while (lists.moveToNext()) {
            db.execSQL("UPDATE lists SET priority = " + listPriority + " WHERE _id = " + lists.getInt(0) + ";");
            Cursor items = db.rawQuery("SELECT * FROM list_items WHERE list_id = " + lists.getInt(0) + " ORDER BY _id;", null);
            int itemPriority = 0;
            while (items.moveToNext()) {
                db.execSQL("UPDATE list_items SET priority = " + itemPriority + " WHERE _id = " + items.getInt(0) + ";");
                itemPriority++;
            }
            listPriority++;
        }
    }

    public boolean firstRun(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS lists(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_name TEXT);");
//        db.execSQL("CREATE TABLE IF NOT EXISTS list_items(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_id INTEGER, list_item_name TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS lists(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_name TEXT, priority INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS list_items(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_id INTEGER, list_item_name TEXT, priority INTEGER);");
        System.out.println("Created tables.");
        ContentValues sampleListsValues;
        ContentValues sampleItemsValues;
        Long newListRowId;
        Long newItemRowId;
        for (int i = 0; i < 3; i++) {
            sampleListsValues = new ContentValues();
            sampleListsValues.put("list_name", "Sample List " + (i + 1));
            sampleListsValues.put("priority", i);
            newListRowId = db.insert("lists", null, sampleListsValues);
            for (int j = 0; j < 5; j++) {
                sampleItemsValues = new ContentValues();
                sampleItemsValues.put("list_id", newListRowId.toString());
                sampleItemsValues.put("list_item_name", "Sample Item " + (j + 1));
                sampleItemsValues.put("priority", j);
                newItemRowId = db.insert("list_items", null, sampleItemsValues);
            }
        }
        return true;
    }

    public boolean addList(String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("list_name", listName);
        contentValues.put("priority", getLists().getCount());
        db.insert("lists", null, contentValues);
        return true;
    }

    public boolean addItem(String listId, String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("list_id", listId);
        contentValues.put("list_item_name", itemName);
        contentValues.put("priority", getItems(listId).getCount());
        db.insert("list_items", null, contentValues);
        return true;
    }

    public boolean deleteList(String listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int r1 = db.delete("list_items", "list_id = ?", new String[]{listId});
        int r2 = db.delete("lists", "_id = ?", new String[]{listId});
        return true;
    }

    public boolean deleteItem(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int r = db.delete("list_items", "_id = ?", new String[]{itemId});
        return true;
    }

    public Cursor getLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM lists;", null);
        return results;
    }

    public Cursor getItems(String listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM list_items WHERE list_id = " + listId + ";", null);
        return results;
    }

    public void printTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + tableName + " ORDER BY _id;", null);
        System.out.println("CONTENTS OF " + tableName + " TABLE:");
        while (results.moveToNext()) {
            for (int i = 0; i < results.getColumnCount(); i++) {
                System.out.print(results.getString(i) + "\t");
            }
            System.out.println();
        }
    }

}