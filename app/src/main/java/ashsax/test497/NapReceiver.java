package ashsax.test497;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

public class NapReceiver extends WakefulBroadcastReceiver {
    public NapReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Utility.wakeupScreen(context);
        Utility.createNotification(context, "napTimer");
    }
}
