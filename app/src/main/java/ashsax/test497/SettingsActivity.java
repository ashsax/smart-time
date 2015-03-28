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
import android.text.Editable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.text.Format;
import java.util.Calendar;

public class SettingsActivity extends ActionBarActivity {

    private SharedPreferences mCalendarPrefs;
    private SharedPreferences mTimePrefs;
//    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff005fbf));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.wakeup_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mTimePrefs = getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
        String minutesNeededPreset = mTimePrefs.getString("minutesPreset", "30");
        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setText(minutesNeededPreset);
        editText.setOnClickListener(

        );

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

                    myIntent = new Intent(SettingsActivity.this, AlarmReceiver.class);
                    myIntent.putExtra("calendarSync", true);
                    // if pendingIntent already set, cancel it first before making this new one
                    pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    // time needed to wake up in milliseconds

                    String minutesNeeded = editText.getText().toString();
                    int timeNeededToWakeUp = Integer.parseInt( minutesNeeded ) * 60 * 1000;

                    Long alarmTime = start - timeNeededToWakeUp;

                    String day = "tomorrow";
                    long startOfTomorrow = Utility.getStartOfTomorrow().getTimeInMillis();
                    if(alarmTime < startOfTomorrow)
                        day = "today";

                    if(alarmTime <= Calendar.getInstance().getTimeInMillis()){
                        Toast.makeText(SettingsActivity.this, "Cannot set alarm for the past", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);

                    Format df = DateFormat.getDateFormat(SettingsActivity.this);
                    Format tf = DateFormat.getTimeFormat(SettingsActivity.this);

                    Toast.makeText(SettingsActivity.this, "Alarm set for " + tf.format(alarmTime) + " for " + day, Toast.LENGTH_SHORT).show();
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
