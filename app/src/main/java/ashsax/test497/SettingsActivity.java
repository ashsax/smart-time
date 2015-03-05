package ashsax.test497;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.text.Format;
import java.util.Calendar;

public class SettingsActivity extends ActionBarActivity {

    private SharedPreferences mCalendarPrefs;
    private SharedPreferences mTimePrefs;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff005fbf));

        mSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.wakeup_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mTimePrefs = getSharedPreferences("timePrefs", Context.MODE_PRIVATE);

        // We need to save spinnerIndex -> used to calculate how much time is needed to wake up.
        // This is used in AlarmReceiver when setting the calendar alarm for the next day
        // (only if calendarSync is on).
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = mTimePrefs.edit();
                editor.putInt("spinnerIndex", i);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mCalendarPrefs = getSharedPreferences("calendarPrefs", Context.MODE_PRIVATE);
        boolean calendarSync = mCalendarPrefs.getBoolean("calendarSync", false);
        final Switch calendarSyncSwitch = (Switch) findViewById(R.id.calendarSyncSwitch);
        calendarSyncSwitch.setChecked(calendarSync);
        calendarSyncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = mCalendarPrefs.edit();
                editor.putBoolean("calendarSync", b);
                editor.apply();
                // if calendarSync is switched off, then cancel the calendar alarm previously set
                if (!b) {
                    Intent myIntent = new Intent(SettingsActivity.this, AlarmReceiver.class);
                    myIntent.putExtra("calendarSync", false);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, myIntent, PendingIntent.FLAG_NO_CREATE);
                    if (pendingIntent != null) {
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                        pendingIntent.cancel();
                    }
                    Toast.makeText(SettingsActivity.this, "Alarm previously set by calendar cancelled", Toast.LENGTH_SHORT).show();
                }
                else {
                    // cancel previous calendar alarm
                    Intent myIntent = new Intent(SettingsActivity.this, AlarmReceiver.class);
                    myIntent.putExtra("calendarSync", true);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, myIntent, PendingIntent.FLAG_NO_CREATE);
                    if (pendingIntent != null) {
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                        pendingIntent.cancel();
                    }

                    // begin querying calendar
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

                    Cursor cursor = getContentResolver().query(uri, EVENT_PROJECTION, null, null, null);
                    cursor.moveToFirst();

                    Format df = DateFormat.getDateFormat(SettingsActivity.this);
                    Format tf = DateFormat.getTimeFormat(SettingsActivity.this);

                    // if there are no events, say so and be done.
                    if (cursor.getCount() == 0) {
                        Toast.makeText(SettingsActivity.this, "No events tomorrow!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // otherwise set alarm for tomorrow, based on spinner value corresponding to time needed to wake up
                    String title = cursor.getString(TITLE_INDEX);
                    Long start = cursor.getLong(DTSTART_INDEX);

                    myIntent = new Intent(SettingsActivity.this, AlarmReceiver.class);
                    myIntent.putExtra("calendarSync", true);
                    // if pendingIntent already set, cancel it first before making this new one
                    pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    // time needed to wake up in milliseconds
                    int timeNeededToWakeUp = (mSpinner.getSelectedItemPosition() + 1) * 30 * 60 * 1000;
                    Long alarmTime = start - timeNeededToWakeUp;
                    alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
                    Toast.makeText(SettingsActivity.this, "Alarm set for " + tf.format(alarmTime) + " for tomorrow", Toast.LENGTH_SHORT).show();

//                    Toast.makeText(SettingsActivity.this, title + " on " + df.format(start) + " at " + tf.format(start), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

}
