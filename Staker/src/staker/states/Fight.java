package staker.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import org.osbot.rs07.api.filter.NameFilter;
import org.osbot.rs07.api.model.Player;
import staker.Staker;

import static api.wrappers.staking.data.Data.ARENA_AREA;

/**
 * Created by s120619 on 28-4-2017.
 */
public class Fight extends ATState<Staker> {

    public Fight(Staker script) {
        super("Fight", script);
    }

    public Timer startFightTimer;
    private Timer glTimer;
    private String[] gls = new String[]{"gl", "glgl", "gl mate", "good luck"};

    @Override
    public int perform() throws InterruptedException {
        String ohText = myPlayer().getHeadMessage();
        script.endFight.challengeAgain = new Timer(30000);
        if (ohText != null && ohText.contains("3") && glTimer == null) {
            glTimer = new Timer(5000);
        }
        if (glTimer != null && glTimer.isFinished()) {
            if (random(10) > 3) {
                keyboard.typeString(gls[random(0, 3)], true);
                glTimer = null;
            }
        }
        if (script.currentDuel != null) {
            Player opp = getPlayer(script.currentDuel.getPlayerName());
            if (opp != null) {
                if (opp.isHitBarVisible() && opp.getHealthPercent() == 0) {
                    startFightTimer.stop();
                    openInventory();
                } else if (stake.fight(opp, script.ruleSet)) {

                }
            }
        }
        return Random.smallSleep();
    }

    @Override
    public boolean validate() {
        return ARENA_AREA.contains(myPosition());
    }
}
