package com.momchil.emailsender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

public class MainActivity extends Activity {

    private static final int READ_REQUEST_CODE = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText emailBody = (EditText) findViewById(R.id.emailBody);
        final Button sendEmail = (Button) findViewById(R.id.sendEmail);

        // StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        // StrictMode.setVmPolicy(builder.build());

        // File f = getExternalFilePath("myfile");
//        if(!f.exists()) {
//            try {
//                f.createNewFile();
//            } catch (IOException e) {
//                Log.e("TAG", "A", e);
//            }
//        }
//        try {
//            FileOutputStream ff = new FileOutputStream(f);
//            ff.write("Hello nurse!".getBytes());
//            ff.close();
//        } catch (IOException e) {
//            Log.e("TAG", "wow", e);
//        }

        // We assume the file we want to load is in the documents/ subdirectory
        // of the internal storage
//        File documentsPath = new File(getFilesDir(), "export");
//        File file = new File(documentsPath, "myfile");
//
//        try {
//            file.createNewFile();
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write("bla\n".getBytes());
//            fos.close();
//        } catch (IOException e) {
//            Log.e("TAG", "wtf", e);
//            return;
//        }
//
//        // This can also in one line of course:
//        // File file = new File(Context.getFilesDir(), "documents/sample.pdf");
//
//        Uri uri = FileProvider.getUriForFile(this, "com.momchil.emailsender.fileprovider", file);
//
//        Intent intent = ShareCompat.IntentBuilder.from(this)
//                .setType("text/csv")
//                .setStream(uri)
//                .setChooserTitle("Choose bar")
//                .createChooserIntent()
//                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        startActivity(intent);


        //TODO: Come here
        //sendFileWithExternal("", "", "Some text");
        //sendFileWithProvider();


//        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "exp");
//        if (!path.mkdirs()) {
//            Log.e("TAG", "Could not make dirs");
//           // return;
//        }
//        File f = new File(path, "testfile");
//
//        try {
//            f.createNewFile();
//            FileOutputStream foss = new FileOutputStream(f);
//            foss.write("blabla\n".getBytes());
//            foss.close();
//        } catch (IOException e) {
//            Log.e("TAG", "wtf", e);
//            return;
//        }
//        //Uri u = FileProvider.getUriForFile(this, "com.momchil.emailsender.fileprovider", f);
//        Uri u = Uri.fromFile(f);
//        send("sda", u);


//        byte[] b = new byte[256];
//        int bytesRead;
//        File f = new File(this.getFilesDir(), "myfile");
//        try {
//            FileInputStream fis = new FileInputStream(f);
//            bytesRead = fis.read(b);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < bytesRead; ++i) {
//            sb.append((int) b[i]);
//            sb.append(' ');
//        }
//
//        Log.i("TAG", sb.toString());
//
//        String ss = new String(b, 0, bytesRead, StandardCharsets.US_ASCII);
//        //String s = new String(b, bytesRead);
//        emailBody.setText(ss);


        //String filename="contacts_sid.vcf";

        //File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);

        //Uri path = Uri.fromFile(filelocation);

        //performFileSearch();


//        sendEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //send(emailBody.getText().toString(), path);
//                send(emailBody.getText().toString(), uri);
//                //emailBody.setText(uri.toString());
//            }
//        });


    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

//    public File getExternalFilePath(String filename) {
//        File file = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOWNLOADS), filename);
//        if (!file.getParentFile().mkdirs()) {
//            Log.e("TAG", "Directory not created");
//        }
//        return file;
//    }

//    public File getStorageDir(Context context, String dir) {
//        // Get the directory for the app's private pictures directory.
//        File file = new File(context.getExternalFilesDir(
//                Environment.DIRECTORY_DOCUMENTS), dir);
////        if (!file.mkdirs()) {
////            //Log.e(LOG_TAG, "Directory not created");
////        }
//        return file;
//    }


    private void send(String body, Uri path){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"mchernev95@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Android App Test");
        i.putExtra(Intent.EXTRA_TEXT   , body);
        i.putExtra(Intent.EXTRA_STREAM, path);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

//    public void performFileSearch() {
//
//        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
//        // browser.
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//
//        // Filter to only show results that can be "opened", such as a
//        // file (as opposed to a list of contacts or timezones)
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        // Filter to show only images, using the image MIME data type.
//        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
//        // To search for all documents available via installed storage providers,
//        // it would be "*/*".
//        intent.setType("*/*");
//
//        startActivityForResult(intent, READ_REQUEST_CODE);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                EditText txt = (EditText) findViewById(R.id.emailBody);
                txt.setText(uri.toString());
                //send("Some text", uri);
            }
        }
    }

    public void sendFileWithProvider(){
        File documentsPath = new File(getFilesDir(), "export");
        documentsPath.mkdirs();
        File file = new File(documentsPath, "myfile");

        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write("bla\n".getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("TAG", "wtf", e);
            return;
        }

        // This can also in one line of course:
        // File file = new File(Context.getFilesDir(), "documents/sample.pdf");

        Uri uri = FileProvider.getUriForFile(this, "com.momchil.emailsender.fileprovider", file);



        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("text/csv")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }

    public void sendFileWithExternal(String receiver, String subject, String body) {
        if (isExternalStorageWritable()) {
            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "exp");
            File f = new File(path, "testfile");

            try {
                f.createNewFile();
                FileOutputStream foss = new FileOutputStream(f);
                foss.write("blabla\n".getBytes());
                foss.close();
            } catch (IOException e) {
                Log.e("TAG", "wtf", e);
                return;
            }
            //Uri u = FileProvider.getUriForFile(this, "com.momchil.emailsender.fileprovider", f);
            Uri u = Uri.fromFile(f);
            send(body, u);
        }
        else {
            Log.e("TAG", "External Storage not writable");
        }
    }



}
