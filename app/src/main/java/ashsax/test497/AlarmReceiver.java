package ashsax.test497;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Utility.wakeupScreen(context);
        Utility.createNotification(context, "alarm");
    }
}
