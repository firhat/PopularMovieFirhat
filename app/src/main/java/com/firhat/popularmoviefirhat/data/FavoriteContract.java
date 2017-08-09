package com.firhat.popularmoviefirhat.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Macbook on 8/8/17.
 */

public class FavoriteContract {

    public static final String AUTHORITY = "com.firhat.popularmoviefirhat";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);

    public static final String PATH_FAVORITE = "favorite";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();


        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
