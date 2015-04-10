package ashsax.test497;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class ReminderReceiver extends WakefulBroadcastReceiver {
    public ReminderReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        Utility.createNotification(context, "reminder");
    }
}
