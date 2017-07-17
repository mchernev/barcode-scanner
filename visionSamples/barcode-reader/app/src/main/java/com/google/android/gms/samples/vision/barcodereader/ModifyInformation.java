package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.text.Text;
import com.google.gson.Gson;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

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
        TextView displayCompany = (TextView) findViewById(R.id.displayCompany);
        TextView displayPosition = (TextView) findViewById(R.id.displayPosition);
        TextView displayPhone = (TextView) findViewById(R.id.displayPhone);
        final EditText inputComment = (EditText) findViewById(R.id.inputComment);
        final TextView displayComment = (TextView) findViewById(R.id.displayComment);
        final TextView displayTime = (TextView) findViewById(R.id.displayTime);
        final Button addComment = (Button) findViewById(R.id.addComment);
        final Button discard = (Button) findViewById(R.id.discard);
        final Button save = (Button) findViewById(R.id.save);

        dbManager = new DBManager(this);
        dbManager.open();

        final Intent intent = getIntent();
        final String json = intent.getStringExtra("QRCode");
        final String meta = intent.getStringExtra("Meta");

        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, Object>>(){}.getType();

        try {
            Map<String, Object> myMap = gson.fromJson(json, type);
            MapWrapper mw = new MapWrapper(myMap);
            displayName.setText(mw.getName());
            displayCompany.setText(mw.getCompany());
            displayPosition.setText(mw.getPosition());
            displayPhone.setText(mw.getPhone());
            //displayName.setText(myMap.get("name").toString());
            //displayCompany.setText(myMap.get("company").toString());
            //displayPosition.setText(myMap.get("position").toString());
            //displayPhone.setText(myMap.get("phone").toString());
            //displayName.setText(((ArrayList)myMap.get("array")).get(2).toString());
        }
        catch (Exception e){
            Intent data = new Intent();
            data.putExtra("Display Error Message", R.string.invalid_json);
            data.putExtra("Display Exception", e.toString());
            setResult(CommonStatusCodes.ERROR, data);
            finish();
        }

        if(meta != null){
            try {
                Map<String, Object> myMap = gson.fromJson(meta, type);
                displayComment.setText(myMap.get("comment").toString());
                displayTime.setText(myMap.get("date").toString());
                //TODO: Add Author
            }
            catch (Exception e){
                Intent data = new Intent();
                data.putExtra("Display Error Message", R.string.invalid_json);
                data.putExtra("Display Exception", e.toString());
                setResult(CommonStatusCodes.ERROR, data);
                finish();
            }
        }
        else{
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
            df.setTimeZone(TimeZone.getDefault());
            displayTime.setText(df.format(date));
            //TODO: Add Author
        }
        //if meta != null => I am modifying
        //if meta == null => It is new scan

        if(meta != null){
            addComment.setText("Modify Comment");
            inputComment.setText(displayComment.getText().toString());
            discard.setText("Delete");
            discard.setVisibility(View.VISIBLE);
            save.setText("Update");
            save.setVisibility(View.VISIBLE);
        }

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayComment.setText(inputComment.getText());
                addComment.setVisibility(View.INVISIBLE);//add comment button disappears
                if(meta == null) {
                    discard.setText("Discard Person");
                    save.setText("Add Person");
                }
//                else{
//                    discard.setText("Delete");
//                    save.setText("Update");
//                }
                discard.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                inputComment.setVisibility(View.INVISIBLE);//edit text field disappears
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(meta == null) {
                    Cursor c = dbManager.fetch();
                    if (!isInDb(displayName.getText().toString(), c))
                        dbManager.insert(json, getMetaJSON(displayComment.getText().toString(), displayTime.getText().toString()));
                    else
                        Toast.makeText(ModifyInformation.this, "Person already scanned", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(ModifyInformation.this, ListCustomers.class);
                    startActivity(i);
                }
                else{
                    //TODO: add update code (look at sql contact app)
                    dbManager.update(Long.parseLong(intent.getStringExtra("id")), json, getMetaJSON(displayComment.getText().toString(), displayTime.getText().toString()));
                    Intent i = new Intent(ModifyInformation.this, ListCustomers.class);
                    startActivity(i);
                }
            }
        });

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(meta == null){
                    //TODO: Write discard code
                    //Returns to MainActivity which restarts scan
                    Intent i = new Intent(ModifyInformation.this, MainActivity.class);
                    startActivity(i);
                }
                else{
                    if (intent.getStringExtra("id") != null) {
                        dbManager.delete(Long.parseLong(intent.getStringExtra("id")));
                        Intent i = new Intent(ModifyInformation.this, ListCustomers.class);
                        startActivity(i);
                    }
                }
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
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            }
            case R.id.action_list: {
                // do something
                break;
            }
            case R.id.action_export: {
                // do something
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

    private boolean isInDb(String name, Cursor cursor){
        if(cursor.getCount()!=0){
            if(cursor.moveToFirst()){
                String match;
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                do{
                    Map<String, Object> myMap = gson.fromJson(cursor.getString(cursor.getColumnIndex("customer")), type);
                    match = myMap.get("name").toString();
                    if(match.equals(name))
                        return true;
                }while(cursor.moveToNext());
            }
        }
        return false;
    }

}

//{"name":"Han Solo", "company":"Rebels", "position":"Pilot", "phone":"555-555-555"}