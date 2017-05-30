package api.util.requirements;

import api.ATMethodProvider;
import org.osbot.rs07.api.ui.Skill;

/**
 * Created by Krulvis on 11-9-2015.
 */
public class EquipRequirement extends Requirement {

    private Skill skill = null;
    private ATMethodProvider mp;
    private int level = -1, id;

    public EquipRequirement(ATMethodProvider mp, Skill skill, int level, int id) {
        super(mp);
        this.skill = skill;
        this.level = level;
        this.id = id;
    }

    public EquipRequirement(ATMethodProvider mp, int id) {
        super(mp);
        this.id = id;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public int getLevel() {
        return this.level;
    }

    public int getItemId() {
        return this.id;
    }

    public boolean hasSkillRequirement() {
        return getSkill() == null || mp.skills.getDynamic(skill) >= getLevel();
    }

    @Override
    public boolean hasRequirement() {
        if (!hasSkillRequirement()) {
            return false;
        }
        return mp.equipment.getItem(getItemId()) != null;
    }

    public boolean hasItemInInventory() {
        return mp.inventory.contains(getItemId());
    }

    @Override
    public String toString() {
        String skillReq = getSkill() == null ? "" : getSkill().name() + ": " + getLevel();
        return skillReq + ", ItemId: " + getItemId();
    }
}
