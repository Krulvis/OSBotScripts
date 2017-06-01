package staker.states;

import api.ATState;
import api.util.Random;
import api.wrappers.staking.calculator.SPlayer;
import org.osbot.rs07.api.ui.Skill;
import staker.Staker;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Starting extends ATState<Staker> {

    public Starting(Staker script) {
        super("Starting", script);
    }

    @Override
    public int perform() throws InterruptedException {
        if (script.myPlayer == null && client.isLoggedIn() && webAPI.isConnected()) {
            int[] sks = {skills.getStatic(Skill.ATTACK), skills.getStatic(Skill.STRENGTH), skills.getStatic(Skill.DEFENCE), skills.getStatic(Skill.HITPOINTS), skills.getStatic(Skill.PRAYER)};
            if (sks[0] > 0 && sks[1] > 0) {
                script.addStatusUpdater();
                script.getNewSettings();
                script.myPlayer = new SPlayer(myPlayer().getName(), sks);
                webAPI.sendAccountInfo(script.myPlayer);
            }
        }
        return Random.smallSleep();
    }

    @Override
    public boolean validate() {
        return !script.isScriptRunning.get() || script.myPlayer == null;
    }
}
