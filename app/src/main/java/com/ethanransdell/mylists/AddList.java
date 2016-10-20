package com.ethanransdell.mylists;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddList extends AppCompatActivity {

    private EditText mEditTextNewListName;
    private Button mButtonAdd;
    private String newListNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        mEditTextNewListName = (EditText) findViewById(R.id.edit_text_new_list_item_name);
        mButtonAdd = (Button) findViewById(R.id.button_add);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listNameIsValid()) {
                    newListNameString = mEditTextNewListName.getText().toString();
                    AddListTask addListTask = new AddListTask();
                    addListTask.execute();
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

    public class AddListTask extends AsyncTask<Void, Void, Void> {

        private SQLiteDatabase db = openOrCreateDatabase("my_lists", MODE_PRIVATE, null);

        protected Void doInBackground(Void... params) {
            ContentValues values = new ContentValues();
            values.put("list_name", newListNameString);
            Long newListRowId = db.insert("lists", null, values);
            System.out.println("Added newListRowId " + newListRowId.toString());
            db.close();
            goToMainActivity();
            return null;
        }
    }
}
