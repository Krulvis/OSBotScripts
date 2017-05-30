package api.wrappers.staking.calculator;

import api.wrappers.staking.data.RuleSet;
import api.wrappers.staking.data.Settings;

/**
 * Created by Krulvis on 11-1-2016.
 */
public class Odds {

    private double randomOdds = 0, pidOdds, noPidOdds, multiplier;
    private Settings.Weapon weapon;
    private Settings.Style style;
    private RuleSet set;
    public boolean calculated = false;

    public SPlayer me, opponent;

    public Odds(SPlayer p1, SPlayer p2, RuleSet set) {
        this.me = p1;
        this.opponent = p2;
        this.set = set;
        this.weapon = set != null ? set.getWeapon() : Settings.Weapon.VINE_WHIP;
        this.style = set != null ? set.getStyle() : Settings.Style.CONTROLLED;
        this.set = set;
        calculate();
    }

    public void calculate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                me.calcStats(set);
                opponent.calcStats(set);

                boolean special = set == RuleSet.DDS;

                Calculator calculator = new Calculator(me, opponent, special, "player1", 100000);
                double odds = calculator.simulate();

                Calculator calculator2 = new Calculator(me, opponent, special, "player2", 100000);
                double odds2 = calculator2.simulate();

                double finalOdds = (odds + odds2) / 2;
                pidOdds = (double) ((int) (odds * 10)) / 10.0D;
                noPidOdds = (double) ((int) (odds2 * 10)) / 10.0D;
                randomOdds = (double) ((int) (finalOdds * 10)) / 10.0D;
                calculated = true;
            }
        }, "Calculate odds thread").start();

    }

    public double getRandomOdds() {
        return randomOdds;
    }

    public double getPidOdds() {
        return pidOdds;
    }

    public double getNoPidOdds() {
        return noPidOdds;
    }

    public Settings.Weapon getWeapon() {
        return weapon;
    }

    public Settings.Style getStyle() {
        return style;
    }

    public double getMultiplier(double returnRate) {

        double winLosses = randomOdds / (100D - randomOdds);
        double xing = returnRate / (100D - returnRate);

        return Math.round(winLosses / xing * 100D) / 100D;
    }

    public static void main(String... args) {
        double odds = 53.7D;
        int returnRate = 55;

        double winLosses = odds / (100D - odds);
        double xing = returnRate / (100D - returnRate);
        System.out.println("WinLosses: " + winLosses + ", xing: " + xing);

        double multiply = Math.round(winLosses / xing * 100D) / 100D;
        System.out.println("Multiply: " + multiply);

    }

    public RuleSet getSet() {
        return set;
    }

    public boolean isCalculated() {
        return calculated;
    }
}
