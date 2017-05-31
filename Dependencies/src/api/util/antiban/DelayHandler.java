package api.util.antiban;

import api.util.statistics.Distributions.NormalDistribution;

/**
 * Created by Arthur on 31-May-17.
 */
public abstract class DelayHandler {

    /* MEAN, DEVIATION, MIN & MAX in seconds */
    private double mean;
    private double sd;
    private double min;
    private double max;

    /* Determines wether or not the delay should be a decimal */
    private boolean castInt;

    abstract protected double setMean();

    abstract protected double setDeviation();

    public DelayHandler() {

        this.mean = this.setMean();
        this.sd = this.setDeviation();
        this.castInt = this.forceInt();
        this.min = this.setMin();
        this.max = this.setMax();
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

    public void handle() {
        double delay = NormalDistribution.generateWithBoundaries(this.min, this.max, this.mean, this.sd, this.castInt);
        try {
            Thread.sleep((long) delay * 1000);
        } catch (Exception e) {
        }

    }
}
