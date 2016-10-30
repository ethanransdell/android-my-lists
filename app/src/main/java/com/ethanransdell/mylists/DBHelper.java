package com.ethanransdell.mylists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        Cursor lists;
        Cursor items;
        db.execSQL("ALTER TABLE lists ADD COLUMN priority INTEGER;");
        db.execSQL("ALTER TABLE list_items ADD COLUMN priority INTEGER;");
        db.execSQL("UPDATE lists SET priority = 1;");
        db.execSQL("UPDATE list_items SET priority = 1;");
        lists = db.rawQuery("SELECT * FROM lists ORDER BY _id;", null);
        int listPriority = 0;
        while (lists.moveToNext()) {
            db.execSQL("UPDATE lists SET priority = " + listPriority + " WHERE _id = " + lists.getInt(0) + ";");
            items = db.rawQuery("SELECT * FROM list_items WHERE list_id = " + lists.getString(0) + " ORDER BY priority, _id;", null);
            int i = 0;
            while (items.moveToNext()) {
                db.execSQL("UPDATE list_items SET priority = " + i + " WHERE _id = " + items.getInt(0) + ";");
                i++;
            }
            listPriority++;
        }
    }

    public void firstRun(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS lists(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_name TEXT);");
//        db.execSQL("CREATE TABLE IF NOT EXISTS list_items(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_id INTEGER, list_item_name TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS lists(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_name TEXT, priority INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS list_items(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_id INTEGER, list_item_name TEXT, priority INTEGER);");
        System.out.println("Created tables.");
        ContentValues sampleListsValues;
        ContentValues sampleItemsValues;
        Long newListRowId;
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
                db.insert("list_items", null, sampleItemsValues);
            }
        }
    }

    public void sortItems(String listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor items = getItems(listId);
        int i = 0;
        while (items.moveToNext()) {
            db.execSQL("UPDATE list_items SET priority = " + i + " WHERE _id = " + items.getInt(0) + ";");
            i++;
        }
    }

    public boolean moveItem(String listId, String movingItemId, boolean up) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor items = db.rawQuery("SELECT * FROM list_items WHERE list_id = " + listId + " ORDER BY priority, _id;", null);
        // Create list of _id in order of priority
        List<Integer> itemsList = new ArrayList<>();
        while (items.moveToNext()) {
            itemsList.add(items.getInt(0));
        }
        if (!itemsList.contains(Integer.parseInt(movingItemId))) {
            return false;
        } else {
            int movingItemPriority = itemsList.indexOf(Integer.parseInt(movingItemId));
            if (up && movingItemPriority == 0) {
                return false;
            } else if (!up && movingItemPriority == itemsList.size() - 1) {
                return false;
            } else {
                int displacedItemPriority;
                if (up) {
                    displacedItemPriority = movingItemPriority - 1;
                } else {
                    displacedItemPriority = movingItemPriority + 1;
                }
                int displacedItemId = itemsList.get(displacedItemPriority);
                itemsList.set(displacedItemPriority, Integer.parseInt(movingItemId));
                itemsList.set(movingItemPriority, displacedItemId);
                return true;
            }
        }

    }

    public void addList(String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("list_name", listName);
        contentValues.put("priority", getLists().getCount());
        db.insert("lists", null, contentValues);
    }

    public void addItem(String listId, String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("list_id", listId);
        contentValues.put("list_item_name", itemName);
        contentValues.put("priority", getItems(listId).getCount());
        db.insert("list_items", null, contentValues);
    }

    public void deleteList(String listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("list_items", "list_id = ?", new String[]{listId});
        db.delete("lists", "_id = ?", new String[]{listId});
    }

    public void deleteItem(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("list_items", "_id = ?", new String[]{itemId});
    }

    public Cursor getLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor lists = db.rawQuery("SELECT * FROM lists ORDER BY priority, _id;", null);
        return lists;
    }

    public Cursor getItems(String listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor items = db.rawQuery("SELECT * FROM list_items WHERE list_id = " + listId + " ORDER BY priority, _id;", null);
        return items;
    }

    public void printTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + tableName + " ORDER BY priority, _id;", null);
        System.out.println("CONTENTS OF " + tableName + " TABLE:");
        while (results.moveToNext()) {
            for (int i = 0; i < results.getColumnCount(); i++) {
                System.out.print(results.getString(i) + "\t");
            }
            System.out.println();
        }
    }

}