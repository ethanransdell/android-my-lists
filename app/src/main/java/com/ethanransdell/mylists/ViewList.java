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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ViewList extends AppCompatActivity {

    private DBHelper dbh = new DBHelper(this);

    private Intent incomingIntent;
    private Bundle incomingExtras;
    private String listId;
    private String listName;
    private ImageButton mButtonDeleteList;
    private Map<String, String> itemsMap;
    private Cursor items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton floating_add_item = (FloatingActionButton) findViewById(R.id.floating_add_item);
        floating_add_item.setOnClickListener(new View.OnClickListener() {
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

        mButtonDeleteList = (ImageButton) findViewById(R.id.button_delete_list);
        mButtonDeleteList.setOnClickListener(new View.OnClickListener() {
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
        System.out.println("Creating item buttons.");
        // Remove any previously created buttons so we can rebuild them if the user presses the back button
        LinearLayout previousLayout = (LinearLayout) findViewById(R.id.content_view_list);
        previousLayout.removeAllViews();
        // Query for items
        items = dbh.getItems(listId);
        itemsMap = new LinkedHashMap<>();
        while (items.moveToNext()) {
            itemsMap.put(items.getString(0), items.getString(2));
        }
        LinearLayout layout = (LinearLayout) findViewById(R.id.content_view_list);
        layout.setOrientation(LinearLayout.VERTICAL);
        for (String itemId : itemsMap.keySet()) {
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
//                    deleteItem(v.getTag(R.string.ITEM_ID_KEY).toString(), v);
                    alterItem(v.getTag(R.string.ITEM_ID_KEY).toString(), v);
                }
            });
            row.addView(itemButton);
            layout.addView(row);
            System.out.println("Added button for itemId " + itemId);
        }
        dbh.printItems(listId);
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

    public void alterItem(final String itemId, final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_alter_item)
                .setItems(R.array.options_alter_item, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boolean result = false;
                        switch (which) {
                            case 0: // Move up
                                result = dbh.moveItem(listId, itemId, true);
                                break;
                            case 1: // Move down
                                result = dbh.moveItem(listId, itemId, false);
                                break;
                            case 2: // Delete
                                result = true;
                                deleteItem(itemId, v);
                                break;
                            case 3: // Cancel
                                result = true;
                                break;
                        }
                        if (result) {
                            createItemButtons();
                        } else {
                            alterItemFailed();
                        }
                    }
                });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    public void alterItemFailed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alter_item_failed)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void deleteItem(final String itemId, final View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_delete_item)
                .setMessage(R.string.delete_item_confirmation)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbh.deleteItem(itemId);
                        dbh.sortItems(listId);
                        createItemButtons();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
