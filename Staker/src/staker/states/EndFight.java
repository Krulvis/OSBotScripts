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
            final int wons = script.currentDuel.getOtherExact();
            rechallenge(wc, oldChallenge);
            if (!validateWidget(VICTORY_INTERFACE)) {
                //sendResults(true, true);
                script.totalGains += wons;
            }
        } else if (lost) {
            sleep(Random.nextGaussian(5000, 7500, 6000, 3000));
            System.out.println("Lost!");
            final RS2Widget wc = getWidgetChild(LOST_INTERFACE, new TextFilter("Close"));
            final int loses = script.currentDuel.getMyRoundedMultiplied();
            rechallenge(wc, oldChallenge);
            if (!validateWidget(LOST_INTERFACE)) {
                //sendResults(false, true);
                script.totalLosses += loses;
            }
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

    /*public static void sendResults(boolean won, boolean finished) {
        if (WebAPI.isConnected()) {
            //convert naar JSON before sending dis shit
            JsonNull nul = JsonNull.INSTANCE;
            JsonObject object = new JsonObject();
            object.add("opponent_rsn", new JsonPrimitive(Staker.opponentName));
            object.add("won", finished ? new JsonPrimitive(won) : new JsonPrimitive(false));
            object.add("bot_stake_value", new JsonPrimitive(Second.myRoundedMultiplied > 0 ? Second.myRoundedMultiplied : 0));
            object.add("opponent_stake_value", new JsonPrimitive(Second.otherExact > 0 ? Second.otherExact : 0));
            JsonArray bet_items = new JsonArray();
            Item[] items = Second.otherItems;
            if (items != null && items.length > 0) {
                for (Item i : items) {
                    if (i != null && i.getID() > 0) {
                        bet_items.add(i);
                    }
                }
            }
            object.add("opponent_stake_items", bet_items);
            object.add("duration", new JsonPrimitive(Fight.fightTimer != null ?  (int)(Fight.fightTimer.getElapsedTime() / 1000) : 0));
            Calculator calc = Staker.calcMap.get(Staker.opponentName);
            double mult = calc != null ? calc.getMultiplier(StakeSettings.increasedModifier) : 1;
            mult = mult >= 100 ? 1 : mult;
            StakeSettings.increasedModifier = StakeSettings.increasedModifier > StakeSettings.modifier ? StakeSettings.increasedModifier : StakeSettings.modifier;
            object.add("npid_odds", calc != null ? new JsonPrimitive(calc.getNoPidOdds()) : nul);
            object.add("pid_odds", calc != null ? new JsonPrimitive(calc.getPidOdds()) : nul);
            object.add("rnd_odds", calc != null ? new JsonPrimitive(calc.getRandomOdds()) : nul);
            object.add("returnPercent", calc != null ? new JsonPrimitive(mult) : nul);
            object.add("finished", new JsonPrimitive(finished));
            JsonElement response = WebAPI.getWebConnection().sendJSON("bot/duel", "POST", object);
            WebAPI.sendInventoryScreenshot();
            StakeSettings.sendInventoryValue();
            System.out.println("Send stake results: " + response);
        }
    }*/

    @Override
    public boolean validate() {
        if (stake.isFirstScreenOpen()) {
            System.out.println("Already back in stakescreen");
            challengeAgain = null;
            return false;
        }
        won = validateWidget(VICTORY_INTERFACE);
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
        return false;
    }
}
