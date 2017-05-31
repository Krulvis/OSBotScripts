package api.util.antiban.timers;

import api.util.Timer;
import api.util.antiban.DelayHandler;
import api.util.antiban.TimerHandler;

/**
 * Created by Tony on 31/05/2017.
 */
public class AutoChatMessageTimer extends TimerHandler {

    @Override
    protected double setMean() {
        return 10;
    }

    @Override
    protected double setDeviation() {
        return 5;
    }

    @Override
    protected double setMin() {
        return 5;
    }

    @Override
    protected double setMax() {
        return 25;
    }

    public static Timer getTimer() {
        TimerHandler timer = new AutoChatMessageTimer();
        return timer.timer;
    }

}
