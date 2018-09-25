package com.example.ouser.newwifitracker;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WiFiAdapter extends ArrayAdapter<JSONObject> {

    public WiFiAdapter(@NonNull Context context, @NonNull List<JSONObject> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wifi_item, parent, false);
        }
        JSONObject wifi = getItem(position);
        TextView name = convertView.findViewById(R.id.name);
        TextView power = convertView.findViewById(R.id.power);
        Log.d("WiFiAdapter JSON:", wifi.toString());
        try {
            name.setText(wifi.getString("SSID"));
            power.setText(Integer.toString(WifiManager.calculateSignalLevel(wifi.getInt("level"), 100)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public void changeDataset(List<JSONObject> newDataset){
        clear();
        addAll(newDataset);
    }
}
