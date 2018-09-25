package com.example.ouser.newwifitracker.database;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.ouser.newwifitracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpotsCursorAdapter extends CursorAdapter {
    /**
     * Constructor that always enables auto-requery.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @deprecated This option is discouraged, as it results in Cursor queries
     * being performed on the application's UI thread and thus can cause poor
     * responsiveness or even Application Not Responding errors.  As an alternative,
     * use {@link LoaderManager} with a {@link CursorLoader}.
     */
    public SpotsCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.spot_item, parent, false);
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView number_of_wifis = (TextView) view.findViewById(R.id.number_of_wifis);

        name.setText(cursor.getString(1));
        time.setText(cursor.getString(2));
        try {
            number_of_wifis.setText(String.valueOf(new JSONArray(cursor.getString(3)).length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
