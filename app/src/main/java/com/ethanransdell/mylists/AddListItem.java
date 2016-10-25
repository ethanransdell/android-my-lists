package com.ethanransdell.mylists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AddListItem extends AppCompatActivity {
    private Intent incomingIntent;
    private Bundle incomingExtras;
    private String listId;
    private String listName;

    private EditText mEditTextNewListItemName;
    private Button mButtonAdd;

    private DBHelper dbh = new DBHelper(this);

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

        mEditTextNewListItemName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTextNewListItemName, InputMethodManager.SHOW_IMPLICIT);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItemNameIsValid()) {
                    dbh.addListItem(listId, mEditTextNewListItemName.getText().toString());
                    goToViewList(listId, listName);
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

}
