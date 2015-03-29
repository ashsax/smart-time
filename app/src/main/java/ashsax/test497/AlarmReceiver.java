package ashsax.test497;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager)context.getSystemService(
                Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "AlarmReceiver");
        wl.acquire(10000);

        Intent myIntent = new Intent(context, AlarmCancelReceiver.class);
        Intent snoozeIntent = new Intent(context, AlarmSnoozeReceiver.class);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 1000, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 1000, snoozeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("SmartTime")
                        .setContentText("Alarm sounded. Time to wake up!")
                        .addAction(R.drawable.ic_action_image_timelapse, "Snooze", snoozePendingIntent)
                        .addAction(R.drawable.ic_action_alarm, "Dismiss", dismissPendingIntent)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), AudioManager.STREAM_ALARM)
                        .setDeleteIntent(dismissPendingIntent);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        // if calendarSync is on, then set the alarm for the next day as well
        if (intent.getBooleanExtra("calendarSync", false)) {
            Long start;
            try {
                start = Utility.getAlarmTime(context);
            }
            catch (Exception e) {
                return;
            }

            myIntent = new Intent(context, AlarmReceiver.class);
            myIntent.putExtra("calendarSync", true);
            // if pendingIntent already set, cancel it first before making this new one
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // time needed to wake up in milliseconds
            SharedPreferences sharedPrefs = context.getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
            int timeNeededToWakeUp = (sharedPrefs.getInt("spinnerIndex", 0) + 1) * 30 * 60 * 1000;
            Long alarmTime = start - timeNeededToWakeUp;
//            mAlarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
//            Log.d("AlarmReceiver", "Alarm set for " + tf.format(alarmTime) + " on " + df.format(alarmTime));
//            Log.d("AlarmReceiver", "Alarm set for " + tf.format(alarmTime) + " for tomorrow");

//            Log.d("AlarmReceiver", title + " on " + df.format(start) + " at " + tf.format(start));
        }
    }
}
