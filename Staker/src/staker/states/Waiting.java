package staker.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import api.wrappers.staking.calculator.SPlayer;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.utility.Condition;
import staker.Staker;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Waiting extends ATState<Staker> {

    public Waiting(Staker script) {
        super("Waiting", script);
    }

    public Timer logoutTimer;

    @Override
    public int perform() throws InterruptedException {
        if (!isLoggedIn()) {
            return Random.bigSleep();
        } else if (!webAPI.handleWebActions()) {
            return Random.smallSleep();
        }
        script.resetValues();
        if (script.myPlayer == null && client.isLoggedIn()) {
            script.myPlayer = new SPlayer(myPlayer().getName(), new int[]{skills.getStatic(Skill.ATTACK), skills.getStatic(Skill.STRENGTH), skills.getStatic(Skill.DEFENCE), skills.getStatic(Skill.HITPOINTS), skills.getStatic(Skill.PRAYER)});
        }
        if (script.startPos == null && client.isLoggedIn()) {
            script.startPos = myPosition();
        }
        if (logoutTimer == null) {
            logoutTimer = new Timer(Random.nextGaussian(30000, 240000, 20000));
        } else if (logoutTimer.isFinished()) {
            interact.moveMouseRandomly();
            logoutTimer = null;
        }
        RS2Widget tradeTab = chat.getTradeTab();
        if (tradeTab != null && tradeTab.getSpriteIndex1() == 1019) {
            RS2Widget trade = chat.getTradeTab();
            RectangleDestination target = trade != null ? new RectangleDestination(bot, trade.getBounds()) : null;
            if (target != null) {
                interact.interact(target);
            }
            sleep(300);
        }

        chat.setMessage(script.autoChatMessage, true);

        if (stake.acceptChallenge(script.maxDistance + 1, null)) {
            script.resetValues();
            if (script.trayMessage) {
                trayMessage(myPlayer().getName(), "Has a challenge!");
            }

            waitFor(2000, new Condition() {
                @Override
                public boolean evaluate() {
                    return stake.isFirstScreenOpen();
                }
            });
        }
        return Random.smallSleep();
    }

    @Override
    public boolean validate() {
        return true;
    }
}
