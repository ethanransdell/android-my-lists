package com.ethanransdell.mylists;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs = null;
    private Map<String, String> listsMap;
    private List<String> listsList;

    private Cursor dbResults;
    private DBHelper dbh = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("com.ethanransdell.mylists", MODE_PRIVATE);

        FloatingActionButton floating_new_list = (FloatingActionButton) findViewById(R.id.floating_new_list);
        floating_new_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddList();
            }
        });
        dbh.printTable("lists");
        dbh.printTable("list_items");
//        if (prefs.getBoolean("firstrun", true) || !prefs.contains("firstrun")) {
//            SQLiteDatabase db = openOrCreateDatabase("my_lists", MODE_PRIVATE, null);
//            db.execSQL("CREATE TABLE IF NOT EXISTS lists(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_name TEXT);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS list_items(" + BaseColumns._ID + " INTEGER PRIMARY KEY, list_id INTEGER, list_item_name TEXT);");
//            db.close();
//            dbh.firstRun();
//            prefs.edit().putBoolean("firstrun", false).commit();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Remove any previously created buttons so we can rebuild them if the user presses the back button
        LinearLayout previousLayout = (LinearLayout) findViewById(R.id.content_main);
        previousLayout.removeAllViews();
        // Query for lists
        dbResults = dbh.getLists();
        listsMap = new HashMap<>();
        listsList = new ArrayList<>();
        while (dbResults.moveToNext()) {
            listsMap.put(dbResults.getString(0), dbResults.getString(1));
            listsList.add(dbResults.getString(0));
        }
        Collections.sort(listsList);
        createListButtons();
    }

    public void goToAddList() {
        System.out.println("Going to add list...");
        Intent addListIntent = new Intent(this, AddList.class);
        startActivity(addListIntent);
    }

    public void goToViewList(String listId, String listName) {
        System.out.println("Going to view list...");
        Intent viewListIntent = new Intent(this, ViewList.class);
        Bundle bundle = new Bundle();
        bundle.putString("LIST_ID", listId);
        bundle.putString("LIST_NAME", listName);
        viewListIntent.putExtras(bundle);
        startActivity(viewListIntent);
    }

    public void createListButtons() {
        System.out.println("Creating list buttons.");

        LinearLayout layout = (LinearLayout) findViewById(R.id.content_main);
        layout.setOrientation(LinearLayout.VERTICAL);

        for (String listId : listsList) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
            Button listButton = new Button(this);
            listButton.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            listButton.setAllCaps(false);
            listButton.setText(StringUtils.capitalize(listsMap.get(listId)));
            listButton.setTag(R.string.LIST_ID_KEY, listId);
            listButton.setTag(R.string.LIST_NAME_KEY, listsMap.get(listId));
            listButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToViewList(v.getTag(R.string.LIST_ID_KEY).toString(), v.getTag(R.string.LIST_NAME_KEY).toString());
                }
            });
            row.addView(listButton);
            layout.addView(row);
            System.out.println("Created button for list " + listId + " (ID " + listsMap.get(listId) + ")");
        }
    }
}
