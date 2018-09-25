package com.example.ouser.newwifitracker;

import android.Manifest;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ouser.newwifitracker.database.SpotsCursorAdapter;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static com.example.ouser.newwifitracker.database.SpotContract.SpotEntry.COLUMN_SPOT_DATE_CREATED;
import static com.example.ouser.newwifitracker.database.SpotContract.SpotEntry.COLUMN_SPOT_NAME;
import static com.example.ouser.newwifitracker.database.SpotContract.SpotEntry.COLUMN_SPOT_WIFI_DATA;
import static com.example.ouser.newwifitracker.database.SpotContract.SpotEntry.CONTENT_URI;
import static com.example.ouser.newwifitracker.database.SpotContract.SpotEntry._ID;

public class SpotsListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SpotsCursorAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spots_list);


        // Setup FAB to open WiFiListActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_spot);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpotsListActivity.this, WiFiListActivity.class);
                startActivity(intent);
            }
        });

        adapter = new SpotsCursorAdapter(this, null);
        ListView listView = (ListView) findViewById(R.id.spots_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(SpotsListActivity.this, WiFiListActivity.class);
                intent.setData(ContentUris.withAppendedId(CONTENT_URI, id));
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {_ID, COLUMN_SPOT_NAME, COLUMN_SPOT_DATE_CREATED, COLUMN_SPOT_WIFI_DATA};
        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
