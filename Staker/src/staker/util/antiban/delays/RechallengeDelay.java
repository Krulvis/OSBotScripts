package staker.util.antiban.delays;

import api.util.antiban.DelayHandler;

/**
 * Created by Tony on 31/05/2017.
 */
public class RechallengeDelay extends DelayHandler{

    @Override
    protected double setMean() {
        return 1.5;
    }

    @Override
    protected double setDeviation() {
        return 1;
    }

    @Override
    protected double setMin() {
        return 0.5;
    }

    @Override
    protected double setMax() {
        return 8;
    }

    public static void execute(){
        DelayHandler delay = new RechallengeDelay();
        delay.handle();
    }

}
