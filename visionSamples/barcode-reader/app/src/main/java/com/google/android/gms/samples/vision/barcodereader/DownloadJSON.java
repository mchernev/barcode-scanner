package com.google.android.gms.samples.vision.barcodereader;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class DownloadJSON extends AsyncTask<URL, Void, Void> {

    protected Void doInBackground(URL... urls) {
        StringBuilder sb = new StringBuilder();
        //TODO: Get JSON from url and save it in StringBuilder
        sb.append("{\"hall\":[\"Hall 1\", \"Hall 2\", \"Hall 3\", \"Hall 4\", \"Hall 5\", \"Hall 6\"]}");

        String result = sb.toString();
        Log.d("Download Json", result);
        File documentsPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "BarcodeReader");
        documentsPath = new File(documentsPath, "agenda");
        File file = new File(documentsPath, "agenda.txt");

        try {
            documentsPath.mkdirs();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(result.getBytes());
            fos.close();
            Log.d("Async", "Ready");
        } catch (IOException e) {
            Log.e("TAG", "wtf", e);
        }
        return null;
    }
}
