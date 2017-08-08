package com.firhat.popularmoviefirhat.data;

import android.provider.BaseColumns;

/**
 * Created by Macbook on 8/8/17.
 */

public class FavoriteContract {

    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
