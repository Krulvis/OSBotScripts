package api.util.antiban.delays;

import api.util.antiban.DelayHandler;

/**
 * Created by Tony on 31/05/2017.
 */
public class ResumeAutochatDelay extends DelayHandler{

    @Override
    protected double setMean() {
        return 7;
    }

    @Override
    protected double setDeviation() {
        return 5;
    }

    @Override
    protected double setMin() {
        return 2;
    }

    @Override
    protected double setMax() {
        return 60;
    }

    public static void execute(){
        DelayHandler delay = new ResumeAutochatDelay();
        delay.handle();
    }

}
