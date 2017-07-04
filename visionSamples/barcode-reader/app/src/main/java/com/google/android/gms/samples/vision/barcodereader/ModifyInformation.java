package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class ModifyInformation extends Activity {

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_information);

        final TextView displayName = (TextView) findViewById(R.id.displayName);
        TextView displayCompany = (TextView) findViewById(R.id.displayCompany);
        TextView displayPosition = (TextView) findViewById(R.id.displayPosition);
        TextView displayPhone = (TextView) findViewById(R.id.displayPhone);
        final EditText inputComment = (EditText) findViewById(R.id.inputComment);
        final TextView displayComment = (TextView) findViewById(R.id.displayComment);
        TextView displayTime = (TextView) findViewById(R.id.displayTime);
        final Button addComment = (Button) findViewById(R.id.addComment);
        final Button discard = (Button) findViewById(R.id.discard);
        final Button save = (Button) findViewById(R.id.save);

        dbManager = new DBManager(this);
        dbManager.open();

        Intent intent = getIntent();
        final String json = intent.getStringExtra("QRCode");

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


        Date date = new Date();
        DateFormat df = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        df.setTimeZone(TimeZone.getDefault());
        displayTime.setText(df.format(date));

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayComment.setText(inputComment.getText());
                addComment.setVisibility(View.INVISIBLE);
                discard.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                inputComment.setVisibility(View.INVISIBLE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = dbManager.fetch();
                if(!isInDb(displayName.getText().toString(), c))
                    dbManager.insert(json, getMetaJSON());
                else
                    Toast.makeText(ModifyInformation.this, "Person already scanned", Toast.LENGTH_LONG).show();
                Intent i = new Intent(ModifyInformation.this, ListCustomers.class);
                startActivity(i);
            }
        });
    }

    private String getMetaJSON(){
        return "{\"comment\":\"placeholder\", \"date\":\"placeholder\", \"author\":\"admin\"}";
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

//{"name":"Han Solo", "company":"Rebels", "position":"Pilot", "phone":"555-555-555",}