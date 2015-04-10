package ashsax.test497;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.text.Format;
import java.util.Calendar;

public class CalendarReceiver extends BroadcastReceiver {

    public static PendingIntent mCalendarSearchPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("CalendarReceiver", "inside the beginning of onReceive");
        // by default, we call this receiver to search for events again at tomorrow midnight
        Calendar timeToSearchForEvents = Utility.getStartOfTomorrow();
        try {
            Long start = Utility.getAlarmTime(context);
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            // if pendingIntent already set, cancel it first before making this new one
            AlarmFragment.mCalendarPendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // time needed to wake up in milliseconds
            SharedPreferences sharedPrefs = context.getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
            int timeNeededToWakeUp = (sharedPrefs.getInt("timeNeededToGetReady", 0)) * 60 * 1000;
            Long alarmTime = start - timeNeededToWakeUp;
            if (alarmTime > Calendar.getInstance().getTimeInMillis()){
//                alarmTime = Calendar.getInstance().getTimeInMillis();
                MainActivity.mAlarmManager.set(AlarmManager.RTC, alarmTime, AlarmFragment.mCalendarPendingIntent);
//                String day = "tomorrow";
//                long startOfTomorrow = Utility.getStartOfTomorrow().getTimeInMillis();
//                if(alarmTime < startOfTomorrow)
//                    day = "today";
                AlarmFragment.calendarEventTime = alarmTime;
                AlarmFragment.mReminderPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, ReminderReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
                int fiveMinutes = 5 * 60 * 1000;
                // send notification 5 minutes later reminding user about alarm for tomorrow's first calendar event
                MainActivity.mAlarmManager.set(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis() + fiveMinutes, AlarmFragment.mReminderPendingIntent);

                Format tf = DateFormat.getTimeFormat(context);
                Format df = DateFormat.getDateFormat(context);
                Toast.makeText(context, "Alarm set for " + tf.format(alarmTime) + " " + df.format(alarmTime), Toast.LENGTH_SHORT).show();
                Log.v("CalendarReceiver", "Alarm set for " + tf.format(alarmTime) + " " + df.format(alarmTime));
                timeToSearchForEvents.setTimeInMillis(alarmTime + 30 * 1000); // search for events 30 seconds after alarm goes off
            }
            else {
                Log.v("CalendarReceiver", "Cannot set alarm for the past");
                Toast.makeText(context, "Cannot set alarm for the past", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Log.v("CalendarReceiver", "No events tomorrow");
        }

        Intent calendarIntent = new Intent(context, CalendarReceiver.class);
        if (mCalendarSearchPendingIntent != null) {
            mCalendarSearchPendingIntent.cancel();
        }
        mCalendarSearchPendingIntent = PendingIntent.getBroadcast(context, 0, calendarIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        MainActivity.mAlarmManager.set(AlarmManager.RTC, timeToSearchForEvents.getTimeInMillis(), mCalendarSearchPendingIntent);

        Format tf = DateFormat.getTimeFormat(context);
        Format df = DateFormat.getDateFormat(context);
        Log.v("CalendarReceiver", "next check of events at " + tf.format(timeToSearchForEvents.getTimeInMillis()) + " on " + df.format(timeToSearchForEvents.getTimeInMillis()));
    }
}
