package db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Havayi on 28-Jan-17.
 */

public class ToDoTable {

    public static final String TABLE_NAME = "TASKS";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_DESC = "DESCRIPTION";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_ALERT = "ALERT";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_DESC + " TEXT, " +
            COLUMN_DATE + " DATE, " +
            COLUMN_ALERT + " INTEGER)";

    public static void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
