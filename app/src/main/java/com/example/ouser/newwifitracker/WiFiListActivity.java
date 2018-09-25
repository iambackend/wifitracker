package com.example.ouser.newwifitracker;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ouser.newwifitracker.database.SpotContract;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class WiFiListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_WIFI_STATE = 14;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 88;

    private List<JSONObject> wifiResults = null;
    private EditText mNameEditText;
    private ListView mListView;
    private WiFiAdapter mWifiAdapter;

    private boolean mSpotHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSpotHasChanged = true;
            return false;
        }
    };
    private Uri mUri = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_list);

        mNameEditText = (EditText) findViewById(R.id.edit_spot_name);

        mNameEditText.setOnTouchListener(mTouchListener);

        if (getIntent().getData() != null) {
            setTitle("Edit Spot");
            mUri = getIntent().getData();
            getLoaderManager().initLoader(0, null, this);
        } else
            setTitle("Add Spot");

        setupListView();
    }


    private void setupListView() {// Here, thisActivity is the current activity
        int b = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_WIFI_STATE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                        WiFiListActivity.MY_PERMISSIONS_REQUEST_ACCESS_WIFI_STATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        mWifiAdapter = new WiFiAdapter(this, new ArrayList<JSONObject>());
        mListView = (ListView) this.findViewById(R.id.wifi_list);
        mListView.setAdapter(mWifiAdapter);

        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "Success?", Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                String json = gson.toJson(wifiManager.getScanResults());
                Log.d("Received JSON:", json);
                wifiResults = new ArrayList<>();
                try {
                    JSONArray jeeezon = new JSONArray(json);
                    for(int i = 0; i < jeeezon.length(); i++){
                        wifiResults.add(jeeezon.getJSONObject(i));
                        Log.d("JSON in for", jeeezon.getJSONObject(i).toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mWifiAdapter.changeDataset(wifiResults);
                mWifiAdapter.notifyDataSetInvalidated();
                setTitle(new GregorianCalendar().getTime().toString());
                getBaseContext().unregisterReceiver(this);
                mSpotHasChanged = true;
            }
        };
        if (mUri == null) {
            this.registerReceiver(broadcastReceiver, new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
        }

        findViewById(R.id.fab_sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseContext().registerReceiver(broadcastReceiver, new IntentFilter(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiManager.startScan();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.wifi_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save:
                saveSpot();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this spot?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the spot.
                deleteSpot();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the spot.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteSpot() {
        if (getContentResolver().delete(mUri, null, null) != 0)
            Toast.makeText(this, "Spot was succesfully deleted", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Error occurred during spot deletion, so I have no idea what is going on", Toast.LENGTH_LONG);
        finish();
    }

    private void saveSpot() throws IllegalArgumentException {
        ContentValues values = new ContentValues();
        values.put(SpotContract.SpotEntry.COLUMN_SPOT_NAME, mNameEditText.getText().toString().trim());
        values.put(SpotContract.SpotEntry.COLUMN_SPOT_DATE_CREATED, getTitle().toString().trim());
        Gson gson = new Gson();
        values.put(SpotContract.SpotEntry.COLUMN_SPOT_WIFI_DATA, gson.toJson(wifiResults));
        Log.d("AFHDAKFJHBDHJFBAHJKS", "Juicy JSON: " + gson.toJson(wifiResults));

        if (mUri == null)
            getContentResolver().insert(SpotContract.SpotEntry.CONTENT_URI, values);
        else
            getContentResolver().update(mUri, values, null, null);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Want to leave without saving changes?");
        builder.setPositiveButton("Yes", discardButtonListener);
        builder.setNegativeButton("No, stay editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mSpotHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToNext();
        mNameEditText.setText(data.getString(1));
        setTitle(data.getString(2));

        Gson gson = new Gson();
        Log.d("jafdks", "Juicy JSON is back " + data.getString(3));
        Type type = new TypeToken<List<JSONObject>>() {
        }.getType();
        wifiResults = gson.fromJson(data.getString(3), type
        );
        mWifiAdapter.changeDataset(wifiResults);
        mWifiAdapter.notifyDataSetInvalidated();

        Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        setTitle(null);
        wifiResults = new ArrayList<>();
        mWifiAdapter.changeDataset(wifiResults);
        mWifiAdapter.notifyDataSetInvalidated();

    }
}
