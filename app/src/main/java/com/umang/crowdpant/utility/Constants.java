package com.umang.crowdpant.utility;

import com.umang.crowdpant.data.CrowdPantContract.BookmarkEntry;
import com.umang.crowdpant.data.CrowdPantContract.PantEntry;
import com.umang.crowdpant.data.CrowdPantContract.ShirtEntry;

/**
 * Created by umang on 10/02/16.
 */
public class Constants {

    public static final String[] SHIRT_PROJECTION_COLS = {
            ShirtEntry._ID,
            ShirtEntry.COLUMN_STORED_AT
    };
    public static final int COL_SHIRT_ID = 0;
    public static final int COL_SHIRT_STORED_AT = 1;

    public static final String[] PANT_PROJECTION_COLS = {
            PantEntry._ID,
            PantEntry.COLUMN_STORED_AT
    };
    public static final int COL_PANT_ID = 0;
    public static final int COL_PANT_STORED_AT = 1;

    public static final String[] BOOKMARK_PROJECTION_COLS = {
            BookmarkEntry.TABLE_NAME + "." + BookmarkEntry._ID,
            ShirtEntry.TABLE_NAME + "." + ShirtEntry._ID,
            ShirtEntry.TABLE_NAME + "." + ShirtEntry.COLUMN_STORED_AT,
            PantEntry.TABLE_NAME + "." + PantEntry._ID,
            PantEntry.TABLE_NAME + "." + PantEntry.COLUMN_STORED_AT,
    };
    public static final int COL_BOOKMARK_ID = 0;
    public static final int COL_BOOKMARK_SHIRT_ID = 1;
    public static final int COL_BOOKMARK_SHIRT_SAVED_AT = 2;
    public static final int COL_BOOKMARK_PANT_ID = 3;
    public static final int COL_BOOKMARK_PANT_SAVED_AT = 4;
}
