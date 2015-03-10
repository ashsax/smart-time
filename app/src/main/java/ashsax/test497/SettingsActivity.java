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

                    Long start;
                    try {
                        start = Utility.getAlarmTime(SettingsActivity.this);
                    }
                    catch (Exception e) {
                        return;
                    }
//                    Toast.makeText(SettingsActivity.this, "cal is set to " + df.format(start) + " at " + tf.format(start), Toast.LENGTH_SHORT).show();

                    myIntent = new Intent(SettingsActivity.this, AlarmReceiver.class);
                    myIntent.putExtra("calendarSync", true);
                    // if pendingIntent already set, cancel it first before making this new one
                    pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    // time needed to wake up in milliseconds
                    int timeNeededToWakeUp = (mSpinner.getSelectedItemPosition() + 1) * 30 * 60 * 1000;
                    Long alarmTime = start - timeNeededToWakeUp;

                    if(alarmTime > Calendar.getInstance().getTimeInMillis())
                        alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);

                    Format df = DateFormat.getDateFormat(SettingsActivity.this);
                    Format tf = DateFormat.getTimeFormat(SettingsActivity.this);

                    Toast.makeText(SettingsActivity.this, "Alarm set for " + tf.format(alarmTime) + " for tomorrow", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(SettingsActivity.this, "Alarm set for "+ (alarmTime - Calendar.getInstance().getTimeInMillis()), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(SettingsActivity.this, "Alarm set for " + df.format(alarmTime) + " at " + tf.format(alarmTime), Toast.LENGTH_SHORT).show();

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
