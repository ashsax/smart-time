package ashsax.test497;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class NapCancelReceiver extends WakefulBroadcastReceiver {
    public NapCancelReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Utility.cancelAlarm(context, "napTimer");
    }
}
