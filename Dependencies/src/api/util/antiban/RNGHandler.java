package api.util.antiban;

import api.util.statistics.Distributions.NormalDistribution;

/**
 * Created by Tony on 31/05/2017.
 */
abstract public class RNGHandler {
    /* MEAN, DEVIATION, MIN & MAX in seconds */
    private double mean;
    private double sd;
    private double min;
    private double max;

    /* Determines wether or not the delay should be a decimal */
    private boolean castInt;

    abstract protected double setMean();
    abstract protected double setDeviation();
    abstract protected double setMin();
    abstract protected double setMax();
    abstract protected boolean forceInt();
    abstract protected void handle();

    public RNGHandler() {

        this.mean = this.setMean();
        this.sd = this.setDeviation();
        this.castInt = this.forceInt();
        this.min = this.setMin();
        this.max = this.setMax();
    }

    public double getMean() {
        return mean;
    }

    public double getSd() {
        return sd;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public boolean isCastInt() {
        return castInt;
    }
}
