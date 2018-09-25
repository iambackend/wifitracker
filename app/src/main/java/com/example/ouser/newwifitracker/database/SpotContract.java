package com.example.ouser.newwifitracker.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class SpotContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.spots";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SPOTS = "spots";

    private SpotContract() {
    }

    public static final class SpotEntry implements BaseColumns {
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SPOTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SPOTS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SPOTS);
        public static final String TABLE_NAME = "spots";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SPOT_NAME = "name";
        public static final String COLUMN_SPOT_DATE_CREATED = "date";
        public static final String COLUMN_SPOT_WIFI_DATA = "data";
    }
}
