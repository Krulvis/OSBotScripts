package staker.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import api.wrappers.staking.Duel;
import api.wrappers.staking.calculator.Odds;
import api.wrappers.staking.calculator.SPlayer;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.utility.Condition;
import staker.Staker;
import staker.util.antiban.delays.AcceptFirstDuelScreenDelay;

/**
 * Created by Krulvis on 29-May-17.
 */
public class First extends ATState<Staker> {

    public First(Staker script) {
        super("First", script);
    }

    @Override
    public int perform() throws InterruptedException {
        if (script.currentDuel == null) {
            String opponent = stake.getOtherName1();
            if (opponent != null && skills != null) {
                Duel previous = script.getPreviousDuel(opponent);
                if (previous != null) {
                    script.currentDuel = previous;
                    script.currentDuel.resetVars();
                } else {
                    int[] skills = stake.getOtherSkills();
                    SPlayer player = new SPlayer(opponent, skills);
                    Odds odds = new Odds(script.myPlayer, player, script.ruleSet);
                    script.currentDuel = new Duel(script.myPlayer, player, odds, script.returnPercent, 0, 0, 0);
                }
            }
        } else if (script.currentDuel.isFinished()) {
            //Came here directly after rechallenging
            script.resetValues();
        } else {
            script.currentDuel.checkTimer(script.declineTime);
            Player player = getPlayer(script.currentDuel.getPlayerName());
            if (player != null && script.currentDuel.getOpponent() != null && Math.abs(player.getCombatLevel() - script.currentDuel.getOpponent().getCombatLevel()) > 2) {
                System.out.println("COMBAT LEVEL DIFFERS! CALCED: " + script.currentDuel.getOpponent().getCombatLevel() + ", REAL: " + player.getCombatLevel());
            }
            if (script.currentDuel.isOddsCalculated()) {
                double odds = script.currentDuel.getOdds().getRandomOdds();
                if (!script.debug && odds <= 30 && odds > 0.0) {
                    System.out.println("Declining duel, odds: " + odds + " too " + "Low");
                    //TODO Make speciel timer for declining
                    script.currentDuel.setCancelReason("odds_too_low_1st");
                    if (stake.declineFirst()) {
                        waitFor(2000, new Condition() {
                            @Override
                            public boolean evaluate() {
                                return !stake.isStakeScreenOpen();
                            }
                        });
                    }
                } else if (script.currentDuel.shouldDecline()) {
                    log("Declined challenge, opponent taking too long.");
                    //TODO Make speciel timer for declining
                    script.currentDuel.setCancelReason("took_too_long_1st");
                    if (stake.declineFirst()) {
                        waitFor(2000, new Condition() {
                            @Override
                            public boolean evaluate() {
                                return !stake.isStakeScreenOpen();
                            }
                        });
                    }
                } else if (script.ruleSet.allGood(this)) {
                    AcceptFirstDuelScreenDelay.execute();
                    if (!stake.isFirstScreenAccepted() && stake.acceptFirstScreen()) {
                        waitFor(random(2000, 5000), new Condition() {
                            @Override
                            public boolean evaluate() {
                                return stake.isSecondScreenOpen();
                            }
                        });
                        if (stake.isSecondScreenOpen()) {
                            script.currentDuel.declineTimer = new Timer(script.declineTime + Random.nextGaussian(15000, 25000, 5000));
                        }
                    }
                } else if (stake.loadPreset()) {
                    waitFor(5000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return script.ruleSet.allGood(script);
                        }
                    });
                }
            }
        }
        return Random.smallSleep();
    }

    @Override
    public boolean validate() {
        return stake.isFirstScreenOpen();
    }
}
