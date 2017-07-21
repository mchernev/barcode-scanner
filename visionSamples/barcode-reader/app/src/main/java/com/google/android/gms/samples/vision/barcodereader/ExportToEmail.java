package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ExportToEmail extends AppCompatActivity {

    private static ArrayList<String> CSV_FIELDS = new ArrayList<String>();

    static {
        CSV_FIELDS.add("customer.name");
        CSV_FIELDS.add("customer.company");
        CSV_FIELDS.add("customer.position");
        CSV_FIELDS.add("customer.phone");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_to_email);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back_arrow_grey);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        DBManager dbManager = new DBManager(this);
        dbManager.open();


        //CreateCSV ccsv = new CreateCSV(CSV_FIELDS, dbManager.fetch());
        //sendFileWithProvider(ccsv.getCSV());

        //sendFileWithExternal();
        //sendFileWithProvider();
//        ArrayList<String> CSV_FIELDS = new ArrayList<String>();
//        CSV_FIELDS.add("customer.name");
//        CSV_FIELDS.add("customer.company");
//        CSV_FIELDS.add("customer.model.m");
//        CSV_FIELDS.add("customer.model.n.a");
//        CreateCSV ccsv = new CreateCSV(CSV_FIELDS, dbManager.fetch());
//        Log.v("TAG", ccsv.getCSV());
//        ArrayList<String> all = new ArrayList<String>();
//        all.add("customer");
//        all.add("model");
//        all.add("m");
//        Log.v("TAG", ccsv.getValue(CSV_FIELDS));

    }

    //    Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void send(Uri path) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        //i.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
        //i.putExtra(Intent.EXTRA_SUBJECT, "");
        //i.putExtra(Intent.EXTRA_TEXT   , body);
        i.putExtra(Intent.EXTRA_STREAM, path);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(findViewById(R.id.export_layout), "There are no email clients installed.",
                    Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public void sendFileWithProvider(String csv) {
        File documentsPath = new File(getFilesDir(), "export");
        documentsPath.mkdirs();
        File file = new File(documentsPath, "testfile.csv");

        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(csv.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("TAG", "wtf", e);
            return;
        }

        Uri uri = FileProvider.getUriForFile(this, "com.google.android.gms.samples.vision.barcodereader.fileprovider", file);

        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("text/csv")
                .setStream(uri)
                .setChooserTitle("Send mail...")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }

    public void sendFileWithExternal(String csv) {
        if (isExternalStorageWritable()) {
            File documentsPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "export");
            File file = new File(documentsPath, "testfile.csv");

            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(csv.getBytes());
                fos.close();
            } catch (IOException e) {
                Log.e("TAG", "wtf", e);
                return;
            }
            Uri uri = Uri.fromFile(file);
            send(uri);
        } else {
            Log.e("TAG", "External Storage not writable");
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
                //Intent i = new Intent(this, ExportToEmail.class);
                //startActivity(i);
                //Already here
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
