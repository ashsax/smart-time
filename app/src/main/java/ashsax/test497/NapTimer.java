package ashsax.test497;

/**
 * Created by ash on 2/6/15.
 */
public class NapTimer {

    public int minutes;

    public NapTimer(){
        minutes = 0;
    }

    public NapTimer(int minutes_){
        minutes = minutes_;
    }

    private String getHrStr(){
        return String.valueOf(minutes/60);
    }
    private String getMinStr(){
        return String.valueOf(minutes%60);
    }

    public String toString(){
        String minuteStr = getMinStr();
        if(minutes%60 < 10)
            minuteStr = "0"+minuteStr;

        return getHrStr()+"h "+minuteStr+"m";
    }

    public String toastMsg(){
        String minStr = getMinStr() + " minutes";
        if(minutes < 60)
            return minStr;
        else if(minutes == 60)
            return getHrStr() + " hour";

        return getHrStr() + " hour and " + minStr;
    }

    public int getMinuteCount() {
        return minutes;
    }

    public int getMilliseconds() {
        return minutes*60*1000;
    }
}
