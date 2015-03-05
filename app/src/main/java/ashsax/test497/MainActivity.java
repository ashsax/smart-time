package ashsax.test497;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class MainActivity extends ActionBarActivity {

    private TextView mClock;
    private SeekBar mSeekBar;
    private Button startAlarmButton;
    private RelativeLayout mBox;
    private int minuteCount;
    private CircularSeekBar mMinuteSeekBar;
    private CircularSeekBar mHourSeekBar;
    private Time mTime;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    final float[] hsvColor = {240, 0.68f, 0.2f};
    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff005fbf));
        mBox = (RelativeLayout) findViewById(R.id.relativeLayout);
        mTime = new Time();
        mClock = (TextView) findViewById(R.id.clock);
        mHourSeekBar = (CircularSeekBar) findViewById(R.id.hourSeekBar);
        mMinuteSeekBar = (CircularSeekBar) findViewById(R.id.minuteSeekBar);
        mMinuteSeekBar.setMax(60);
        mHourSeekBar.setMax(24);
        mMinuteSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                mTime.minute = progress;
                if (progress == 60)
                    mTime.minute = 0;
                minuteCount = mTime.getMinuteCount();
                mClock.setText(mTime.toString());
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
        mBox.setBackgroundColor(Color.HSVToColor(hsvColor));
        mHourSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int i, boolean fromUser) {
                updateColor(i);
//                minuteCount = 30 * i;
//                hsvColor[0] = updateColor(i);
                mBox.setBackgroundColor(Color.HSVToColor(hsvColor));
//                mClock.setText(updateClock(i));
                mTime.hour = i;
                if (i == 24)
                    mTime.hour = 0;
                minuteCount = mTime.getMinuteCount();
                mClock.setText(mTime.toString());
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
//        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
////                minuteCount = 30 * i;
////                hsvColor[0] = updateColor(i);
////                mBox.setBackgroundColor(Color.HSVToColor(hsvColor));
////                mClock.setText(updateClock(i));
//                mTime.hour = i;
//                minuteCount = mTime.getMinuteCount();
//                mClock.setText(mTime.toString());
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (startAlarmButton == null) {
            startAlarmButton = (ToggleButton) findViewById(R.id.startAlarmButton);
        }
        if (mSharedPrefs == null) {
            mSharedPrefs = getSharedPreferences("calendarPrefs", Context.MODE_PRIVATE);
        }
        boolean calendarSync = mSharedPrefs.getBoolean("calendarSync", false);
        startAlarmButton.setEnabled(!calendarSync);
    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        String day = "today";

        // set alarm based on user inputted time
        if (on) {
            Calendar calendar = Calendar.getInstance();
            if (mTime.getMinuteCount() < calendar.get(Calendar.MINUTE) + 60 * calendar.get(Calendar.HOUR_OF_DAY)) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                day = "tomorrow";
            }
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, mTime.getMilliseconds());
            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            myIntent.putExtra("calendarSync", false);
            // if pendingIntent already set, cancel it first before making this new one
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(MainActivity.this, "Alarm set for " + mTime + " for " + day, Toast.LENGTH_SHORT).show();
        }
        else if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:
                Intent intentToStartSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentToStartSettings);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateColor(int i) {
        float hue = (float) Math.cos((2 * Math.PI * (float) i / 24));
        hsvColor[0] = 220 + 20f * hue;
        hsvColor[2] = ((1 - hue)*0.35f) + 0.2f;
    }

}
