package ashsax.test497;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.ToggleButton;

/**
 * Created by ash on 3/28/15.
 */
public class AlarmCancelReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AlarmFragment.mManualPendingIntent != null) {
            MainActivity.mAlarmManager.cancel(AlarmFragment.mManualPendingIntent);
            AlarmFragment.mManualPendingIntent.cancel();
        }
        if (AlarmFragment.mCalendarPendingIntent != null) {
            MainActivity.mAlarmManager.cancel(AlarmFragment.mCalendarPendingIntent);
            AlarmFragment.mCalendarPendingIntent.cancel();
        }
        Utility.clearNotifications(context);
        ToggleButton toggleButton = (ToggleButton)AlarmFragment.startAlarmButton;
        toggleButton.setChecked(false);
    }
}
