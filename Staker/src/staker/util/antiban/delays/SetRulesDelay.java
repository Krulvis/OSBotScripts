package staker.util.antiban.delays;

import api.util.antiban.DelayHandler;

/**
 * Created by Tony on 31/05/2017.
 */
public class SetRulesDelay extends DelayHandler{

    @Override
    protected double setMean() {
        return 10;
    }

    @Override
    protected double setDeviation() {
        return 1.5;
    }

    @Override
    protected double setMin() {
        return 0.5;
    }

    @Override
    protected double setMax() {
        return 40;
    }

    public static void execute(){
        DelayHandler delay = new SetRulesDelay();
        delay.handle();
    }

}
