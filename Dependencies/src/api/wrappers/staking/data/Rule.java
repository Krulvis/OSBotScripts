package api.wrappers.staking.data;

import api.ATMethodProvider;

import java.util.ArrayList;

import static api.wrappers.staking.data.Data.*;

/**
 * Created by Krulvis on 30-6-2014.
 */
public enum Rule {
    //8192
    //LEFT COLUMN
    NO_RANGED(RANGED, 0x10),
    NO_MELEE(MELEE, 0x20),
    NO_MAGIC(MAGIC, 0x40),
    NO_SP_ATTACK(SPECIAL, 0x2000),
    FUN_WEAPONS(FUN_WEPS, 0x1000),
    NO_FORFEIT(FORFEIT, 0x1),
    //RIGHT COLUMN
    NO_DRINKS(DRINKS, 0x80),
    NO_FOOD(FOOD, 0x100),
    NO_PRAYER(PRAYER, 0x200),
    NO_MOVEMENT(MOVEMENT, 0x2),
    HAVE_OBSTACLES(OBSTACLES, 0x400),
    //EQUIPMENT
    NO_HELMET(HEAD, 0x4000),
    NO_CAPE(CAPE, 0x8000),
    NO_AMULET(NECK, 0x10000),
    NO_AMMO(AMMO, 0x8000000),
    NO_WEAPON(WEAPON, 0x20000),
    NO_BODY(CHEST, 0x40000),
    NO_SHIELD(SHIELD, 0x80000),
    NO_LEGS(LEGS, 0x200000),
    NO_GLOVES(HANDS, 0x800000),
    NO_BOOTS(FEET, 0x1000000),
    NO_RING(RING, 0x4000000),;

    public static Rule[] SETTINGS = {
            NO_RANGED,
            NO_MELEE,
            NO_MAGIC,
            NO_SP_ATTACK,
            FUN_WEAPONS,
            NO_FORFEIT,
            NO_DRINKS,
            NO_FOOD,
            NO_PRAYER,
            NO_MOVEMENT,
            HAVE_OBSTACLES,
    };

    public static Rule[] DDSING = {
            NO_RANGED,
//		NO_MELEE,
            NO_MAGIC,
//		NO_SP_ATTACK,
//		FUN_WEAPONS,
            NO_FORFEIT,

            NO_DRINKS,
            NO_FOOD,
            NO_PRAYER,
            NO_MOVEMENT,
//		OBSTACLES,

            NO_HELMET,
            NO_CAPE,
            NO_AMULET,
            NO_AMMO,
//		NO_WEAPON,
            NO_BODY,
            NO_SHIELD,
            NO_LEGS,
            NO_GLOVES,
            NO_BOOTS,
            NO_RING,
    };

    public static Rule[] WHIPPING = {
            NO_RANGED,
//		NO_MELEE,
            NO_MAGIC,
            NO_SP_ATTACK,
//		FUN_WEAPONS,
            NO_FORFEIT,

            NO_DRINKS,
            NO_FOOD,
            NO_PRAYER,
            NO_MOVEMENT,
//		OBSTACLES,

            NO_HELMET,
            NO_CAPE,
            NO_AMULET,
            NO_AMMO,
//		NO_WEAPON,
            NO_BODY,
            NO_SHIELD,
            NO_LEGS,
            NO_GLOVES,
            NO_BOOTS,
            NO_RING,
    };

    public static Rule[] BOXING = {
            NO_RANGED,
//		NO_MELEE,
            NO_MAGIC,
            NO_SP_ATTACK,
//		FUN_WEAPONS,
//		NO_FORFEIT,

            NO_DRINKS,
            NO_FOOD,
            NO_PRAYER,
            NO_MOVEMENT,
//		OBSTACLES,
            NO_HELMET,
            NO_CAPE,
            NO_AMULET,
            NO_AMMO,
            NO_WEAPON,
            NO_BODY,
            NO_SHIELD,
            NO_LEGS,
            NO_GLOVES,
            NO_BOOTS,
            NO_RING,
    };


    public int childId, flag;

    private Rule(int childId, int flag) {
        this.childId = childId;
        this.flag = flag;
    }

    public boolean isSet(ATMethodProvider api) {
        return (getConfig(api) & flag) != 0;
    }

    public int getConfig(ATMethodProvider api) {
        return api.configs.get(Data.SETTINGS_CONFIG);
    }

    public boolean shouldBeSet(Rule[] rules) {
        for (Rule rule : rules) {
            if (this == rule) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldNotBeSet(Rule[] rules) {
        return !shouldBeSet(rules);
    }


    public static ArrayList<Rule> listIncorrectRules(RuleSet set, ATMethodProvider api) {
        ArrayList<Rule> temp = new ArrayList<Rule>();
        int x = 230, y = 340, yy = 13;
        Rule[] rules;
        if (set != null) {
            rules = set.getRules();
            if (rules != null) {
                for (Rule r : Rule.values()) {
                    if ((r.shouldBeSet(rules) && !r.isSet(api)) || (r.shouldNotBeSet(rules) && r.isSet(api))) {
                        temp.add(r);
                    }
                }
            }
        }
        return temp;
    }



}
