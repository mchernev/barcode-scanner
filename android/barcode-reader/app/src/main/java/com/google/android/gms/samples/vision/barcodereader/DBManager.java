package com.google.android.gms.samples.vision.barcodereader;

/**
 * Created by momchil on 04.07.17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class DBManager {
    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String customer, String meta) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.JSON_CUSTOMERS, customer);
        contentValue.put(DatabaseHelper.JSON_META, meta);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.JSON_CUSTOMERS, DatabaseHelper.JSON_META };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String customer, String meta) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.JSON_CUSTOMERS, customer);
        contentValues.put(DatabaseHelper.JSON_META, meta);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }


}
