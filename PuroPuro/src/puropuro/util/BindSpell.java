package puropuro.util;

import api.ATMethodProvider;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.Skills;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.MagicSpell;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

/**
 * Created by Krulvis on 15-Mar-17.
 */
public enum BindSpell {
    NONE(0, null, 0),
    BIND(20, Spells.NormalSpells.BIND, 5500),
    SNARE(50, Spells.NormalSpells.SNARE, 10500),
    ENTANGLE(79, Spells.NormalSpells.ENTANGLE, 15000),;
    private int level;
    private MagicSpell spell;
    private int bindTime;

    BindSpell(int level, MagicSpell spell, int bindTime) {
        this.level = level;
        this.spell = spell;
        this.bindTime = bindTime;
    }

    public boolean bind(final ATMethodProvider mp, final NPC target) {
        final int magicXp = mp.skills.getExperience(Skill.MAGIC);
        if (this == NONE) {
            return true;
        }
        if (target == null) {
            return false;
        }
        int degree = ATMethodProvider.getDegree(mp.distance(target));
        if (mp.camera.getPitchAngle() > degree) {
            mp.camera.movePitch(degree);
        }
        if (!mp.magic.isSpellSelected()) {
            if (mp.magic.castSpell(spell)) {
                mp.waitFor(1000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return mp.magic.isSpellSelected();
                    }
                });
            }
        }
        if (mp.hasSpellSelected(spell)) {
            if (!target.isOnScreen()) {
                mp.camera.toEntity(target);
            }
            if (mp.interact(target, "Cast")) {
                System.out.println("casted bind!");
                mp.waitFor(1500, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return magicXp < mp.skills.getExperience(Skill.MAGIC);
                    }
                });
                return magicXp < mp.skills.getExperience(Skill.MAGIC);
            }
        }
        return false;
    }

    public int getLevel() {
        return level;
    }

    public boolean hasRequirements(ATMethodProvider mp) {
        return this == NONE || mp.canCast(spell);
    }

    public static BindSpell getBestSpell(ATMethodProvider mp) {
        int magicLevel = mp.skills.getDynamic(Skill.MAGIC);
        for (int i = values().length - 1; i >= 0; i--) {
            BindSpell cur = values()[i];
            if (magicLevel >= cur.level) {
                return cur;
            }
        }
        return null;
    }


    public MagicSpell getSpell(){return spell;}

    public int getBindTime() {
        return bindTime;
    }
}
