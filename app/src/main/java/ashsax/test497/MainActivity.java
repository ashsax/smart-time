package ashsax.test497;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class MainActivity extends ActionBarActivity {

    private TextView mClock;
    private SeekBar mSeekBar;
    private Button startAlarmButton;
//    private LinearLayout mBox;
    private int minuteCount;
    private CircularSeekBar mMinuteSeekBar;
    private CircularSeekBar mHourSeekBar;
    private Time mTime;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff005fbf));
        startAlarmButton = (ToggleButton) findViewById(R.id.startAlarmButton);
        mTime = new Time();
        mClock = (TextView) findViewById(R.id.clock);
        mHourSeekBar = (CircularSeekBar) findViewById(R.id.hourSeekBar);
        mMinuteSeekBar = (CircularSeekBar) findViewById(R.id.minuteSeekBar);
        mMinuteSeekBar.setMax(60);
        mHourSeekBar.setMax(24);
        mMinuteSeekBar.setCircleColor(Color.BLACK);
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
        final float[] hsvColor = {240, 1, 1};
//        mBox.setBackgroundColor(Color.HSVToColor(hsvColor));
        mHourSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int i, boolean fromUser) {
//                minuteCount = 30 * i;
//                hsvColor[0] = updateColor(i);
//                mBox.setBackgroundColor(Color.HSVToColor(hsvColor));
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

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        String day = "today";

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
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(MainActivity.this, "Alarm set for " + mTime + " for " + day, Toast.LENGTH_SHORT).show();
        }
        else {
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public float updateColor(int i) {
        float val = (float) Math.cos((2 * Math.PI * (float) i / 48));
        return 210 + 30f * val;
    }

    public String updateClock(int i) {
        if(i == 0)
            return "12:00 AM";
        else if(i == 1)
            return "12:30 AM";
        else if(i == 24)
            return "12:00 PM";
        else if(i == 25)
            return "12:30 PM";


        boolean am = true;

        String time = "";

        if(i >= 24){
            am = false;
            time += String.valueOf((i-24)/2);
        }
        else
            time += String.valueOf(i/2);

        time += ":";

        int result = i%2;
        if(result == 1)
            time += "30";
        else
            time += "00";

        if(am)
            time += " AM";
        else
            time += " PM";

//        time += String.valueOf(i%2);

        return time;



    }

}
