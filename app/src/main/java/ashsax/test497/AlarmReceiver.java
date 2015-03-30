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
import java.util.Calendar;

public class AlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utility.wakeupScreen(context);

        Utility.createNotification(context, "alarm");

        // if calendarSync is on, then set the alarm for the next day as well
        if (intent.getBooleanExtra("calendarSync", false)) {
            Long start;
            try {
                start = Utility.getAlarmTime(context);
            }
            catch (Exception e) {
                return;
            }

//            myIntent = new Intent(context, AlarmReceiver.class);
//            myIntent.putExtra("calendarSync", true);
//            // if pendingIntent already set, cancel it first before making this new one
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            // time needed to wake up in milliseconds
//            SharedPreferences sharedPrefs = context.getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
//            int timeNeededToWakeUp = (sharedPrefs.getInt("spinnerIndex", 0) + 1) * 30 * 60 * 1000;
//            Long alarmTime = start - timeNeededToWakeUp;
//            mAlarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
//            Log.d("AlarmReceiver", "Alarm set for " + tf.format(alarmTime) + " on " + df.format(alarmTime));
//            Log.d("AlarmReceiver", "Alarm set for " + tf.format(alarmTime) + " for tomorrow");

//            Log.d("AlarmReceiver", title + " on " + df.format(start) + " at " + tf.format(start));
        }
    }
}
