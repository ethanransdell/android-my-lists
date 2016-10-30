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

public class AddItem extends AppCompatActivity {

    private DBHelper dbh = new DBHelper(this);
    private Intent incomingIntent;
    private Bundle incomingExtras;
    private String listId;
    private String listName;
    private EditText mEditTextNewItemName;
    private Button mButtonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        incomingIntent = getIntent();
        incomingExtras = incomingIntent.getExtras();
        listId = incomingExtras.getString("LIST_ID");
        listName = incomingExtras.getString("LIST_NAME");

        mEditTextNewItemName = (EditText) findViewById(R.id.edit_text_new_item_name);
        mButtonAdd = (Button) findViewById(R.id.button_add);

        //FIXME Not working
        mEditTextNewItemName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTextNewItemName, InputMethodManager.SHOW_IMPLICIT);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemNameIsValid()) {
                    dbh.addItem(listId, mEditTextNewItemName.getText().toString());
                    goToViewList(listId, listName);
                } else {
                    mEditTextNewItemName.setError(getString(R.string.new_item_validation));
                }
            }
        });
    }

    public boolean itemNameIsValid() {
        if (mEditTextNewItemName.getText().toString() != null || mEditTextNewItemName.getText().toString() != "") {
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
