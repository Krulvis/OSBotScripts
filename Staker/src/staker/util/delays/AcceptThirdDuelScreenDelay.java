package staker.util.delays;

import api.util.antiban.DelayHandler;

/**
 * Created by Tony on 31/05/2017.
 */
public class AcceptThirdDuelScreenDelay extends DelayHandler{

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
        return 10;
    }

    public static void execute(){
        DelayHandler delay = new AcceptThirdDuelScreenDelay();
        delay.handle();
    }

}
