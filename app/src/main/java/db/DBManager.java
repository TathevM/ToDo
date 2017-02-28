package db;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;

import receiver.AlarmReceiver;
import task.Task;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Havayi on 28-Jan-17.
 */

public class DBManager {
    private DBHelper mDBHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getWritableDatabase();
    }

    public ArrayList<Task> getTasks(){
        ArrayList<Task> list = new ArrayList<>();

        Cursor cursor = mSQLiteDatabase.query(ToDoTable.TABLE_NAME , null , null , null , null, null ,null);
        while (cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(ToDoTable.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(ToDoTable.COLUMN_TITLE));
            String desc = cursor.getString(cursor.getColumnIndex(ToDoTable.COLUMN_DESC));
            long longDate = cursor.getLong(cursor.getColumnIndex(ToDoTable.COLUMN_DATE));
            Date date = new Date();
            date.setTime(longDate);
            long alert  = cursor.getLong(cursor.getColumnIndex(ToDoTable.COLUMN_ALERT));
            Task task = new Task(title , desc , date);
            task.setAlert(alert);
            task.setID(id);

            list.add(task);
        }
        cursor.close();
        return list;
    }

    public void deleteTaskByID(long ID){
        unregisterNextAlertNotification();
        mSQLiteDatabase.delete(ToDoTable.TABLE_NAME, ToDoTable.COLUMN_ID + " = ?" , new String[]{String.valueOf(ID)});
        registerNextAlertNotification();
    }

    public void deleteMultipleRowsByID(String[] IDs){
        String args = TextUtils.join(", " , IDs);
        mSQLiteDatabase.delete(ToDoTable.TABLE_NAME , ToDoTable.COLUMN_ID + " IN ( ? )" , new String[]{args});
    }

    public long updateTask(Task task){
        unregisterNextAlertNotification();
        long id = task.getID();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoTable.COLUMN_TITLE , task.getTitle());
        contentValues.put(ToDoTable.COLUMN_DESC , task.getDescription());
        contentValues.put(ToDoTable.COLUMN_DATE , task.getDate().getTime());
        contentValues.put(ToDoTable.COLUMN_ALERT , task.getAlert());
        long retValue =  mSQLiteDatabase.update(ToDoTable.TABLE_NAME , contentValues , ToDoTable.COLUMN_ID  + " = ?" , new String[]{String.valueOf(id)});

        registerNextAlertNotification();
        return retValue;
    }

    public long insertTask(Task task) {
        long retValue;
        if (task.getAlert() != -1) {
            unregisterNextAlertNotification();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDoTable.COLUMN_TITLE, task.getTitle());
        contentValues.put(ToDoTable.COLUMN_DESC, task.getDescription());
        contentValues.put(ToDoTable.COLUMN_DATE, task.getDate().getTime());
        contentValues.put(ToDoTable.COLUMN_ALERT, task.getAlert());
        retValue = mSQLiteDatabase.insert(ToDoTable.TABLE_NAME, null, contentValues);

        if (task.getAlert() != -1) {
            registerNextAlertNotification();
        }

        return retValue;
    }

    private Task getNextAlertedTask(){
        long now  = System.currentTimeMillis();
        Cursor cursor = mSQLiteDatabase.query(ToDoTable.TABLE_NAME ,
                null ,
                ToDoTable.COLUMN_ALERT + " <> ? and " + ToDoTable.COLUMN_ALERT + " > ?" ,
                new String[]{"-1" , String.valueOf(now)},
                null ,
                null ,
                ToDoTable.COLUMN_ALERT +  " ASC");

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex(ToDoTable.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(ToDoTable.COLUMN_TITLE));
            String desc = cursor.getString(cursor.getColumnIndex(ToDoTable.COLUMN_DESC));
            long longDate = cursor.getLong(cursor.getColumnIndex(ToDoTable.COLUMN_DATE));
            Date date = new Date();
            date.setTime(longDate);
            Task t = new Task(title,desc,date);
            long alert  = cursor.getLong(cursor.getColumnIndex(ToDoTable.COLUMN_ALERT));
            t.setAlert(alert);
            t.setID(id);
            cursor.close();
            return t;
        }
        else return null;
    }


    public void unregisterNextAlertNotification(){
        Task task = getNextAlertedTask();
        if(task != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("task", task);
            intent.setAction(String.valueOf(task.getID()));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) task.getID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            pendingIntent.cancel();
            alarmManager.cancel(pendingIntent);
        }
    }

    public void registerNextAlertNotification(){
        Task task = getNextAlertedTask();
        if (task != null) {
            long notifTime = task.getDate().getTime() - task.getAlert();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("task", task);
            intent.setAction(String.valueOf(task.getID()));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) task.getID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, notifTime, pendingIntent);
        }
    }
}
