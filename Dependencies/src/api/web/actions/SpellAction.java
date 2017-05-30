package api.web.actions;


import api.ATMethodProvider;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.ui.MagicSpell;
import org.osbot.rs07.api.ui.Spells;

import java.beans.IntrospectionException;

public class SpellAction extends Action {

    private final MagicSpell spell;

    public SpellAction(ATMethodProvider mp, final MagicSpell spell) {
        super(mp);
        this.spell = spell;
    }

    public MagicSpell getSpell() {
        return spell;
    }

    public boolean canUse() {
        MagicSpell spell = getSpell();
        try {
            return magic.canCast(spell);
        } catch (InterruptedException e) {
            System.out.println("Something happened trying to cast spell");
        }
        return false;
    }

    @Override
    public boolean traverse() {
        return magic.castSpell(spell);
    }

    @Override
    public double getCost() {
        return 10;
    }

    @Override
    public String toString() {
        return "SpellAction: {" + getSpell() + "}";
    }
}