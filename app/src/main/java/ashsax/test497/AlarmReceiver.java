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
            final String[] EVENT_PROJECTION = new String[] {
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DTSTART
            };

            final int TITLE_INDEX = 0;
            final int DTSTART_INDEX = 1;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            long startTomorrow = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            long endTomorrow = calendar.getTimeInMillis();

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startTomorrow);
            ContentUris.appendId(builder, endTomorrow);
            Uri uri = builder.build();

            Cursor cursor = context.getContentResolver().query(uri, EVENT_PROJECTION, null, null, null);
            cursor.moveToFirst();

            Format df = DateFormat.getDateFormat(context);
            Format tf = DateFormat.getTimeFormat(context);

            if (cursor.getCount() == 0) {
                Log.d("AlarmReceiver", "No events tomorrow!");
                return;
            }

            String title = cursor.getString(TITLE_INDEX);
            Long start = cursor.getLong(DTSTART_INDEX);

            Intent myIntent = new Intent(context, AlarmReceiver.class);
            myIntent.putExtra("calendarSync", true);
            // if pendingIntent already set, cancel it first before making this new one
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // time needed to wake up in milliseconds
            SharedPreferences sharedPrefs = context.getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
            int timeNeededToWakeUp = (sharedPrefs.getInt("spinnerIndex", 0) + 1) * 30 * 60 * 1000;
            Long alarmTime = start - timeNeededToWakeUp;
            alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
            Log.d("AlarmReceiver", "Alarm set for " + tf.format(alarmTime) + " for tomorrow");

//            Log.d("AlarmReceiver", title + " on " + df.format(start) + " at " + tf.format(start));
        }
    }
}
