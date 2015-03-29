package ashsax.test497;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
 * {@link NapTimerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NapTimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NapTimerFragment extends Fragment {

    private TextView mClock;
    private Button startAlarmButton;
    private RelativeLayout mBox;
    private CircularSeekBar mNapSeekBar;
    private NapTimer mNapTimer;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    final float[] hsvColor = {240, 0.68f, 0.2f};
    private SharedPreferences mSharedPrefs;

    private final static int maxNap = 24;

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
     * @return A new instance of fragment NapTimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NapTimerFragment newInstance(String param1, String param2) {
        NapTimerFragment fragment = new NapTimerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NapTimerFragment() {
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
        return inflater.inflate(R.layout.fragment_nap_timer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBox = (RelativeLayout) getActivity().findViewById(R.id.relativeLayout2);
        mNapTimer = new NapTimer();
        mClock = (TextView) getActivity().findViewById(R.id.timer);
        startAlarmButton = (ToggleButton) getActivity().findViewById(R.id.startTimerButton);
        mNapSeekBar = (CircularSeekBar) getActivity().findViewById(R.id.napSeekBar);
        mNapSeekBar.setMax(maxNap);

        mImg = (ImageView) getActivity().findViewById(R.id.zzz);
        updateZZZ(0);

        Calendar cal = Calendar.getInstance();
        updateColor(cal.get(Calendar.HOUR_OF_DAY));
        mBox.setBackgroundColor(Color.HSVToColor(hsvColor));

        mNapSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                Calendar cal = Calendar.getInstance();
                updateColor(cal.get(Calendar.HOUR_OF_DAY));
                mBox.setBackgroundColor(Color.HSVToColor(hsvColor));

                updateZZZ(progress);

                mNapTimer.minutes = progress * 5;
                if (progress == maxNap) {
                    mNapSeekBar.setProgress(0);
                    mImg.setImageResource(R.drawable.z0);
                }
                mClock.setText(mNapTimer.toString());
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

                // set alarm based on user inputted time
                if (on) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MILLISECOND, mNapTimer.getMilliseconds());

                    Intent myIntent = new Intent(getActivity(), AlarmReceiver.class);
                    myIntent.putExtra("calendarSync", false);
                    // if pendingIntent already set, cancel it first before making this new one
                    pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                    Toast.makeText(getActivity(), "Alarm set for " + mNapTimer.getMinuteCount() + " minutes from now", Toast.LENGTH_SHORT).show();
                }
                else if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                    Utility.clearNotifications(view.getContext());
                }
            }
        });
    }

    public void updateColor(int i) {
        float hue = (float) Math.cos((2 * Math.PI * (float) i / maxNap));
        hsvColor[0] = 220 + 20f * hue;
        hsvColor[2] = ((1 - hue)*0.35f) + 0.2f;
    }

    public void updateZZZ(int i){
        Resources res = getResources();
        int resource = res.getIdentifier("z" + i, "drawable", getActivity().getApplicationContext().getPackageName());
        mImg.setImageResource(resource);
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
