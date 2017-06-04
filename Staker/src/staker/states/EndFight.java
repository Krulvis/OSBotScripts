package staker.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import api.util.filter.TextFilter;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.utility.Condition;
import staker.Staker;

import static api.wrappers.staking.data.Data.DUEL_INTERFACE_1;
import static api.wrappers.staking.data.Data.LOST_INTERFACE;
import static api.wrappers.staking.data.Data.VICTORY_INTERFACE;

/**
 * Created by s120619 on 28-4-2017.
 */
public class EndFight extends ATState<Staker> {

    public Timer challengeAgain;

    public boolean won = false;
    public boolean lost = false;

    public EndFight(Staker script) {
        super("End fight", script);
    }

    @Override
    public int perform() throws InterruptedException {
        RS2Widget[] wcs = widgets.get(162, 43).getChildWidgets();
        RS2Widget oldChallenge = null;
        if (wcs != null && wcs.length > 0) {
            for (RS2Widget wc : wcs) {
                if (wc != null && wc.getMessage() != null && wc.isVisible() && wc.getAbsY() > 340) {
                    if (stake.isChallenge(wc.getMessage())) {
                        oldChallenge = wc;
                        break;
                    }
                }
            }
        }
        openInventory();
        if (won) {
            sleep(Random.nextGaussian(5000, 7500, 6000, 3000));
            System.out.println("Won!");
            final RS2Widget wc = getWidgetChild(VICTORY_INTERFACE, new TextFilter("Claim!"));
            rechallenge(wc, oldChallenge);
        } else if (lost) {
            sleep(Random.nextGaussian(5000, 7500, 6000, 3000));
            System.out.println("Lost!");
            final RS2Widget wc = getWidgetChild(LOST_INTERFACE, new TextFilter("Close"));
            rechallenge(wc, oldChallenge);
        } else if (oldChallenge == null && challengeAgain.getElapsedTime() > 10000) {
            challengeAgain = null;
        }
        return Random.smallSleep();
    }

    private void rechallenge(RS2Widget wc, RS2Widget oldChallenge) {
        if (oldChallenge != null && clickRechallenge(oldChallenge) && isMoving()) {
            waitFor(15000, new Condition() {
                @Override
                public boolean evaluate() {
                    return validateWidget(DUEL_INTERFACE_1);
                }
            });
        } else if (wc != null && wc.isVisible() && wc.interact()) {
            waitFor(2000, new Condition() {
                @Override
                public boolean evaluate() {
                    return !validateWidget(LOST_INTERFACE) && !validateWidget(VICTORY_INTERFACE);
                }
            });
        }
    }

    private boolean clickRechallenge(final RS2Widget wc) {
        if (wc != null && wc.interact()) {
            System.out.println("Rechallenging");
            waitFor(1000, new Condition() {
                @Override
                public boolean evaluate() {
                    return isMoving();
                }
            });
            challengeAgain = new Timer(30000);
        }
        return isMoving();
    }

    @Override
    public boolean validate() {
        if (stake.isFirstScreenOpen()) {
            System.out.println("Already back in stakescreen");
            challengeAgain = null;
            return false;
        } else {
            lost = validateWidget(LOST_INTERFACE);
            if (won) {
                this.name = "Accept Win";
                return true;
            } else if (lost) {
                this.name = "Accept Defeat";
                return true;
            } else if (challengeAgain != null && !challengeAgain.isFinished()) {
                this.name = "Re-challenge";
                return true;
            }
        }
        return false;
    }
}
