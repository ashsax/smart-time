package ashsax.test497;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class NapSnoozeReceiver extends WakefulBroadcastReceiver {
    public NapSnoozeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Utility.clearNotification(context, Utility.NAP_NOTIFICATION_ID);
        Utility.snooze(context, "napTimer");
    }
}
