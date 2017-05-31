package api.util.antiban;

import api.util.Timer;
import api.util.statistics.Distributions.NormalDistribution;

/**
 * Created by Krulvis on 31-May-17.
 */
public abstract class TimerHandler extends RNGHandler {

    public Timer timer;

    public void handle() {
        this.timer = new Timer((long) (1000 * NormalDistribution.generateWithBoundaries(this.getMin(), this.getMax(), this.getMean(), this.getSd(), this.isCastInt())));
    }

    protected double setMin() {
        return 0;
    }

    protected double setMax() {
        return 1000000;
    }

    protected boolean forceInt() {
        return false;
    }
}
