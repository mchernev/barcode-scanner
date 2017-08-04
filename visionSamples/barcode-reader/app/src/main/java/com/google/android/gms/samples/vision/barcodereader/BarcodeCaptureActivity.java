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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.samples.vision.barcodereader.ui.camera.CameraSource;
import com.google.android.gms.samples.vision.barcodereader.ui.camera.CameraSourcePreview;

import com.google.android.gms.samples.vision.barcodereader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class BarcodeCaptureActivity extends AppCompatActivity {
    private static final String TAG = "Barcode-reader";

    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    public static BarcodeGraphic mGraphic = null;
    private Barcode currentBarcode = null;
    private String currentTime = null;

    private final Handler h = new Handler();
    private final int delay = 100; //milliseconds
    private static Runnable r;

    private DBManager dbManager;

    private static final String PREFS_NAME = "Config";
    private SharedPreferences settings;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.barcode_capture);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back_arrow_grey);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);

        dbManager = new DBManager(this);
        dbManager.open();

        // read parameters from the intent used to launch the activity.
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, true);
        boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

//        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        final Button discardBtn = (Button) findViewById(R.id.btnDiscardOverlay);
        discardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardPerson();
            }
        });

        final Button editBtn = (Button) findViewById(R.id.btnEditOverlay);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPerson();
            }
        });

        r = new Runnable(){
            public void run(){
                //Log.d("Run", "Am In Loop");
                if(mGraphic != null) {
                    BarcodeGraphic bg = mGraphic;
                    if(currentBarcode == null || !bg.getBarcode().displayValue.equals(currentBarcode.displayValue)) {
                        currentBarcode = bg.getBarcode();
                        foundBarcode(bg.getBarcode());
                        //Log.d("JSON", bg.getBarcode().displayValue);
                    }
                }
                h.postDelayed(this, delay);
            }
        };

        settings = getSharedPreferences(PREFS_NAME, 0);

//        barcodeListener();

