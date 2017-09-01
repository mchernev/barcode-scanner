package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class SelectHall extends AppCompatActivity {

    private ArrayList<String> arraySpinner;
    private final String TAG = "Select Hall";
    private final String HALL = "hall";//hall array from json

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_hall);

//        String json = "{\"hall\":[\"Hall 1\", \"Hall 2\", \"Hall 3\", \"Hall 4\", \"Hall 5\", \"Hall 6\"]}";
        String json = readFile();

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        try {
            Map<String, Object> myMap = gson.fromJson(json, type);
            arraySpinner = (ArrayList<String>) myMap.get(HALL);
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }

        final Spinner spinner = (Spinner) findViewById(R.id.spinner_select_hall);
        final Button selectHall = (Button) findViewById(R.id.btn_select_hall);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, arraySpinner);
        spinner.setAdapter(adapter);

        selectHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectHall.this, BarcodeCaptureActivity.class);
                intent.putExtra("hall", spinner.getSelectedItem().toString());
                startActivity(intent);
            }
        });
    }

    private String readFile(){
        File documentsPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "BarcodeReader");
        documentsPath = new File(documentsPath, "agenda");
        File file = new File(documentsPath, "agenda.txt");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (Exception e) {
            Log.e("Read Error", e.toString());
        }

        return text.toString();
    }

}
