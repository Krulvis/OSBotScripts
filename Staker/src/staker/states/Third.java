package staker.states;

import api.ATState;
import api.util.Random;
import org.osbot.rs07.utility.Condition;
import staker.Staker;

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
            if (stake.acceptThirdScreen()) {
                waitFor(10000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return ARENA_AREA.contains(myPosition());
                    }
                });
            }
        } else if (stake.declineThird()) {
            log("Declined on third interface, shit was a scam");
            waitFor(2000, new Condition() {
                @Override
                public boolean evaluate() {
                    return !stake.isStakeScreenOpen();
                }
            });
        }
        return Random.smallSleep();
    }

    @Override
    public boolean validate() {
        return stake.isThirdScreenOpen();
    }
}
