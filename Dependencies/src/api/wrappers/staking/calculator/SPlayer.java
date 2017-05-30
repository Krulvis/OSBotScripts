package api.wrappers.staking.calculator;

import api.wrappers.staking.data.RuleSet;
import api.wrappers.staking.data.Settings;

/**
 * Created by Krulvis on 29-May-17.
 */
public class SPlayer {

    public int attack, strength, defense, hitpoints;
    public int currentHp, specsUsed, specsToTank, hitsTankWeapon;
    public int attBonWep, strBonWep, defBonWep;
    public int AEL, SEL, DEL;
    public int ASL, SSL, DSL;
    public int ATL, STL, DTL;
    public int MMAR, MMSR, MMDR; //rolls
    public int SMAR, SMSR, SMDR; //rolls
    public int TMAR, TMSR, TMDR; //rolls
    public int[] SKILLS = new int[7];
    private Settings.Weapon weapon;
    private Settings.Style style, specStyle;
    private boolean useSpecial;
    private boolean dead, turn;
    public int wins;
    private int combatLevel = -1;
    public String name;

    public SPlayer(String name, int[] skills) {
        this.name = name;
        this.SKILLS = skills;
        setLevels();
        this.dead = false;
        this.turn = false;
        this.wins = 0;
    }

    public SPlayer(String name, int[] skills, RuleSet set) {
        this.name = name;
        this.SKILLS = skills;
        setLevels();
        this.dead = false;
        this.turn = false;
        this.wins = 0;
        calcStats(set);
    }

    public void reset(boolean useSpecial) {
        setCurrentHp(hitpoints);
        setSpecData(useSpecial);
        this.dead = false;
        this.turn = false;
    }

    public void calcStats(RuleSet set){
        this.wins = 0;
        this.dead = false;
        this.turn = false;
        this.weapon = set != null ? set.getWeapon() : Settings.Weapon.VINE_WHIP;
        this.style = set != null ? set.getStyle() : Settings.Style.CONTROLLED;
        this.specStyle = Settings.Style.AGGRESSIVE;
        this.useSpecial = set == RuleSet.DDS;
        setCurrentHp(hitpoints);
        setSpecData(set == RuleSet.DDS);
        this.specStyle = Settings.Style.AGGRESSIVE;
        setWeaponBonus();
        setMainEffectiveLevels();
        setSpecEffectiveLevels();
        setTankEffectiveLevels();
        setMainMaxRolls();
        setSpecMaxRolls();
        setTankMaxRolls();
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void setCurrentHp(int hitpoints) {
        this.currentHp = hitpoints;
    }

    public void setLevels() {
        this.attack = SKILLS[0];
        this.strength = SKILLS[1];
        this.defense = SKILLS[2];
        this.hitpoints = SKILLS[3];
        System.out.println(this.name + " levels: ");
        System.out.println("Attack: " + attack);
        System.out.println("Strength: " + strength);
        System.out.println("Defense: " + defense);
        System.out.println("Hitpoints: " + hitpoints);
        System.out.println("..................");
    }

    public void setWeaponBonus() {
        this.attBonWep = weapon.attBonus;
        this.strBonWep = weapon.strBonus;
        this.defBonWep = weapon.defBonus;
    }

    public void setMainEffectiveLevels() {
        this.AEL = this.attack + 8 + this.style.att;
        this.SEL = this.strength + 8 + this.style.str;
        this.DEL = this.defense + 8 + this.style.def;
        System.out.println(this.name + " MEL: ");
        System.out.println("AEL: " + AEL);
        System.out.println("ASL: " + SEL);
        System.out.println("ADL: " + DEL);
        System.out.println("..................");

    }

    public void setSpecEffectiveLevels() {
        this.ASL = this.attack + 8 + specStyle.att;
        this.SSL = this.strength + 8 + specStyle.str;
        this.DSL = this.defense + 8 + specStyle.def;
    }

    public void setTankEffectiveLevels() {
        this.ATL = attack + 9;
        this.STL = strength + 9;
        this.DTL = defense + 9;
    }

    public void setMainMaxRolls() {
        this.MMAR = (int) Math.floor(((((double) this.attBonWep * 0.015625) + 1.0) * ((double) this.AEL))); // Add Equiment Bonus (if ever needed)
        this.MMDR = (int) Math.floor(((((double) this.defBonWep * 0.015625) + 1.0) * ((double) this.DEL))); // Add Equiment Bonus (if ever needed)
        this.MMSR = (int) Math.floor(((((double) this.strBonWep * 0.015625) + 1.0) * ((double) this.SEL) + 5.0) / 10.0); // Add Equiment Bonus (if ever needed)
        System.out.println(this.name + " MMR: ");
        System.out.println("MMAR: " + MMAR);
        System.out.println("MMSR: " + MMSR);
        System.out.println("MMDR: " + MMDR);
        System.out.println("..................");
    }

    public void setSpecMaxRolls() {
        this.SMAR = (int) Math.floor(((((double) 40 * 0.015625) + 1.0) * ((double) this.ASL * 1.1)));
        this.SMDR = (int) Math.floor(((((double) 0 * 0.015625) + 1.0) * ((double) this.DSL)));
        this.SMSR = (int) (Math.floor(((((double) 40 * 0.015625) + 1.0) * ((double) this.SSL) + 5.0) / 10.0) * 1.15);
        System.out.println(this.name + " SMR: ");
        System.out.println("SMAR: " + SMAR);
        System.out.println("SMSR: " + SMSR);
        System.out.println("SMDR: " + SMDR);
        System.out.println("..................");
    }

    public void setTankMaxRolls() {
        this.TMAR = (int) Math.floor(((((double) 85 * 0.015625) + 1.0) * ((double) this.ATL)));
        this.TMDR = (int) Math.floor(((((double) 13 * 0.015625) + 1.0) * ((double) this.DTL)));
        this.TMSR = (int) Math.floor(((((double) 75 * 0.015625) + 1.0) * ((double) this.STL) + 5.0) / 10.0);
    }


    public void setSpecData(boolean useSpecial) {
        if (useSpecial) {
            this.specsUsed = 0;
            this.specsToTank = 0;
            this.hitsTankWeapon = 0;
        } else {
            this.specsUsed = 0;
            this.specsToTank = 0;
            this.hitsTankWeapon = 0;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isTurn() {
        return turn;
    }

    public int getWins() {
        return this.wins;
    }

    public void addWin() {
        this.wins++;
    }

    public int getCombatLevel() {
        if (combatLevel != -1)
            return combatLevel;
        return combatLevel = (int) Math.floor(combatFormula(attack, strength, defense, hitpoints, SKILLS[4], SKILLS[5], SKILLS[6]));
    }

    public static double combatFormula(int attack, int strength, int defence, int hitpoints, int prayer, int ranged, int magic) {
        double combatLevel = (defence + hitpoints + Math.floor(prayer / 2)) * 0.25;
        double warrior = (attack + strength) * 0.325;
        double ranger = ranged * 0.4875;
        double mage = magic * 0.4875;
        return combatLevel + Math.max(warrior, Math.max(ranger, mage));
    }
}
