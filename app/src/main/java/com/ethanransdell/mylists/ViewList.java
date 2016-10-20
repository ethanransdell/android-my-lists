package com.ethanransdell.mylists;

import android.app.ActionBar;
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

import java.util.HashMap;
import java.util.Map;

public class ViewList extends AppCompatActivity {

    private Intent incomingIntent;
    private Bundle incomingExtras;
    private String listId;
    private String listName;
    private ImageButton mButtonDelete;

    private Map<String, String> listItemsMap;

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

            }
        });

        incomingIntent = getIntent();
        incomingExtras = incomingIntent.getExtras();
        listId = incomingExtras.getString("LIST_ID");
        listName = incomingExtras.getString("LIST_NAME");

        System.out.println("************************My list ID is " + listId);
        toolbar.setTitle(listName);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Remove any previously created buttons so we can rebuild them if the user presses the back button
        LinearLayout previousLayout = (LinearLayout) findViewById(R.id.content_view_list);
        previousLayout.removeAllViews();

        GetListItemsTask getListItems = new GetListItemsTask();
        getListItems.execute();

        try {
            Thread.sleep(1000);
            createListItemButtons();
        } catch (Exception e) {
        }
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

        for (String listItemId : listItemsMap.keySet()) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
            Button listButton = new Button(this);
            listButton.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            listButton.setText(listItemsMap.get(listItemId));
            listButton.setTag(R.string.LIST_ITEM_ID_KEY, listItemId);
            listButton.setTag(R.string.LIST_ITEM_NAME_KEY, listItemsMap.get(listItemId));
            listButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListItem(v.getTag(R.string.LIST_ITEM_ID_KEY).toString());
                }
            });
            row.addView(listButton);
            layout.addView(row);
            System.out.println("Created button for list item " + listItemId + ": " + listItemsMap.get(listItemId));
        }
    }

    public void deleteListItem(String listItemId) {
        DeleteListItemTask deleteListItemTask = new DeleteListItemTask(listItemId);
        deleteListItemTask.execute();
        try {
            Thread.sleep(1000);
            goToViewList();
        } catch (Exception e) {
        }
    }

    public void deleteList() {
        DeleteListTask deleteListTask = new DeleteListTask();
        deleteListTask.execute();
        try {
            Thread.sleep(1000);
            goToMainActivity();
        } catch (Exception e) {
        }
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

    public class DeleteListTask extends AsyncTask<Void, Void, Void> {
        private SQLiteDatabase db = openOrCreateDatabase("my_lists", MODE_PRIVATE, null);
        private String listItemId;

        DeleteListTask() {
        }

        protected Void doInBackground(Void... params) {
            db.execSQL("DELETE FROM lists WHERE _id=" + Integer.parseInt(listId) + ";");
            db.close();
            System.out.println("Deleted list " + listId + ".");
            return null;
        }
    }

    public class DeleteListItemTask extends AsyncTask<Void, Void, Void> {
        private SQLiteDatabase db = openOrCreateDatabase("my_lists", MODE_PRIVATE, null);
        private String listItemId;

        DeleteListItemTask(String listItemId) {
            this.listItemId = listItemId;
        }

        protected Void doInBackground(Void... params) {
            db.execSQL("DELETE FROM list_items WHERE _id=" + Integer.parseInt(listItemId) + ";");
            db.close();
            System.out.println("Deleted list item " + listItemId + ".");
            return null;
        }
    }

    public class GetListItemsTask extends AsyncTask<Void, Void, Void> {
        private SQLiteDatabase db = openOrCreateDatabase("my_lists", MODE_PRIVATE, null);
        private Cursor listItemsCursor;

        GetListItemsTask() {
        }

        protected Void doInBackground(Void... params) {
            listItemsCursor = db.rawQuery("SELECT * FROM list_items WHERE list_id=" + listId + ";", null);
            System.out.println("Selected " + listItemsCursor.getCount() + " list items.");
            listItemsMap = new HashMap<>();
            while (listItemsCursor.moveToNext()) {
//                if (listItemsCursor.getString(1) == listId) {
                    listItemsMap.put(listItemsCursor.getString(0), listItemsCursor.getString(2));
//                }
            }
            System.out.println("listItemsMap has " + listItemsMap.keySet().size() + " keys.");
            db.close();
            return null;
        }
    }
}
