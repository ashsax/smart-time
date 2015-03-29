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
        Utility.clearNotification(context, Utility.ALARM_NOTIFICATION_ID);
        Utility.snooze(context, "alarm");
    }
}
