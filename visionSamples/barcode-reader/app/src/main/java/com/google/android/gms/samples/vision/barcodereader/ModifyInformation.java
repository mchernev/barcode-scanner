package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class ModifyInformation extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_information);

        TextView displayName = (TextView) findViewById(R.id.displayName);
        TextView displayCompany = (TextView) findViewById(R.id.displayCompany);
        TextView displayPosition = (TextView) findViewById(R.id.displayPosition);
        TextView displayPhone = (TextView) findViewById(R.id.displayPhone);

        Intent intent = getIntent();
        String json = intent.getStringExtra("QRCode");

        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        try {
            Map<String, Object> myMap = gson.fromJson(json, type);
            displayName.setText(myMap.get("name").toString());
            displayCompany.setText(myMap.get("company").toString());
            displayPosition.setText(myMap.get("position").toString());
            displayPhone.setText(myMap.get("phone").toString());
            displayName.setText(((ArrayList)myMap.get("array")).get(2).toString());
        }
        catch (Exception e){
            displayName.setText(R.string.invalid_json);
        }
        //TODO: Write class, which contains a Map and get set methods. Include bonus fields for
        //TODO: comments, time it was added, who added it

    }
}

//{"name":"Han Solo", "company":"Rebels", "position":"Pilot", "phone":"555-555-555",}