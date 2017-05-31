package api.util.antiban.delays;

import api.util.antiban.DelayHandler;

/**
 * Created by Tony on 31/05/2017.
 */
public class RelogDelay extends DelayHandler{

    @Override
    protected double setMean() {
        return 3.5;
    }

    @Override
    protected double setDeviation() {
        return 1.5;
    }

    @Override
    protected double setMin() {
        return 1;
    }

    @Override
    protected double setMax() {
        return 8;
    }

    public static void execute(){
        DelayHandler delay = new RelogDelay();
        delay.handle();
    }

}
