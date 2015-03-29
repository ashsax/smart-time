package ashsax.test497;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by ash on 3/28/15.
 */
public class AlarmCancelReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utility.cancelAlarm(context, "alarm");
    }
}