//        Snackbar.make(mGraphicOverlay, "Tap to capture. Pinch/Stretch to zoom",
//                Snackbar.LENGTH_LONG)
//                .show();
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        findViewById(R.id.topLayout).setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void barcodeListener(){
        h.postDelayed(r, delay);
    }

    public void foundBarcode(Barcode bc) {
        h.removeCallbacks(r);

        switch (settings.getString("mode", "default")) {
            case "company":
                companyModeOverlay(bc);
                break;
            case "hall":
                hallModeOverlay(bc);
                break;
            default:
                Log.e("Mode Error", "Unknown mode");
                break;
        }
//        companyModeOverlay(bc);
    }

    private MapWrapper extractJsonToMap(Barcode bc){
        String json = bc.displayValue;
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        try {
            Map<String, Object> myMap = gson.fromJson(json, type);
            MapWrapper mw = new MapWrapper(myMap);
            return mw;
        }
        catch (Exception e){
            Log.e("JSON Parse Error", e.toString());
            return null;
        }
    }

    //Displays overlay when qr code is scanned and adds person to db
    private void companyModeOverlay(Barcode bc){
        MapWrapper mw = extractJsonToMap(bc);

        String name = null;
        String company = null;
        String id = null;

        TextView nameView = (TextView) findViewById(R.id.nameOverlay);
        TextView companyView = (TextView) findViewById(R.id.companyOverlay);
        TextView idView = (TextView) findViewById(R.id.idOverlay);
        Button discardView = (Button) findViewById(R.id.btnDiscardOverlay);
        Button editView = (Button) findViewById(R.id.btnEditOverlay);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.infoOverlay);

        try {
            name = mw.get("name");
            company = mw.get("company");
            id = mw.get("id");
            nameView.setText(name);
            companyView.setText(company);
            idView.setText(id);
            nameView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            companyView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            discardView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            editView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            if(!getIdFromDb(mw.get("id"), dbManager.fetch()).equals("")){//person already in db
                rl.setBackgroundColor(Color.parseColor("#ff1111"));
            }
            else{
                rl.setBackgroundColor(Color.parseColor("#666666"));
                addPerson(bc);
            }
            barcodeListener();
        }
        catch(Exception e){
            Log.e("Barcode Scan", e.toString());
            Snackbar.make(mGraphicOverlay, R.string.invalid_json,
                Snackbar.LENGTH_LONG)
                .show();
        }
    }

    private void addPerson(Barcode bc){
        currentTime = getTime();
        dbManager.insert(bc.displayValue, getMetaJSON("", currentTime, settings.getString("name", "Unknown")));
        Snackbar.make(mGraphicOverlay, getNameOverlay() + getString(R.string.added_to_list_message),
                Snackbar.LENGTH_LONG)
                .show();
    }

    //discard deletes an already added person if clicked
    //maybe check if duplicate before deleting
    public void discardPerson(){
        //TextView idView = (TextView) findViewById(R.id.idOverlay);
        String id = getIdFromDb(getIdOverlay(), dbManager.fetch());
        dbManager.delete(Long.parseLong(id));

        hideInfoOverlay();
    }

    private void editPerson(){
        //Edit button sends to ModifyActivity
        Intent i = new Intent(BarcodeCaptureActivity.this, ModifyInformation.class);
        i.putExtra("QRCode", currentBarcode.displayValue);
        i.putExtra("Meta", getMetaJSON("", currentTime, settings.getString("name", "Unknown")));
        String id = getIdFromDb(getIdOverlay(), dbManager.fetch());
        i.putExtra("id", id);
        startActivity(i);
    }

    private void hideInfoOverlay(){
        TextView nameView = (TextView) findViewById(R.id.nameOverlay);
        TextView companyView = (TextView) findViewById(R.id.companyOverlay);
        Button discardView = (Button) findViewById(R.id.btnDiscardOverlay);
        Button editView = (Button) findViewById(R.id.btnEditOverlay);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.infoOverlay);
        rl.setBackgroundColor(Color.parseColor("#666666"));

        nameView.setHeight(0);
        discardView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        discardView.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        editView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        editView.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        companyView.setHeight(0);
    }

    //returns the DB ID of the scanned person
    private String getIdFromDb(String id, Cursor cursor){//cursor = dbManager.fetch()
        if(cursor.getCount()!=0){
            if(cursor.moveToFirst()){
                String match;
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                do{
                    Map<String, Object> myMap = gson.fromJson(cursor.getString(cursor.getColumnIndex("customer")), type);
                    match = myMap.get("id").toString();
                    if(match.equals(id))
                        return cursor.getString(cursor.getColumnIndex("_id"));
                }while(cursor.moveToNext());
            }
        }
        return "";
    }

    private String getMetaJSON(String comment, String time, String author){
        return "{\"comment\":\"" + comment + "\", \"date\":\"" + time + "\", \"author\":\"" + author + "\"}";
    }

    private String getTime(){
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        df.setTimeZone(TimeZone.getDefault());
        return df.format(date);
    }

    private String getNameOverlay(){
        TextView nameView = (TextView) findViewById(R.id.nameOverlay);
        return nameView.getText().toString();
    }

    private String getIdOverlay(){
        TextView idView = (TextView) findViewById(R.id.idOverlay);
        return idView.getText().toString();
    }

    private void hallModeOverlay(Barcode bc){
        Intent intent = getIntent();
        String hall = intent.getStringExtra("hall");
        Boolean tag = true;
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.infoOverlay);
//        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        //Code checking tags

        rl.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
        rl.requestLayout();
        if(tag){//tags match
            rl.setBackgroundColor(Color.parseColor("#76ee00"));
//            MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.accept);
//            mPlayer.start();
//            toneG.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 200);//accept
            addPerson(bc);
        }
        else{
            rl.setBackgroundColor(Color.parseColor("#ff1111"));
//            MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.fail);
//            mPlayer.start();
//            toneG.startTone(ToneGenerator.TONE_SUP_RADIO_NOTAVAIL, 600);//reject
//            toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 600);//reject
        }
        //turn off the color overlay after 2.5 seconds
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                rl.requestLayout();
            }
        }, 2500);

        //TODO: Check person tags against hall tags and react.
        //TODO: Get hall tags from file. Get person tags from Barcode parameter.
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector
                .Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        //BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        /*barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                foundBarcode(detections.getDetectedItems().get(0));
            }
        });
        //new MultiProcessor.Builder<>(barcodeFactory).build());
        */
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        /*
         ** Every 100 milliseconds checks if BarcodeTrackerFactory has returned a BarcodeGraphic
         ** through a global static variable. If it has, then MainActivity receives the
         ** BarcodeGraphic and resets the global static variable
        */

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
        mGraphic = null;
        barcodeListener();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
        mGraphic = null;
        h.removeCallbacks(r);
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
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
                //Intent i = new Intent(this, BarcodeCaptureActivity.class);
                //startActivity(i);
                //Already here
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
            case R.id.action_info: {//change to info page. Use as list because of broken screen
//                Intent i = new Intent(this, ListCustomers.class);
//                startActivity(i);
                Intent i = new Intent(this, ExportToEmail.class);
                startActivity(i);
                break;
            }
        }
        return false;
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }
}
