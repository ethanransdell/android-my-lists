package com.ethanransdell.mylists;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddListItem extends AppCompatActivity {
    private Intent incomingIntent;
    private Bundle incomingExtras;
    private String listId;
    private String listName;

    private EditText mEditTextNewListItemName;
    private Button mButtonAdd;
    private String newListItemNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        incomingIntent = getIntent();
        incomingExtras = incomingIntent.getExtras();
        listId = incomingExtras.getString("LIST_ID");
        listName = incomingExtras.getString("LIST_NAME");

        mEditTextNewListItemName = (EditText) findViewById(R.id.edit_text_new_list_item_name);
        mButtonAdd = (Button) findViewById(R.id.button_add);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItemNameIsValid()) {
                    newListItemNameString = mEditTextNewListItemName.getText().toString();
                    AddListItemTask addListItemTask = new AddListItemTask();
                    addListItemTask.execute();
//                    try {
//                        Thread.sleep(1000);
                        goToViewList(listId, listName);
//                    } catch (Exception e) {
//                    }
                } else {
                    mEditTextNewListItemName.setError("Enter a valid list name.");
                }
            }
        });
    }

    public boolean listItemNameIsValid() {
        if (mEditTextNewListItemName.getText().toString() != null && mEditTextNewListItemName.getText().toString() != "") {
            return true;
        } else {
            return false;
        }
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

    public class AddListItemTask extends AsyncTask<Void, Void, Void> {

        private SQLiteDatabase db = openOrCreateDatabase("my_lists", MODE_PRIVATE, null);

        protected Void doInBackground(Void... params) {
            ContentValues values = new ContentValues();
            values.put("list_id", listId);
            values.put("list_item_name", newListItemNameString);
            Long newListItemRowId = db.insert("list_items", null, values);
            System.out.println("Added newListItemRowId " + newListItemRowId.toString());
            db.close();
            return null;
        }
    }
}
