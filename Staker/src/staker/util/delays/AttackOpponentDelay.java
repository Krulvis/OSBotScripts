package staker.util.delays;

import api.util.antiban.DelayHandler;

/**
 * Created by Tony on 31/05/2017.
 */
public class AttackOpponentDelay extends DelayHandler{

    @Override
    protected double setMean() {
        return 0.2;
    }

    @Override
    protected double setDeviation() {
        return 0.5;
    }

    @Override
    protected double setMin() {
        return 0;
    }

    @Override
    protected double setMax() {
        return 0.8;
    }

    public static void execute(){
        DelayHandler delay = new AttackOpponentDelay();
        delay.handle();
    }

}
