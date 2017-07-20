package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;



public class ListCustomers extends AppCompatActivity {

    private DBManager dbManager;

    private ListView listView;

    private SimpleCursorAdapter adapter;

    final String[] from = new String[]{DatabaseHelper._ID,
            DatabaseHelper.JSON_CUSTOMERS, DatabaseHelper.JSON_META};

    final int[] to = new int[]{R.id.id, R.id.json, R.id.meta};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_emp_list);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back_arrow_grey);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();

        listView = (ListView) findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty));

        adapter = new SimpleCursorAdapter(this, R.layout.activity_view_record, cursor, from, to, 0);
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);

        // OnCLickListiner For List Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
                TextView idTextView = (TextView) view.findViewById(R.id.id);
                TextView jsonTextView = (TextView) view.findViewById(R.id.json);
                TextView metaTextView = (TextView) view.findViewById(R.id.meta);

                String id = idTextView.getText().toString();
                String json = jsonTextView.getText().toString();
                String meta = metaTextView.getText().toString();

                Intent modify_intent = new Intent(getApplicationContext(), ModifyInformation.class);
                modify_intent.putExtra("QRCode", json);
                modify_intent.putExtra("Meta", meta);
                modify_intent.putExtra("id", id);

                startActivity(modify_intent);
            }
        });
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
//                Intent i = new Intent(this, ListCustomers.class);
//                startActivity(i);
//                Already here
                break;
            }
            case R.id.action_export: {
                // do something
                break;
            }
            case R.id.action_info: {
                //Intent i = new Intent(this, ListCustomers.class);
                //startActivity(i);
                //Already here
                break;
            }
        }
        return false;
    }

}
