package providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Date;

import db.DBHelper;
import db.ToDoTable;
import task.Task;

/**
 * Created by Havayi on 28-Jan-17.
 */

public class TaskProvider extends ContentProvider {
    static final String AUTHORITY = "com.havayi.todo.providers";
    static final String TODO_PATH = "tasks";

    public static final Uri TODO_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TODO_PATH);

    static final String TODO_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + TODO_PATH;

    static final String TODO_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + TODO_PATH;

    static final int URI_TODO = 1;

    static final int URI_TODO_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, TODO_PATH, URI_TODO);
        uriMatcher.addURI(AUTHORITY, TODO_PATH + "/#", URI_TODO_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)){
            case URI_TODO:
                sortOrder = ToDoTable.COLUMN_ID + " ASC";
                break;
            case URI_TODO_ID:
                String id = uri.getLastPathSegment();
                selection = ToDoTable.COLUMN_ID + " = " + id;
                break;
            default:
                throw new IllegalArgumentException("Wrong uri: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(ToDoTable.TABLE_NAME , projection , selection, selectionArgs , null , null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver() , TODO_CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case URI_TODO:
                return TODO_CONTENT_TYPE;
            case URI_TODO_ID:
                return TODO_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_TODO)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long ID = db.insert(ToDoTable.TABLE_NAME , null , values);
        Uri resultUri = ContentUris.withAppendedId(TODO_CONTENT_URI , ID);
        getContext().getContentResolver().notifyChange(resultUri , null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case URI_TODO:
                break;
            case URI_TODO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = ToDoTable.COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + ToDoTable.COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        int count = db.delete(ToDoTable.TABLE_NAME , selection , selectionArgs);
        getContext().getContentResolver().notifyChange(uri , null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case URI_TODO:
                break;
            case URI_TODO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = ToDoTable.COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + ToDoTable.COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        int count = db.update(ToDoTable.TABLE_NAME , values , selection , selectionArgs);
        getContext().getContentResolver().notifyChange(uri , null);

        return count;
    }
}
