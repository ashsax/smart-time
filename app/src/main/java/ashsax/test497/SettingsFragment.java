package ashsax.test497;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SharedPreferences mCalendarPrefs;
    private SharedPreferences mTimePrefs;
    private SharedPreferences mSnoozePrefs;
    private SharedPreferences mAlarmTypePrefs;

    private String[] alarmSpinner;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.wakeup_times, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        alarmSpinner = new String[] {
                "1", "2", "3", "4", "5"
        };

        Spinner alarmSpinner = (Spinner) getActivity().findViewById(R.id.alarmTypeSpinner);
        List<CharSequence> planets = new ArrayList<CharSequence>();
        ArrayAdapter<CharSequence> adapter= new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, planets);

        // GET RINGTONES AND ADD TO LIST
        RingtoneManager ringtoneManager = new RingtoneManager(getActivity());
//        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
//        Cursor ringtones = ringtoneManager.getCursor();
//        String[] cols = ringtones.getColumnNames();
//        Toast.makeText(getActivity(), "" + ringtones.getCount() , Toast.LENGTH_SHORT).show();

        adapter.add("Alarm1");
        adapter.add("Alarm2");
        adapter.notifyDataSetChanged();
        alarmSpinner.setAdapter(adapter);


        mSnoozePrefs = getActivity().getSharedPreferences("snoozePrefs", Context.MODE_PRIVATE);
        final EditText snoozeText = (EditText) getActivity().findViewById(R.id.snoozeLength);
        snoozeText.setText("" + mSnoozePrefs.getInt("snoozeTime", 5));
        snoozeText.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                String snooze = snoozeText.getText().toString();
                if(snooze.trim().length() == 0) {
                    snooze = "5";
                }

                SharedPreferences.Editor editor = mSnoozePrefs.edit();
                editor.putInt("snoozeTime", Integer.parseInt(snooze));
                editor.apply();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        mTimePrefs = getActivity().getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
        final EditText editText = (EditText) getActivity().findViewById(R.id.editText);
        editText.setText("" + mTimePrefs.getInt("timeNeededToGetReady", 30));

        mCalendarPrefs = getActivity().getSharedPreferences("calendarPrefs", Context.MODE_PRIVATE);
        final boolean calendarSync = mCalendarPrefs.getBoolean("calendarSync", false);
        final Switch calendarSyncSwitch = (Switch) getActivity().findViewById(R.id.calendarSyncSwitch);
        calendarSyncSwitch.setChecked(calendarSync);
        calendarSyncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = mCalendarPrefs.edit();
                editor.putBoolean("calendarSync", b);
                editor.apply();
                AlarmFragment.startAlarmButton.setEnabled(!b);
                // if calendarSync is switched off, then cancel the calendar alarm previously set
                if (!b) {
                    if (AlarmFragment.mCalendarPendingIntent != null) {
                        MainActivity.mAlarmManager.cancel(AlarmFragment.mCalendarPendingIntent);
                        AlarmFragment.mCalendarPendingIntent.cancel();
                    }
                    Toast.makeText(getActivity(), "Alarm previously set by calendar cancelled", Toast.LENGTH_SHORT).show();
                }
                else {
                    // cancel previous calendar alarm
                    Intent myIntent = new Intent(getActivity(), AlarmReceiver.class);
                    myIntent.putExtra("calendarSync", true);
                    AlarmFragment.mCalendarPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (AlarmFragment.mCalendarPendingIntent != null) {
                        MainActivity.mAlarmManager.cancel(AlarmFragment.mCalendarPendingIntent);
                        AlarmFragment.mCalendarPendingIntent.cancel();
                    }

                    Long start;
                    try {
                        start = Utility.getAlarmTime(getActivity());
                    }
                    catch (Exception e) {
                        return;
                    }

                    myIntent = new Intent(getActivity(), AlarmReceiver.class);
                    myIntent.putExtra("calendarSync", true);
                    // if pendingIntent already set, cancel it first before making this new one
                    AlarmFragment.mCalendarPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // time needed to wake up in milliseconds
                    editor = mTimePrefs.edit();
                    String minutesNeeded = editText.getText().toString();
                    Log.v("SettingsFragment", minutesNeeded);
                    editor.putInt("timeNeededToGetReady", Integer.parseInt(minutesNeeded));
                    editor.apply();
                    int timeNeededToWakeUp = Integer.parseInt( editText.getText().toString() ) * 60 * 1000;
                    Long alarmTime = start - timeNeededToWakeUp;

                    String day = "tomorrow";
                    long startOfTomorrow = Utility.getStartOfTomorrow().getTimeInMillis();
                    if(alarmTime < startOfTomorrow)
                        day = "today";

                    if(alarmTime <= Calendar.getInstance().getTimeInMillis()){
                        Toast.makeText(getActivity(), "Cannot set alarm for the past", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MainActivity.mAlarmManager.set(AlarmManager.RTC, alarmTime, AlarmFragment.mCalendarPendingIntent);

                    Format df = DateFormat.getDateFormat(getActivity());
                    Format tf = DateFormat.getTimeFormat(getActivity());

                    Toast.makeText(getActivity(), "Alarm set for " + tf.format(alarmTime) + " for " + day, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(SettingsActivity.this, "Alarm set for "+ (alarmTime - Calendar.getInstance().getTimeInMillis()), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(SettingsActivity.this, "Alarm set for " + df.format(alarmTime) + " at " + tf.format(alarmTime), Toast.LENGTH_SHORT).show();

//                    Toast.makeText(SettingsActivity.this, title + " on " + df.format(start) + " at " + tf.format(start), Toast.LENGTH_SHORT).show();
                    ToggleButton toggleButton = (ToggleButton) AlarmFragment.startAlarmButton;
                    toggleButton.setChecked(false);
                }
            }
        });
    }

    public static int getSnoozeTime(Context context){
        return context.
                getSharedPreferences("snoozePrefs", Context.MODE_PRIVATE).
                getInt("snoozeTime", 5);
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
