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

    public String toString(){
        String hourStr = String.valueOf(minutes/60);
        String minuteStr = String.valueOf(minutes%60);

        if(minutes%60 < 10)
            minuteStr = "0"+minuteStr;

        return hourStr+":"+minuteStr;
    }

    public int getMinuteCount() {
        return minutes;
    }

    public int getMilliseconds() {
        return minutes*60*1000;
    }
}
