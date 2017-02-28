package services;

import android.app.IntentService;
import android.content.Intent;

import db.DBManager;
import task.Task;

/**
 * Created by Havayi on 01-Feb-17.
 */

public class TaskAlarmerService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    public TaskAlarmerService() {
        //super(name);
        super("TaskAlarmerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DBManager dbManager = new DBManager(this);
        Task t = intent.getParcelableExtra("task");
        t.setAlert(-1);
        dbManager.updateTask(t);
        //dbManager.registerNextAlertNotification();
    }
}
