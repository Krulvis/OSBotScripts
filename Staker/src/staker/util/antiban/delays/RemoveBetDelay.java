package staker.util.antiban.delays;

import api.util.antiban.DelayHandler;

/**
 * Created by Tony on 31/05/2017.
 */
public class RemoveBetDelay extends DelayHandler{

    @Override
    protected double setMean() {
        return 2;
    }

    @Override
    protected double setDeviation() {
        return 1;
    }

    @Override
    protected double setMin() {
        return 0.1;
    }

    @Override
    protected double setMax() {
        return 5;
    }

    public static void execute(){
        DelayHandler delay = new RemoveBetDelay();
        delay.handle();
    }

}
