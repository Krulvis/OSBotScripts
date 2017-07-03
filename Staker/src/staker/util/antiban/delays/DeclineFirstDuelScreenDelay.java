package staker.util.antiban.delays;

import api.util.antiban.DelayHandler;

/**
 * Created by Tony on 31/05/2017.
 */
public class DeclineFirstDuelScreenDelay extends DelayHandler{

    @Override
    protected double setMean() {
        return 2;
    }

    @Override
    protected double setDeviation() {
        return 0.5;
    }

    @Override
    protected double setMin() {
        return 0.2;
    }

    protected double setMax(){
        return 10;
    }

    public static void execute(){
        DelayHandler delay = new DeclineFirstDuelScreenDelay();
        delay.handle();
    }

}
