package api.util.antiban;

import api.util.statistics.Distributions.NormalDistribution;

/**
 * Created by Arthur on 31-May-17.
 */
public abstract class DelayHandler extends RNGHandler {

    public void handle() {
        double delay = NormalDistribution.generateWithBoundaries(this.getMin(), this.getMax(), this.getMean(), this.getSd(), this.isCastInt());
        try {
            Thread.sleep((long) delay * 1000);
        } catch (Exception e) {
        }

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
