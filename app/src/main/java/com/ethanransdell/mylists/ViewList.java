package com.ethanransdell.mylists;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ViewList extends AppCompatActivity {

    private Intent incomingIntent;
    private Bundle incomingExtras;
    private String listId;
    private String listName;
    private ImageButton mButtonDelete;

    private Map<String, String> itemsMap;
    private List<String> itemsList;

    private Cursor dbResults;
    private DBHelper dbh = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton floating_add_list_item = (FloatingActionButton) findViewById(R.id.floating_add_item);
        floating_add_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddItem();
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
        toolbar.setTitle(StringUtils.capitalize(listName));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Remove any previously created buttons so we can rebuild them if the user presses the back button
        LinearLayout previousLayout = (LinearLayout) findViewById(R.id.content_view_list);
        previousLayout.removeAllViews();
        // Query for lists
        dbResults = dbh.getItems(listId);
        itemsMap = new HashMap<>();
        itemsList = new ArrayList<>();
        while (dbResults.moveToNext()) {
            itemsMap.put(dbResults.getString(0), dbResults.getString(2));
            itemsList.add(dbResults.getString(0));
        }
        Collections.sort(itemsList);
        createItemButtons();
    }

    public void goToMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    public void goToAddItem() {
        Intent addItemIntent = new Intent(this, AddItem.class);
        addItemIntent.putExtras(getListBundle());
        startActivity(addItemIntent);
    }

    public Bundle getListBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("LIST_ID", listId);
        bundle.putString("LIST_NAME", listName);
        return bundle;
    }

    public void createItemButtons() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.content_view_list);
        layout.setOrientation(LinearLayout.VERTICAL);
        for (String itemId : itemsList) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
            Button itemButton = new Button(this);
            itemButton.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            itemButton.setAllCaps(false);
            itemButton.setText(itemsMap.get(itemId));
            itemButton.setTag(R.string.ITEM_ID_KEY, itemId);
            itemButton.setTag(R.string.ITEM_NAME_KEY, itemsMap.get(itemId));
            itemButton.setId(Integer.parseInt(itemId));
            itemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(v.getTag(R.string.ITEM_ID_KEY).toString(), v);
                }
            });
            row.addView(itemButton);
            layout.addView(row);
        }
    }

    public void deleteItem(final String itemId, final View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_delete_item)
                .setMessage(R.string.delete_item_confirmation)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbh.deleteItem(itemId);
                        v.setVisibility(View.GONE);
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

    public void deleteList() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_delete_list)
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
}
