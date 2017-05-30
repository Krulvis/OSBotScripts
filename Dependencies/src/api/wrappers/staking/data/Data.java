package api.wrappers.staking.data;


import org.osbot.rs07.api.map.Area;

/**
 * Created by Krulvis on 30-6-2014.
 */
public class Data {

    public static int DRAGON_SCIMITAR = 4587,
            DDS = 5698;

    public static int DUEL_INTERFACE_1 = 482,
            DUEL_INTERFACE_2 = 481,
            DUEL_INTERFACE_3 = 476,

            LOST_INTERFACE = 119,
            VICTORY_INTERFACE = 110;

    public static int CLAIM_LOST_BUTTON = 22,
            CLAIM_PRICE_BUTTON = 14,
            ITEM_INVENTORY = 33;

    public static Area BANK_AREA = new Area(3380, 3267, 3384, 3271),
            ALTAR_AREA = new Area(3374, 3280, 3379, 3286),
            ARENA_AREA = new Area(3332, 3200, 3379, 3260),//this is wrong, need to getResource data
            CHALLENGE_AREA = new Area(3355, 3262, 3379, 3279);

    //First screen Levels
    public static final int ATT_LEVEL_CUR = 8,
                ATT_LEVEL_REAL = 9,
                STR_LEVEL_CUR = 12,
                STR_LEVEL_REAL = 13,
                DEF_LEVEL_CUR = 16,
                DEF_LEVEL_REAL = 17,
                HP_LEVEL_CUR = 20,
                HP_LEVEL_REAL = 21,
                PRAY_LEVEL_CUR = 24,
                PRAY_LEVEL_REAL = 25,
                RANG_LEVEL_CUR = 28,
                RANG_LEVEL_REAL = 29,
                MAG_LEVEL_CUR = 32,
                MAG_LEVEL_REAL = 33;

    public static final int OTHER_NAME_1 = 35,
    COMBAT_LEVEL_1 = 34;

    //FIRST SCREEN SETTINGS
    public static final int FORFEIT = 37,
            MOVEMENT = 38,
            WEAP_SWITCH = 39,
            SHOW_INVENTORY = 40,
            RANGED = 41,
            MELEE = 42,
            MAGIC = 43,
            DRINKS = 44,
            FOOD = 45,
            PRAYER = 46,
            OBSTACLES = 47,
            FUN_WEPS = 48,
            SPECIAL = 49;
    // + 13 for the button

    //First screen Equipment parent 63
    public static int EQUIPMENT_PARENT_1 = 63, EQUIPMENT_PARENT_3 = 11,
            HEAD = 0,
            CAPE = 1,
            NECK = 2,
            WEAPON = 4,
            CHEST = 5,
            SHIELD = 6,
            LEGS = 7,
            HANDS = 8,
            FEET = 9,
            RING = 10,
            AMMO = 3;

    public static int SETTINGS_CONFIG = 286;

    //First screen
    public static int ACCEPT_1 = 103,
            DECLINE_1 = 104,
            ACCEPTED_CHECK_1 = 107,
            LOAD_PRESET = 112,
            SAVE_PRESET = 110,
            LOAD_LAST = 111;

    //second screen
    public static int MY_OFFER_2 = 18,
            OTHER_OFFER_2 = 26,
            ACCEPT_2 = 73,
            DECLINE_2 = 72,
            ACCEPTED_CHECK_2 = 74,
            DUELING_INV = 40,
            BEFORE_DUEL = 49;

    //Third screen
    public static int ACCEPT_3 = 78,
            DECLINE_3 = 80,
            ACCEPT_CHECK_3 = 26,
            MY_OFFER_3 = 57,
            OTHER_OFFER_3 = 65,
            OTHER_OFFER_WORTH_3 = 67,
            MY_OFFER_WORTH_3 = 59;



    public static final Area OBSTABCLE_ARENA1 = new Area(3362, 3259, 3390, 3243),
            ARENAS = new Area(3329, 3259, 3390, 3205);


}
