package api.wrappers;

import api.ATMethodProvider;
import org.osbot.rs07.api.model.Character;

/**
 * Created by Krulvis on 29-May-17.
 */
public class ATCombat extends ATMethodProvider {

    public ATCombat(ATMethodProvider parent) {
        init(parent);
    }

    public boolean isAttacking() {
        Character character = myPlayer().getInteracting();
        if (character == null) {
            return false;
        }
        return character.hasAction("Attack");
    }

    public int getSpecialAttack() {
        return getCombat().getSpecialPercentage();
    }
}
