package com.google.android.gms.samples.vision.barcodereader;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by momchil on 21.07.17.
 */

public class CreateCSV {
    private Cursor cursor;
    private ArrayList<String> info;

    public CreateCSV(ArrayList<String> al, Cursor c) {
        cursor = c;
        info = al;
    }

    private ArrayList<String> chopString(String s) {
        ArrayList<String> res = new ArrayList<String>();
        int dotIndex;
        if (!s.contains(".")) {
            res.add(s);
            return res;
        }
        for (int i = 0; i < s.length(); ) {
            dotIndex = s.indexOf(".");
            if (dotIndex < 0) {
                res.add(s);
                return res;
            }
            res.add(s.substring(0, dotIndex));
            s = s.substring(dotIndex + 1);
        }
        return res;
    }

    //TODO: does not work with values != String. Make it work with all values
    public String getValue(ArrayList<String> al) {//maybe make private
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();

        if (al.size() == 1)
            return cursor.getString(cursor.getColumnIndex(al.get(0)));

        try {
            LinkedTreeMap<String, Object> myMap = gson.fromJson(cursor.getString(cursor.getColumnIndex(al.get(0))), type);
            if (al.size() == 2) {
                if (myMap.get(al.get(1)) != null)
                    return (String) myMap.get(al.get(1));
                return "";
            }
            myMap = (LinkedTreeMap<String, Object>) myMap.get(al.get(1));
            if (al.size() > 2) {
                for (int i = 2; i < al.size() - 1; ++i) {
                    myMap = (LinkedTreeMap<String, Object>) myMap.get(al.get(i));
                }
                if (myMap.get(al.get(al.size() - 1)) != null)
                    return (String) myMap.get(al.get(al.size() - 1));
                return "";
            }
            return "";
        } catch (Exception e) {
            Log.e("Error Parsing JSON", e.toString());
            return "";
        }
    }


    //array of strings [column(containing json).key...key]
    //.getClass().getName()

//    private String getTitles(){}

    public String getCSV() {
        StringBuilder result = new StringBuilder();
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    for (int i = 0; i < info.size(); ++i) {
                        result.append(getValue(chopString(info.get(i))));
                        if (i < info.size() - 1)
                            result.append(";");
                    }
                    result.append("\n");
                } while (cursor.moveToNext());
            }
        }
        return result.toString();
    }


}
