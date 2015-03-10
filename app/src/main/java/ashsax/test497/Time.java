package ashsax.test497;

/**
 * Created by ash on 2/6/15.
 */
public class Time {

    public int hour;
    public int minute;
    private boolean am;

    public Time(){
        hour = 0;
        minute = 0;
        am = true;
    }

    public Time(int hour_, int minute_, boolean am_){
        hour = hour_;
        minute = minute_;
        am = am_;
    }

    public String toString(){
        int result = hour%12;
        String hourStr = String.valueOf(result);
        if(result == 0)
            hourStr = "12";

        if(hourStr.length() == 1)
            hourStr = "  " + hourStr;


        String ampm = " PM";
        if(hour <= 11)
            ampm = " AM";

        String timeStr = hourStr+":";
        if (minute < 10)
            timeStr+="0";

        return timeStr+String.valueOf(minute)+ampm;
    }

    public int getMinuteCount() {
        return hour*60 + minute;
    }

    public int getMilliseconds() {
        return hour*60*60*1000+minute*60*1000;
    }
}
