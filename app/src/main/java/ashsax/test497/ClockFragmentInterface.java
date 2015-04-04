package ashsax.test497;

import android.app.AlarmManager;
import android.support.v4.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Hayden on 4/3/2015.
 */
public class ClockFragmentInterface extends Fragment
{
    protected TextView mClock;
    protected static Button startAlarmButton;
    protected RelativeLayout mBox;
    protected CircularSeekBar mHourSeekBar;

    protected float[] hsvColor = {240, 0.68f, 0.2f};
    protected int[] tcbColor = { 0xff3B5998, 0xff3B5998, 0xffffffff };
    protected int[] tcbColor2 = { 0xff2c4d8c, 0xff3363a3, 0xff4090cc };
    protected SharedPreferences mSharedPrefs;

    protected ImageView mImg;
    protected ImageView mMinuteHand;
    final float minRotation = -117.29f;

    public ClockFragmentInterface() {
        // Required empty public constructor
    }

    public int getMax(){ System.exit(99); return 0; }

    public void updateColor(int i) {
        if(i == 12 || i == 0){
            updateHSV(i);
            tcbColor[0] = tcbColor[1] = tcbColor[2] = Color.HSVToColor(hsvColor);
            return;
        }

        updateHSV(i);
        tcbColor[0] = Color.HSVToColor(hsvColor);

        int centerColor = (i > 12) ? -1 : 1;
        updateHSV(i+centerColor);
        tcbColor[1] = Color.HSVToColor(hsvColor);

        int bottomColor = (i > 12) ? -3 : 3;
        updateHSV(i+bottomColor);
        tcbColor[2] = Color.HSVToColor(hsvColor);

        mBox.setBackgroundDrawable( new DrawableGradient(tcbColor, 0).SetTransparency(10));
        Log.v("updateColor", ""+ i +": "+ Integer.toHexString(tcbColor[0]) +" "+Integer.toHexString(tcbColor[1]) +" "+Integer.toHexString(tcbColor[2]));
    }

    public void updateHSV(int i){
        float hue = (float) Math.cos((2 * Math.PI * (float) i / getMax()));
        hsvColor[0] = 220 + 20f * hue;
        hsvColor[2] = ((1 - hue)*0.35f) + 0.2f;
    }

    public class DrawableGradient extends GradientDrawable {
        DrawableGradient(int[] colors, int cornerRadius) {
            super(GradientDrawable.Orientation.TOP_BOTTOM, colors);

            try {
                this.setShape(GradientDrawable.RECTANGLE);
                this.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                this.setCornerRadius(cornerRadius);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public DrawableGradient SetTransparency(int transparencyPercent) {
            this.setAlpha(255 - ((255 * transparencyPercent) / 100));

            return this;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (startAlarmButton == null) {
            startAlarmButton = (ToggleButton) getActivity().findViewById(R.id.startAlarmButton);
        }
        if (mSharedPrefs == null) {
            mSharedPrefs = getActivity().getSharedPreferences("calendarPrefs", Context.MODE_PRIVATE);
        }
    }


}
