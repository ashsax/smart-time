package ashsax.test497;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    private TextView mClock;
    private SeekBar mSeekBar;
//    private LinearLayout mBox;
    private int minuteCount;
    private CircularSeekBar mCircularSeekBar;
    private Time mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTime = new Time();
        mClock = (TextView) findViewById(R.id.clock);
//        mBox = (LinearLayout) findViewById(R.id.box);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mCircularSeekBar = (CircularSeekBar) findViewById(R.id.circularSeekBar1);
        mCircularSeekBar.setMax(60);
        mCircularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                mTime.minute = progress;
                if(progress == 60)
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
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                minuteCount = 30 * i;
                hsvColor[0] = updateColor(i);
//                mBox.setBackgroundColor(Color.HSVToColor(hsvColor));
//                mClock.setText(updateClock(i));
                mTime.hour = i;
                minuteCount = mTime.getMinuteCount();
                mClock.setText(mTime.toString());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public float updateColor(int i) {
//        if (i < 24) {
//            return 240 - 100f * i / 48;
//        }
//        else {
//            return 140 + 100f * i / 48;
//        }
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
