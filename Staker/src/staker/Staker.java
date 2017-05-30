package staker;

import api.ATScript;
import api.ATState;
import api.util.ATPainter;
import api.util.gui.GUIWrapper;
import api.wrappers.staking.Duel;
import api.wrappers.staking.calculator.SPlayer;
import api.wrappers.staking.data.RuleSet;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.ScriptManifest;
import staker.states.*;
import staker.util.GUI;
import staker.util.Painter;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Krulvis on 29-May-17.
 */
@ScriptManifest(author = "Krulvis", version = 1.0D, logo = "", info = "", name = "Staker")
public class Staker extends ATScript {

    public boolean debug = false;
    public int totalGains = 0, totalLosses = 0;

    /**
     * Duel vars
     */
    public ArrayList<Duel> duelList = new ArrayList<>();
    public Duel currentDuel;
    public SPlayer myPlayer;
    public RuleSet ruleSet = RuleSet.VINE_WHIP;
    public Position startPos = null;

    /**
     * Setting vars
     */
    public boolean equalOfferAtHighOdds = false;
    public int returnPercent = 55;
    public int maxDistance = 2;
    public boolean trayMessage = false;
    public int declineTime = 120000;
    public String autoChatMessage = "1";

    /**
     * Offer settingsFolder
     */
    public int minAmount = 1;
    public int maxAmount = 2000000;

    /**
     * States
     */
    public EndFight endFight;

    @Override
    public void update() {

    }

    @Override
    public void onStart() {
        setPrivateVersion();
        useWebAPI();
    }

    @Override
    protected void initialize(LinkedList<ATState> statesToAdd) {
        statesToAdd.add(new Starting(this));
        statesToAdd.add(new Fight(this));
        statesToAdd.add(endFight = new EndFight(this));
        statesToAdd.add(new Third(this));
        statesToAdd.add(new Second(this));
        statesToAdd.add(new First(this));
        statesToAdd.add(new Waiting(this));
    }

    @Override
    protected Class<? extends ATPainter> getPainterClass() {
        return Painter.class;
    }

    @Override
    protected Class<? extends GUIWrapper> getGUI() {
        return null;
    }

    public boolean resetValues() {
        if (currentDuel != null) {
            currentDuel.resetStakes();
            if (getPreviousDuel(currentDuel.getPlayerName()) != null) {
                duelList.add(currentDuel);
            }
            currentDuel = null;
        }
        return true;
    }

    public Duel getPreviousDuel(String name) {
        for (Duel d : duelList) {
            if (d.getPlayerName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }
}
