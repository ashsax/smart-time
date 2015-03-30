package ashsax.test497;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * Created by ash on 3/28/15.
 */
public class AlarmCancelReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utility.cancelAlarm(context, "alarm");
/*        SharedPreferences sharedPreferences = context.getSharedPreferences("calendarPrefs", Context.MODE_PRIVATE);

        // Set same alarm for next day
        long time = sharedPreferences.getLong("Alarm", 0);
        // value is milliseconds in a day
        time += (86400000);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("Alarm", time);
        editor.apply();
        Intent myIntent = new Intent(context, ReminderReceiver.class);*/
    }
}
