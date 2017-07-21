package com.google.android.gms.samples.vision.barcodereader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class ModifyInformation extends AppCompatActivity {

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_information);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back_arrow_grey);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final TextView displayName = (TextView) findViewById(R.id.displayName);
        final TextView displayCompany = (TextView) findViewById(R.id.displayCompany);
        final TextView displayPosition = (TextView) findViewById(R.id.displayPosition);
        final TextView displayPhone = (TextView) findViewById(R.id.displayPhone);
        final EditText inputComment = (EditText) findViewById(R.id.inputComment);
        final TextView displayComment = (TextView) findViewById(R.id.displayComment);
        final TextView displayTime = (TextView) findViewById(R.id.displayTime);
        final Button deleteBtn = (Button) findViewById(R.id.delete);
        final Button updateBtn = (Button) findViewById(R.id.update);

        dbManager = new DBManager(this);
        dbManager.open();

        final Intent intent = getIntent();
        final String json = intent.getStringExtra("QRCode");
        final String meta = intent.getStringExtra("Meta");
        final Long id = Long.parseLong(intent.getStringExtra("id"));

        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, Object>>(){}.getType();

        try {
            Map<String, Object> myMap = gson.fromJson(json, type);
            MapWrapper mw = new MapWrapper(myMap);
            displayName.setText(mw.getName());
            displayCompany.setText(mw.getCompany());
            displayPosition.setText(mw.getPosition());
            displayPhone.setText(mw.getPhone());
            //displayName.setText(((ArrayList)myMap.get("array")).get(2).toString());
        }
        catch (Exception e){
            Log.e("TAG", e.toString());
            Toast.makeText(this, R.string.invalid_json, Toast.LENGTH_LONG).show();
        }

        try {
            Map<String, Object> myMap = gson.fromJson(meta, type);
            displayComment.setText(myMap.get("comment").toString());
            inputComment.setText(myMap.get("comment").toString());
            displayTime.setText(myMap.get("date").toString());
            //TODO: Add Author
        } catch (Exception e) {
            Log.e("TAG", e.toString());
            Toast.makeText(this, R.string.invalid_meta_json, Toast.LENGTH_LONG).show();
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    displayComment.setText(inputComment.getText());
                    dbManager.update(id, json, getMetaJSON(displayComment.getText().toString(), displayTime.getText().toString()));
                    Intent i = new Intent(ModifyInformation.this, ListCustomers.class);
                    startActivity(i);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(id); //confirm deletion
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan: {
                Intent i = new Intent(this, BarcodeCaptureActivity.class);
                startActivity(i);
                break;
            }
            case R.id.action_list: {
                Intent i = new Intent(this, ListCustomers.class);
                startActivity(i);
                break;
            }
            case R.id.action_export: {
                Intent i = new Intent(this, ExportToEmail.class);
                startActivity(i);
                break;
            }
            case R.id.action_info: {
                Intent i = new Intent(this, ListCustomers.class);
                startActivity(i);
                break;
            }
        }
        return false;
    }

    private String getMetaJSON(String comment, String time){
        return "{\"comment\":\"" + comment + "\", \"date\":\"" + time  + "\", \"author\":\"admin\"}";
    }

    private void showDialog(final Long id){
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_person)
                .setMessage(getString(R.string.remove_question_1of2) + getName() + getString(R.string.remove_question_2of2))
                .setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dbManager.delete(id);
                        Intent i = new Intent(ModifyInformation.this, ListCustomers.class);
                        startActivity(i);
                    }})
                .setNegativeButton(R.string.delete_negative, null).show();
    }

    private String getName(){
        TextView nameView = (TextView) findViewById(R.id.displayName);
        return nameView.getText().toString();
    }

}

//{"name":"John Doe", "company":"ABC", "position":"Unknown", "phone":"555-555-555"}
