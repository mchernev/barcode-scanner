/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;
    private EditText setAuthor;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final int MODIFY_INFO = 9002;
    private static final String TAG = "BarcodeMain";
    private static final String PREFS_NAME = "Config";

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back_arrow_grey);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        statusMessage = (TextView)findViewById(R.id.status_message);
        barcodeValue = (TextView)findViewById(R.id.barcode_value);
        setAuthor = (EditText) findViewById(R.id.set_author);

//        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
//        useFlash = (CompoundButton) findViewById(R.id.use_flash);

    //        useFlash.setChecked(false);
    //        autoFocus.setChecked(true);
//        autoFocus.setVisibility(View.INVISIBLE);
//        useFlash.setVisibility(View.INVISIBLE);

//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isFirstUsage = settings.getBoolean("first_usage", true);

        new DownloadJSON().execute();//asynctask test

        if (isFirstUsage) {
            //initial config
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putBoolean("first_usage", false);
//            editor.apply();
        }
        else {
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            startActivity(intent);
        }

//        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
//        startActivity(intent);
        //intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
        //intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());
        //startActivityForResult(intent, RC_BARCODE_CAPTURE);

//        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.hall_mode).setOnClickListener(this);
        findViewById(R.id.company_mode).setOnClickListener(this);

//        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
//        toneG.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 200);//accept
//        toneG.startTone(ToneGenerator.TONE_SUP_RADIO_NOTAVAIL, 600);//reject


    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.read_barcode) {
//            // launch barcode activity.
//            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
//            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
//            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());
//
//            startActivityForResult(intent, RC_BARCODE_CAPTURE);
//        }
        if (v.getId() == R.id.hall_mode) {
            if (setAuthor.getText().length() >= 3) {
                findViewById(R.id.name_error).setVisibility(View.GONE);

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("mode", "hall");
                editor.putString("name", setAuthor.getText().toString());
                editor.apply();

                Intent intent = new Intent(this, SelectHall.class);
                startActivity(intent);
            }
            else {
                findViewById(R.id.name_error).setVisibility(View.VISIBLE);
            }
        }
        else if (v.getId() == R.id.company_mode) {
            if (setAuthor.getText().length() >= 3) {
                findViewById(R.id.name_error).setVisibility(View.GONE);

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("mode", "company");
                editor.putString("name", setAuthor.getText().toString());
                editor.apply();

                Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                startActivity(intent);

                Toast.makeText(this, "Hello " + setAuthor.getText().toString(), Toast.LENGTH_SHORT).show();
            }
            else {
                findViewById(R.id.name_error).setVisibility(View.VISIBLE);
            }
        }
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
                String s = settings.getString("mode", "");
                if(!s.equals("")) {
                    Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(this, "You have to pick a mode first", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "No Mode Picked");
                }
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
}
