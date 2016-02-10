package com.umang.crowdpant.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.umang.crowdpant.data.CrowdPantContract.BookmarkEntry;
import com.umang.crowdpant.data.CrowdPantContract.PantEntry;
import com.umang.crowdpant.data.CrowdPantContract.ShirtEntry;


/**
 * Created by umang on 10/02/16.
 */
public class CrowdPantProvider extends ContentProvider {

    static final int SHIRT = 100;
    static final int SHIRT_ONE = 101;

    static final int PANT = 200;
    static final int PANT_ONE = 201;

    static final int BOOKMARK = 300;
    static final int BOOKMARK_ONE = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    // to get shirt and pant for particular bookmark
    private static final SQLiteQueryBuilder sShirtPantQueryBuilder;

    static {
        sShirtPantQueryBuilder = new SQLiteQueryBuilder();
        // This is an inner join which looks like
        // bookmark INNER JOIN outfit ON bookmark.shirt_id= shirt._id
        //          INNER JOIN outfit ON bookmark.pant_id= pant._id
        sShirtPantQueryBuilder.setTables(
                BookmarkEntry.TABLE_NAME
                        + " INNER JOIN " + ShirtEntry.TABLE_NAME +
                        " ON " + BookmarkEntry.TABLE_NAME + "." + BookmarkEntry.COLUMN_SHIRT_ID +
                        " = " + ShirtEntry.TABLE_NAME + "." + ShirtEntry._ID

                        + " INNER JOIN " + PantEntry.TABLE_NAME +
                        " ON " + BookmarkEntry.TABLE_NAME + "." + BookmarkEntry.COLUMN_PANT_ID +
                        " = " + PantEntry.TABLE_NAME + "." + PantEntry._ID);
    }


    private CrowdPantDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CrowdPantContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CrowdPantContract.PATH_SHIRT, SHIRT);
        matcher.addURI(authority, CrowdPantContract.PATH_SHIRT + "/#", SHIRT_ONE);

        matcher.addURI(authority, CrowdPantContract.PATH_PANT, PANT);
        matcher.addURI(authority, CrowdPantContract.PATH_PANT + "/#", PANT_ONE);

        matcher.addURI(authority, CrowdPantContract.PATH_BOOKMARK, BOOKMARK);
        matcher.addURI(authority, CrowdPantContract.PATH_BOOKMARK + "/#", BOOKMARK_ONE);

        return matcher;
    }

    private Cursor getBookmarks(Uri uri, String[] projection, String sortOrder) {
        return sShirtPantQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new CrowdPantDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        String _id;
        switch (sUriMatcher.match(uri)) {

            // "bookmark/#"
            case BOOKMARK_ONE:
                _id = String.valueOf(BookmarkEntry.getIdFromBookmarkUri(uri));
                retCursor = mOpenHelper.getReadableDatabase().query(
                        BookmarkEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{_id},
                        null,
                        null,
                        sortOrder
                );
                break;
            // "bookmark/"
            case BOOKMARK:
                retCursor = getBookmarks(uri, projection, sortOrder);
                break;

            // "shirt/#"
            case PANT_ONE:
                _id = String.valueOf(PantEntry.getPantIdFromUri(uri));
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PantEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{_id},
                        null,
                        null,
                        sortOrder
                );
                break;
            // "shirt"
            case PANT:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PantEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            // "shirt/#"
            case SHIRT_ONE:
                _id = String.valueOf(ShirtEntry.getShirtIdFromUri(uri));
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ShirtEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{_id},
                        null,
                        null,
                        sortOrder
                );
                break;
            // "shirt"
            case SHIRT:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ShirtEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null)
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKMARK_ONE:
                return BookmarkEntry.CONTENT_ITEM_TYPE;
            case BOOKMARK:
                return BookmarkEntry.CONTENT_TYPE;


            case PANT_ONE:
                return PantEntry.CONTENT_ITEM_TYPE;
            case PANT:
                return PantEntry.CONTENT_TYPE;

            case SHIRT_ONE:
                return ShirtEntry.CONTENT_ITEM_TYPE;
            case SHIRT:
                return ShirtEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch (match) {
            case BOOKMARK:
                _id = db.insert(BookmarkEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = BookmarkEntry.buildBookmarkFromIdUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case PANT:
                _id = db.insert(PantEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PantEntry.buildOnePantUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case SHIRT:
                _id = db.insert(ShirtEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ShirtEntry.buildOneShirtUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (selection == null) selection = "1";
        switch (match) {
            case BOOKMARK:
                rowsDeleted = db.delete(BookmarkEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PANT:
                rowsDeleted = db.delete(PantEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SHIRT:
                rowsDeleted = db.delete(ShirtEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            if (getContext() != null)
                getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case BOOKMARK:
                rowsUpdated = db.update(BookmarkEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PANT:
                rowsUpdated = db.update(PantEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SHIRT:
                rowsUpdated = db.update(ShirtEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            if (getContext() != null)
                getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
