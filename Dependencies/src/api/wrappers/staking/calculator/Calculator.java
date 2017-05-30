package api.wrappers.staking.calculator;

import java.util.Random;

/**
 * Created by Krulvis on 11-1-2016.
 */
public class Calculator {

    private SPlayer p1;
    private SPlayer p2;
    boolean useSpecial;
    String name;
    private int simulations;
    public static Random rand = new Random();
    private int srand;
    private double odds = 0;

    public Calculator(SPlayer p1, SPlayer p2, boolean useSpecial, String name, int simulations) {
        p1.wins = 0;
        p2.wins = 0;
        this.p1 = p1;
        this.p2 = p2;
        this.useSpecial = useSpecial;
        this.name = name;
        this.simulations = simulations;
        this.odds = 0;
        srand = Math.abs((int) System.currentTimeMillis());
        System.out.println("RandomSeed: " + srand);
    }

    public double simulate() {
        int sims = this.simulations;
        if (sims > 0) {
            while (sims > 0) {
                p1.reset(useSpecial);
                p2.reset(useSpecial);
                if (name.equalsIgnoreCase("player1")) {
                    p1.setTurn(true);
                } else if (name.equalsIgnoreCase("player2")) {
                    p2.setTurn(true);
                }
                DUEL:
                while (!p2.isDead()) {
                    if (p1.isTurn()) {
                        attack(p1, p2);
                    } else {
                        attack(p2, p1);
                    }
                    if (p1.isDead()) {
                        break DUEL;
                    }
                }
                //System.out.println("After fight check " + (simulations - sims));
                //System.out.println("Specs used: " + p1.specsUsed);
                //System.out.println("....................");
                sims--;
            }
        }
        return this.odds = ((((double) p1.wins) / ((double) simulations)) * 100.0);
    }

    public void attack(SPlayer pOffense, SPlayer pDefense) {
        if (pOffense.hitsTankWeapon > 0) {
            if (rollHit(pOffense, pDefense, "tank_on_spec")) {
                int curr = pDefense.currentHp;
                int dmg = calcDmg(pOffense.TMSR);
                pDefense.setCurrentHp(curr - dmg);
            }
            pOffense.hitsTankWeapon--;
        } else {
            if (useSpecial && pDefense.specsToTank > 0 && pOffense.specsUsed < 4) {
                if (rollHit(pOffense, pDefense, "spec_on_tank")) {
                    int curr = pDefense.currentHp;
                    int dmg = calcDmg(pOffense.SMSR);
                    pDefense.setCurrentHp(curr - dmg);
                }
                if (rollHit(pOffense, pDefense, "spec_on_tank")) {
                    int curr = pDefense.currentHp;
                    int dmg = calcDmg(pOffense.SMSR);
                    pDefense.setCurrentHp(curr - dmg);
                }
                pOffense.specsUsed++;
                pDefense.hitsTankWeapon++;
                pDefense.specsToTank--;
            } else if (useSpecial && pOffense.specsUsed < 4) {
                if (rollHit(pOffense, pDefense, "spec_on_spec")) {
                    int curr = pDefense.currentHp;
                    int dmg = calcDmg(pOffense.SMSR);
                    pDefense.setCurrentHp(curr - dmg);
                }
                if (rollHit(pOffense, pDefense, "spec_on_spec")) {
                    int curr = pDefense.currentHp;
                    int dmg = calcDmg(pOffense.SMSR);
                    pDefense.setCurrentHp(curr - dmg);
                }
                pOffense.specsUsed++;
            } else if (rollHit(pOffense, pDefense, "main_on_main")) {
                int curr = pDefense.currentHp;
                int dmg = calcDmg(pOffense.MMSR);
                pDefense.setCurrentHp(curr - dmg);
            }
        }
        checkDeath(pOffense, pDefense);
    }

    public void checkDeath(SPlayer pOffense, SPlayer pDefense) {
        if (pDefense.currentHp <= 0) {
            pOffense.addWin();
            pDefense.setDead(true);
        } else {
            pOffense.setTurn(false);
            pDefense.setTurn(true);
        }
    }

    public int calcDmg(int mh) {
        return (rand.nextInt(srand) % (mh + 1));
    }

    public boolean rollHit(SPlayer pOffense, SPlayer pDefense, String attack) {
        int offense = 0;
        int defense = 0;
        switch (attack) {
            case "tank_on_spec":
                offense = rand.nextInt(srand) % pOffense.TMAR;
                defense = rand.nextInt(srand) % pDefense.SMDR;
                if (offense == defense) {
                    while (offense == defense) {
                        offense = rand.nextInt(srand) % pOffense.TMAR;
                        defense = rand.nextInt(srand) % pDefense.SMDR;
                    }
                }
                break;
            case "spec_on_tank":
                offense = rand.nextInt(srand) % pOffense.SMAR;
                defense = rand.nextInt(srand) % pDefense.TMDR;
                if (offense == defense) {
                    while (offense == defense) {
                        offense = rand.nextInt(srand) % pOffense.SMAR;
                        defense = rand.nextInt(srand) % pDefense.TMDR;
                    }
                }
                break;
            case "spec_on_spec":
                offense = rand.nextInt(srand) % pOffense.SMAR;
                defense = rand.nextInt(srand) % pDefense.SMDR;
                if (offense == defense) {
                    while (offense == defense) {
                        offense = rand.nextInt(srand) % pOffense.SMAR;
                        defense = rand.nextInt(srand) % pDefense.SMDR;
                    }
                }
                break;
            case "main_on_main":
                offense = rand.nextInt(srand) % pOffense.MMAR;
                defense = rand.nextInt(srand) % pDefense.MMDR;
                if (offense == defense) {
                    while (offense == defense) {
                        offense = rand.nextInt(srand) % pOffense.MMAR;
                        defense = rand.nextInt(srand) % pDefense.MMDR;
                    }
                }
                break;
        }
        return offense > defense;
    }
}
