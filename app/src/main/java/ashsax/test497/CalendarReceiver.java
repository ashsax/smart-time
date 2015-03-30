package ashsax.test497;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.text.Format;
import java.util.Calendar;

public class CalendarReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar timeToSearchForEvents = Calendar.getInstance();
        // by default, we call this receiver to search for events again at tomorrow midnight
        timeToSearchForEvents.set(Calendar.HOUR_OF_DAY, 0);
        timeToSearchForEvents.add(Calendar.DAY_OF_YEAR, 1);
        try {
            Long start = Utility.getAlarmTime(context);
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            // if pendingIntent already set, cancel it first before making this new one
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // time needed to wake up in milliseconds
            SharedPreferences sharedPrefs = context.getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
            int timeNeededToWakeUp = (sharedPrefs.getInt("timeNeededToGetReady", 0)) * 60 * 1000;
            Long alarmTime = start - timeNeededToWakeUp;
            if (alarmTime > Calendar.getInstance().getTimeInMillis()){
                MainActivity.mAlarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
                String day = "tomorrow";
                long startOfTomorrow = Utility.getStartOfTomorrow().getTimeInMillis();
                if(alarmTime < startOfTomorrow)
                    day = "today";

                if(alarmTime <= Calendar.getInstance().getTimeInMillis()){
                    Toast.makeText(context, "Cannot set alarm for the past", Toast.LENGTH_SHORT).show();
                    return;
                }

                Format tf = DateFormat.getTimeFormat(context);

                Toast.makeText(context, "Alarm set for " + tf.format(alarmTime) + " for " + day, Toast.LENGTH_SHORT).show();
                timeToSearchForEvents.setTimeInMillis(alarmTime + 30 * 60 * 1000); // search for events 30 minutes after alarm goes off
            }
            else {
                Toast.makeText(context, "Cannot set alarm for the past", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {}

        Intent calendarIntent = new Intent(context, CalendarReceiver.class);
        PendingIntent calendarPendingIntent = PendingIntent.getBroadcast(MainActivity.mAlarmContext, 0, calendarIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        MainActivity.mAlarmManager.set(AlarmManager.RTC, timeToSearchForEvents.getTimeInMillis(), calendarPendingIntent);

    }
}
