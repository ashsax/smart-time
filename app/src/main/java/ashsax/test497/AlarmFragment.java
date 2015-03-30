package ashsax.test497;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlarmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment {

    private TextView mClock;
    public static Button startAlarmButton;
    private RelativeLayout mBox;
    private CircularSeekBar mMinuteSeekBar;
    private CircularSeekBar mHourSeekBar;
    private Time mTime;
    public static PendingIntent mCalendarPendingIntent;
    public static PendingIntent mManualPendingIntent;
    final float[] hsvColor = {240, 0.68f, 0.2f};
    private SharedPreferences mSharedPrefs;

    private final static int maxHours = 24;
    private final static int maxMinutes = 60;

    private ImageView mImg;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmFragment newInstance(String param1, String param2) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_screen, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBox = (RelativeLayout) getActivity().findViewById(R.id.relativeLayout);
        mTime = new Time();
        mClock = (TextView) getActivity().findViewById(R.id.clock);
        startAlarmButton = (ToggleButton) getActivity().findViewById(R.id.startAlarmButton);
        mHourSeekBar = (CircularSeekBar) getActivity().findViewById(R.id.hourSeekBar);
        mMinuteSeekBar = (CircularSeekBar) getActivity().findViewById(R.id.minuteSeekBar);
        mMinuteSeekBar.setMax(maxMinutes);
        mHourSeekBar.setMax(maxHours);

        mImg = (ImageView) getActivity().findViewById(R.id.sunmoon);
        updateAlpha(0);

        mMinuteSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                mTime.minute = progress;
                if (progress == maxMinutes) {
                    mTime.minute = 0;
                    mMinuteSeekBar.setProgress(0);
                }
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
                mBox.setBackgroundColor(Color.HSVToColor(hsvColor));

                mTime.hour = i;
                if (i == maxHours) {
                    mTime.hour = 0;
                    mHourSeekBar.setProgress(0);
                }
                mClock.setText(mTime.toString());

                if(i > 6 && i <= 18)
                    mImg.setImageResource(R.drawable.sun);
                else
                    mImg.setImageResource(R.drawable.moon);

                updateAlpha(i);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });

        startAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Intent myIntent = new Intent(getActivity(), AlarmReceiver.class);
                    myIntent.putExtra("calendarSync", false);
                    // if pendingIntent already set, cancel it first before making this new one
                    mManualPendingIntent = PendingIntent.getBroadcast(MainActivity.mAlarmContext, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    MainActivity.mAlarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), mManualPendingIntent);
                    Toast.makeText(getActivity(), "Alarm set for " + mTime + " for " + day, Toast.LENGTH_SHORT).show();
                    mSharedPrefs = getActivity().getSharedPreferences("calendarPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSharedPrefs.edit();
                    editor.putLong("Alarm", calendar.getTimeInMillis());
                    editor.apply();
                }
                else if (mManualPendingIntent != null) {
                    MainActivity.mAlarmManager.cancel(mManualPendingIntent);
                    mManualPendingIntent.cancel();
                    Utility.clearNotification(view.getContext(), Utility.ALARM_NOTIFICATION_ID);
                }
            }
        });
    }

    public void updateColor(int i) {
        float hue = (float) Math.cos((2 * Math.PI * (float) i / maxHours));
        hsvColor[0] = 220 + 20f * hue;
        hsvColor[2] = ((1 - hue)*0.35f) + 0.2f;
    }

    public void updateAlpha(int i){
        int alpha = 120 + 80 * Math.round((float) Math.cos((4 * Math.PI * (float) i / maxHours)));
        mImg.setImageAlpha(alpha);
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
        boolean calendarSync = mSharedPrefs.getBoolean("calendarSync", false);
        startAlarmButton.setEnabled(!calendarSync);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
