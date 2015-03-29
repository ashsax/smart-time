package ashsax.test497;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmSnoozeReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmReceiver.mediaPlayer.stop();
        Utility.clearNotifications(context);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        // GET SNOOZE AMOUNT FROM SETTINGS
        int snooze_amount = 1;
        calendar.add(Calendar.MINUTE, snooze_amount);
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        myIntent.putExtra("calendarSync", false);
        // if pendingIntent already set, cancel it first before making this new one
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "Snoozing for " + snooze_amount + " minutes!", Toast.LENGTH_SHORT).show();
    }
}
