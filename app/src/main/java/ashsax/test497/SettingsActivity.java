package ashsax.test497;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.provider.CalendarContract.Calendars;
import android.widget.Toast;

import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class SettingsActivity extends ActionBarActivity {

    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff005fbf));

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.wakeup_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mSharedPrefs = getSharedPreferences("calendarPrefs", Context.MODE_PRIVATE);
        boolean calendarSync = mSharedPrefs.getBoolean("calendarSync", false);
        final Switch calendarSyncSwitch = (Switch) findViewById(R.id.calendarSyncSwitch);
        calendarSyncSwitch.setChecked(calendarSync);
        calendarSyncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                calendarSyncSwitch.setChecked(b);
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.putBoolean("calendarSync", b);
                editor.apply();
            }
        });

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

        Format df = DateFormat.getDateFormat(this);
        Format tf = DateFormat.getTimeFormat(this);

        if (cursor.getCount() == 0) {
            Toast.makeText(SettingsActivity.this, "No events tomorrow!", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = cursor.getString(TITLE_INDEX);
        Long start = cursor.getLong(DTSTART_INDEX);

        Toast.makeText(SettingsActivity.this, title + " on " + df.format(start) + " at " + tf.format(start), Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

}
