package receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.havayi.todo.MainActivity;
import com.havayi.todo.R;

import services.TaskAlarmerService;
import task.Task;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Havayi on 31-Jan-17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Task task = intent.getParcelableExtra("task");
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(task.getTitle())
                .setContentText(task.getDescription())
                .setAutoCancel(true)
                .setVibrate(new long[]{1000 , 1000 , 1000})
                .setSound(soundUri);

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        nm.notify((int)task.getID() , mBuilder.build());

        Intent intentService = new Intent(context, TaskAlarmerService.class);
        intentService.putExtra("task" , task);
        context.startService(intentService);
    }
}
