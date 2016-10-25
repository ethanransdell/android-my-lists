package com.ethanransdell.mylists;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewList extends AppCompatActivity {

    private Intent incomingIntent;
    private Bundle incomingExtras;
    private String listId;
    private String listName;
    private ImageButton mButtonDelete;

    private Map<String, String> listItemsMap;
    private List<String> listItemsList;

    private Cursor dbResults;
    private DBHelper dbh = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton floating_add_list_item = (FloatingActionButton) findViewById(R.id.floating_add_list_item);
        floating_add_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddListItem();
            }
        });

        FloatingActionButton floating_main_activity = (FloatingActionButton) findViewById(R.id.floating_main_activity);
        floating_main_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });

        mButtonDelete = (ImageButton) findViewById(R.id.button_delete);
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteList();
            }
        });

        incomingIntent = getIntent();
        incomingExtras = incomingIntent.getExtras();
        listId = incomingExtras.getString("LIST_ID");
        listName = incomingExtras.getString("LIST_NAME");
        toolbar.setTitle(listName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Remove any previously created buttons so we can rebuild them if the user presses the back button
        LinearLayout previousLayout = (LinearLayout) findViewById(R.id.content_view_list);
        previousLayout.removeAllViews();
        // Query for lists
        dbResults = dbh.getListItems(listId);
        listItemsMap = new HashMap<>();
        listItemsMap = new HashMap<>();
        listItemsList = new ArrayList<>();
        while (dbResults.moveToNext()) {
            listItemsMap.put(dbResults.getString(0), dbResults.getString(2));
            listItemsList.add(dbResults.getString(0));
        }
        Collections.sort(listItemsList);
        createListItemButtons();
    }

    public void goToMainActivity() {
        System.out.println("Going to main activity...");
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    public void goToAddListItem() {
        System.out.println("Going to add list item...");
        Intent addListItemIntent = new Intent(this, AddListItem.class);
        Bundle bundle = new Bundle();
        bundle.putString("LIST_ID", listId);
        bundle.putString("LIST_NAME", listName);
        addListItemIntent.putExtras(bundle);
        startActivity(addListItemIntent);
    }

    public void createListItemButtons() {

        System.out.println("Creating list item buttons.");

        LinearLayout layout = (LinearLayout) findViewById(R.id.content_view_list);
        layout.setOrientation(LinearLayout.VERTICAL);
        for (String listItemId : listItemsList) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
            Button listButton = new Button(this);
            listButton.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            listButton.setText(listItemsMap.get(listItemId));
            listButton.setTag(R.string.LIST_ITEM_ID_KEY, listItemId);
            listButton.setTag(R.string.LIST_ITEM_NAME_KEY, listItemsMap.get(listItemId));
            listButton.setId(Integer.parseInt(listItemId));
            listButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbh.deleteListItem(v.getTag(R.string.LIST_ITEM_ID_KEY).toString());
                    v.setVisibility(View.GONE);
                }
            });
            row.addView(listButton);
            layout.addView(row);
            System.out.println("Created button for list item " + listItemId + ": " + listItemsMap.get(listItemId));
        }
    }

    public void deleteList() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_list_title)
                .setMessage(R.string.delete_list_confirmation)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbh.deleteList(listId);
                        goToMainActivity();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void goToViewList() {
        System.out.println("Going to view list...");
        Intent viewListIntent = new Intent(this, ViewList.class);
        Bundle bundle = new Bundle();
        bundle.putString("LIST_ID", listId);
        bundle.putString("LIST_NAME", listName);
        viewListIntent.putExtras(bundle);
        startActivity(viewListIntent);
    }
}
