package ashsax.test497;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.text.Format;
import java.util.Calendar;

/**
 * Created by ash on 3/9/15.
 */
public class Utility {

    public static Long getAlarmTime(Context context) throws Exception {
        // begin querying calendar
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

        Cursor cursor = context.getContentResolver().query(uri, EVENT_PROJECTION, null, null, CalendarContract.Events.DTSTART + " ASC");
        cursor.moveToFirst();

        // if there are no events, say so and be done.
        if (cursor.getCount() == 0) {
            Toast.makeText(context, "No events tomorrow!", Toast.LENGTH_SHORT).show();
            throw new Exception();
        }

        // otherwise set alarm for tomorrow, based on spinner value corresponding to time needed to wake up
        String title = cursor.getString(TITLE_INDEX);
        Long start = cursor.getLong(DTSTART_INDEX);

        // fixes problem with recurring events
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(start);
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.DAY_OF_YEAR, tomorrow.get(Calendar.DAY_OF_YEAR));
        start = cal.getTimeInMillis();
        return start;
    }

}
