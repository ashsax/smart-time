package ashsax.test497;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.Format;
import java.util.Calendar;

/**
 * Created by ash on 3/9/15.
 */
public class Utility {
    public static final int ALARM_NOTIFICATION_ID = 0;
    public static final int NAP_NOTIFICATION_ID = 1;
    public static final int REMINDER_NOTIFICATION_ID = 2;

    public static Long getAlarmTime(Context context) throws Exception {
        // begin querying calendar
        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART
        };

        final int TITLE_INDEX = 0;
        final int DTSTART_INDEX = 1;

        Calendar calendar = getStartOfTomorrow();
        long startTomorrow = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        long endTomorrow = calendar.getTimeInMillis();

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startTomorrow);
        ContentUris.appendId(builder, endTomorrow);
        Uri uri = builder.build();

        Cursor cursor = context.getContentResolver().query(uri, EVENT_PROJECTION, null, null, CalendarContract.Events.DTSTART + " ASC");
        cursor.moveToFirst();

        // if there are no events, say so and be done.
        if (cursor.getCount() == 0) {
            Toast.makeText(context, "No events tomorrow!", Toast.LENGTH_SHORT).show();
            throw new Exception();
        }

        // otherwise set alarm for tomorrow, based on spinner value corresponding to time needed to wake up
        String title = cursor.getString(TITLE_INDEX);
        Long start = cursor.getLong(DTSTART_INDEX);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(start);
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.DAY_OF_YEAR, tomorrow.get(Calendar.DAY_OF_YEAR));
        start = cal.getTimeInMillis();

        while (cursor.moveToNext()) {
            Long temp_start = cursor.getLong(DTSTART_INDEX);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(temp_start);
            Calendar tomorrow1 = Calendar.getInstance();
            tomorrow1.add(Calendar.DAY_OF_YEAR, 1);
            cal1.set(Calendar.DAY_OF_YEAR, tomorrow1.get(Calendar.DAY_OF_YEAR));
            temp_start = cal1.getTimeInMillis();

            if (temp_start < start) {
                start = temp_start;
                title = cursor.getString(TITLE_INDEX);
            }
        }

        return start;
    }

    public static Calendar getStartOfTomorrow() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        return tomorrow;
    }

    public static void clearNotification(Context context, int notificationID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
    }

    public static void wakeupScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(
                Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "AlarmReceiver");
        wl.acquire(10000);
    }

    public static void createNotification(Context context, String notificationType) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("SmartTime");
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 1000, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent snoozePendingIntent;
        int notificationID = ALARM_NOTIFICATION_ID; // default ID corresponds to alarm
        if (notificationType == "alarm") {
            notificationID = ALARM_NOTIFICATION_ID;
            mBuilder.setContentText("Alarm sounded. Time to wake up!");
            Intent dismissIntent = new Intent(context, AlarmCancelReceiver.class);
            Intent snoozeIntent = new Intent(context, AlarmSnoozeReceiver.class);
            dismissPendingIntent = PendingIntent.getBroadcast(context, 1000, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            snoozePendingIntent = PendingIntent.getBroadcast(context, 1000, snoozeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.addAction(R.drawable.ic_stat_av_snooze, "Snooze", snoozePendingIntent)
                    .setDeleteIntent(dismissPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), AudioAttributes.USAGE_ALARM);
        }
        else if (notificationType == "napTimer") {
            notificationID = NAP_NOTIFICATION_ID;
            mBuilder.setContentText("Nap is over. Time to wake up!");
            Intent dismissIntent = new Intent(context, NapCancelReceiver.class);
            Intent snoozeIntent = new Intent(context, NapSnoozeReceiver.class);
            dismissPendingIntent = PendingIntent.getBroadcast(context, 1000, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            snoozePendingIntent = PendingIntent.getBroadcast(context, 1000, snoozeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.addAction(R.drawable.ic_stat_av_snooze, "Snooze", snoozePendingIntent)
                    .setDeleteIntent(dismissPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), AudioAttributes.USAGE_ALARM);
        }
        else if (notificationType == "reminder") {
            notificationID = REMINDER_NOTIFICATION_ID;
            mBuilder.setContentText("Alarm set for the same time tomorrow");
            Intent dismissIntent = new Intent(context, ReminderCancelReceiver.class);
            dismissPendingIntent = PendingIntent.getBroadcast(context, 1000, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        mBuilder.setPriority(Notification.PRIORITY_MAX)
                .addAction(R.drawable.ic_stat_action_alarm_off, "Dismiss", dismissPendingIntent);

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
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    public static void snooze(Context context, String snoozeType) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        // GET SNOOZE AMOUNT FROM SETTINGS
        int snooze_amount = SettingsFragment.getSnoozeTime(context);
        calendar.add(Calendar.MINUTE, snooze_amount);
        Intent myIntent = new Intent();
        if (snoozeType == "alarm") {
            myIntent = new Intent(context, AlarmReceiver.class);
        }
        else if (snoozeType == "napTimer") {
            myIntent = new Intent(context, NapReceiver.class);
        }
        // if pendingIntent already set, cancel it first before making this new one
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "Snoozing for " + snooze_amount + " minutes!", Toast.LENGTH_SHORT).show();
    }

    public static void cancelAlarm(Context context, String cancelType) {
        if (AlarmFragment.mManualPendingIntent != null) {
            MainActivity.mAlarmManager.cancel(AlarmFragment.mManualPendingIntent);
            AlarmFragment.mManualPendingIntent.cancel();
        }
        if (AlarmFragment.mCalendarPendingIntent != null) {
            MainActivity.mAlarmManager.cancel(AlarmFragment.mCalendarPendingIntent);
            AlarmFragment.mCalendarPendingIntent.cancel();
        }
        int notificationID = ALARM_NOTIFICATION_ID; // default ID corresponds to alarm
        if (cancelType == "alarm") {
            notificationID = ALARM_NOTIFICATION_ID;
            ToggleButton toggleButton = (ToggleButton)AlarmFragment.startAlarmButton;
            if (toggleButton != null) {
                toggleButton.setChecked(false);
            }
        }
        else if (cancelType == "napTimer") {
            notificationID = NAP_NOTIFICATION_ID;
            Log.v("cancelAlarm", "cancel from napTimer");
            ToggleButton toggleButton = (ToggleButton) NapTimerFragment.startAlarmButton;
            if (toggleButton != null) {
                toggleButton.setChecked(false);
            }
        }
        Utility.clearNotification(context, notificationID);
    }
}
