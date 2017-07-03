package staker.states;

import api.ATState;
import api.util.Random;
import org.osbot.rs07.utility.Condition;
import staker.Staker;
import staker.util.antiban.delays.AcceptThirdDuelScreenDelay;

import static api.wrappers.staking.data.Data.ARENA_AREA;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Third extends ATState<Staker> {

    public Third(Staker script) {
        super("Third", script);
    }

    @Override
    public int perform() throws InterruptedException {
        if (script.ruleSet.allGood(this) && stake.shouldAccept(script.currentDuel.getMyExact(), script.currentDuel.getOtherExact())) {
            stake.hitOnSecond = random(10) <= 2;
            AcceptThirdDuelScreenDelay.execute();
            if (stake.acceptThirdScreen()) {
                waitFor(Random.nextGaussian(2000, 4000, 1000), new Condition() {
                    @Override
                    public boolean evaluate() {
                        return ARENA_AREA.contains(myPosition());
                    }
                });
            }
        } else {
            if (!script.ruleSet.allGood(this)) {
                script.currentDuel.setCancelReason("rule_scamming_3rd");
            } else if (stake.shouldAccept(script.currentDuel.getMyExact(), script.currentDuel.getOtherExact())) {
                script.currentDuel.setCancelReason("amount_scamming_3rd");
            } else {
                script.currentDuel.setCancelReason("unknown_decline_3rd");
            }

            if (stake.declineThird()) {
                System.out.println("Declined on third interface, shit was a scam");
                waitFor(2000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return !stake.isStakeScreenOpen();
                    }
                });
            }
        }
        return Random.smallSleep();
    }

    @Override
    public boolean validate() {
        return stake.isThirdScreenOpen();
    }
}
