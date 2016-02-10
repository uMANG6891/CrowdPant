package com.umang.crowdpant.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.umang.crowdpant.data.CrowdPantContract.BookmarkEntry;
import com.umang.crowdpant.data.CrowdPantContract.PantEntry;
import com.umang.crowdpant.data.CrowdPantContract.ShirtEntry;

/**
 * Created by umang on 10/02/16.
 */
public class CrowdPantDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "crowd_pant.db";
    private static final int DATABASE_VERSION = 1;

    public CrowdPantDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SHIRT_TABLE = "CREATE TABLE " + ShirtEntry.TABLE_NAME + " (" +
                ShirtEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ShirtEntry.COLUMN_STORED_AT + " TEXT NOT NULL); ";

        final String SQL_CREATE_PANT_TABLE = "CREATE TABLE " + PantEntry.TABLE_NAME + " (" +
                PantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PantEntry.COLUMN_STORED_AT + " TEXT NOT NULL); ";

        final String SQL_CREATE_BOOKMARK_TABLE = "CREATE TABLE " + BookmarkEntry.TABLE_NAME + " (" +
                BookmarkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BookmarkEntry.COLUMN_SHIRT_ID + " INTEGER NOT NULL, " +
                BookmarkEntry.COLUMN_PANT_ID + " INTEGER NOT NULL, "
                // Set up the SHIRT_ID as a foreign key to SHIRT table.
                + " FOREIGN KEY (" + BookmarkEntry.COLUMN_SHIRT_ID + ") REFERENCES " +
                ShirtEntry.TABLE_NAME + " (" + ShirtEntry._ID + ")"
                // Set up the PANT_ID as a foreign key to PANT table.
                + " FOREIGN KEY (" + BookmarkEntry.COLUMN_PANT_ID + ") REFERENCES " +
                PantEntry.TABLE_NAME + " (" + PantEntry._ID + "));";


        db.execSQL(SQL_CREATE_SHIRT_TABLE);
        db.execSQL(SQL_CREATE_PANT_TABLE);
        db.execSQL(SQL_CREATE_BOOKMARK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ShirtEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PantEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BookmarkEntry.TABLE_NAME);
        onCreate(db);
    }
}
