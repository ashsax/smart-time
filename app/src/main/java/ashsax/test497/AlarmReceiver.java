package ashsax.test497;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.Format;
import java.util.Calendar;

public class AlarmReceiver extends WakefulBroadcastReceiver implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    public AlarmReceiver() {}

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Log.v("Tag", "onError called");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        try {
            mediaPlayer.setDataSource(context, alarmUri);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        }
        catch (IOException e) {
            Toast.makeText(context, "IOException", Toast.LENGTH_SHORT).show();
        }

        // if calendarSync is on, then set the alarm for the next day as well
        if (intent.getBooleanExtra("calendarSync", false)) {
            Long start;
            try {
                start = Utility.getAlarmTime(context);
            }
            catch (Exception e) {
                return;
            }

            Intent myIntent = new Intent(context, AlarmReceiver.class);
            myIntent.putExtra("calendarSync", true);
            // if pendingIntent already set, cancel it first before making this new one
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // time needed to wake up in milliseconds
            SharedPreferences sharedPrefs = context.getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
            int timeNeededToWakeUp = (sharedPrefs.getInt("spinnerIndex", 0) + 1) * 30 * 60 * 1000;
            Long alarmTime = start - timeNeededToWakeUp;
//            alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
//            Log.d("AlarmReceiver", "Alarm set for " + tf.format(alarmTime) + " on " + df.format(alarmTime));
//            Log.d("AlarmReceiver", "Alarm set for " + tf.format(alarmTime) + " for tomorrow");

//            Log.d("AlarmReceiver", title + " on " + df.format(start) + " at " + tf.format(start));
        }
    }
}
