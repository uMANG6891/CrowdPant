package com.umang.crowdpant.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by umang on 10/02/16.
 */
public class CrowdPantContract {

    public static final String CONTENT_AUTHORITY = "com.umang.crowdpant";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SHIRT = "shirt";
    public static final String PATH_PANT = "pant";
    public static final String PATH_BOOKMARK = "bookmark";


    public static final class ShirtEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHIRT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIRT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIRT;

        public static final String TABLE_NAME = "shirt";
        public static final String COLUMN_STORED_AT = "stored_at";


        public static Uri buildOneShirtUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getShirtIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class PantEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PANT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PANT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PANT;

        public static final String TABLE_NAME = "pant";
        public static final String COLUMN_STORED_AT = "stored_at";


        public static Uri buildOnePantUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getPantIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    // saves bookmarked, combination of shirt and pant
    public static final class BookmarkEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKMARK).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKMARK;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKMARK;

        public static final String TABLE_NAME = "bookmark";

        public static final String COLUMN_SHIRT_ID = "shirt_id";
        public static final String COLUMN_PANT_ID = "pant_id";

        public static Uri buildBookmarkFromIdUri(long bookmark_id) {
            return ContentUris.withAppendedId(CONTENT_URI, bookmark_id);
        }

        public static String getIdFromBookmarkUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
