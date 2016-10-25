package com.ethanransdell.mylists;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AddList extends AppCompatActivity {

    private EditText mEditTextNewListName;
    private Button mButtonAdd;

    private DBHelper dbh = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        mEditTextNewListName = (EditText) findViewById(R.id.edit_text_new_list_item_name);
        mButtonAdd = (Button) findViewById(R.id.button_add);

        mEditTextNewListName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTextNewListName, InputMethodManager.SHOW_IMPLICIT);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listNameIsValid()) {
                    dbh.addList(mEditTextNewListName.getText().toString());
                    goToMainActivity();
                } else {
                    mEditTextNewListName.setError("Enter a valid list name.");
                }
            }
        });
    }

    public boolean listNameIsValid() {
        if (mEditTextNewListName.getText().toString() != null && mEditTextNewListName.getText().toString() != "") {
            return true;
        } else {
            return false;
        }
    }

    public void goToMainActivity() {
        System.out.println("Going to main activity...");
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}
