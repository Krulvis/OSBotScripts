package api.util.statistics.Distributions;

import api.util.Random;

public class NormalDistribution {

    public static double generateWithMaxBoundary(double max, double mean, double sd, boolean castInt) {
        double rand;
        do {
            rand = (Random.gaussian() * sd + mean);
            if (castInt)
                rand = (int) rand;
        } while (rand >= max);
        return rand;
    }

    public static double generateWithMinBoundary(double min, double mean, double sd, boolean castInt) {
        double rand;
        do {
            rand = (Random.gaussian() * sd + mean);
            if (castInt)
                rand = (int) rand;
        } while (rand < min);
        return rand;
    }

    public static double generateWithBoundaries(double min, double max, double mean, double sd, boolean castInt) {
        double rand;
        do {
            rand = (Random.gaussian() * sd + mean);
            if (castInt)
                rand = (int) rand;
        } while (rand < min || rand >= max);
        return rand;
    }

    public static double generate(double mean, double sd, boolean castInt) {
        double rand = (Random.gaussian() * sd + mean);
        if (castInt)
            rand = (int) rand;
        return rand;
    }

}
