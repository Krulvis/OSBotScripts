package staker;

import api.ATScript;
import api.ATState;
import api.util.ATPainter;
import api.util.Timer;
import api.util.gui.GUIWrapper;
import api.webapi.WebAPI;
import api.webapi.actions.GetPid;
import api.wrappers.staking.Duel;
import api.wrappers.staking.calculator.Calculator;
import api.wrappers.staking.calculator.Odds;
import api.wrappers.staking.calculator.SPlayer;
import api.wrappers.staking.data.RuleSet;
import com.google.gson.*;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.script.ScriptManifest;
import staker.states.*;
import staker.util.Painter;
import staker.util.webapi.ReloadSettings;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Krulvis on 29-May-17.
 */

@ScriptManifest(author = "Krulvis", version = 1.02D, logo = "", info = "", name = "Staker")
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
    public String autoChatMessage = "";
    public boolean walkBack = true;

    /**
     * Offer settingsFolder
     */
    public int minAmount = 1;
    public int maxAmount = 2000000;

    /**
     * WebAPI settings
     */
    public Timer lastUpdateTimer;
    public ATState prevState;
    public GetPid getPid;

    /**
     * States
     */
    public Fight fight;
    public Second second;
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
        statesToAdd.add(this.fight = new Fight(this));
        statesToAdd.add(this.endFight = new EndFight(this));
        statesToAdd.add(new Third(this));
        statesToAdd.add(this.second = new Second(this));
        statesToAdd.add(new First(this));
        statesToAdd.add(new Waiting(this));

        webAPI.addAction(new ReloadSettings<>(this));
        webAPI.addAction(getPid = new GetPid(this));
        getNewSettings();
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
            currentDuel.sendResults(webAPI);
            currentDuel.resetStakes();
            if (getPreviousDuel(currentDuel.getPlayerName()) == null) {
                duelList.add(currentDuel);
            }
            currentDuel = null;
        }
        this.fight.canAttackPlayer = false;
        this.second.tooLowTimer = null;
        return true;
    }

    @Override
    public void onMessage(Message m) {
        if (m.getTypeId() == 102 && m.getMessage() != null && m.getMessage().contains("cancelled") && currentDuel != null) {
            currentDuel.setFinished(false);
            resetValues();
        }
    }

    public Duel getPreviousDuel(String name) {
        for (Duel d : duelList) {
            if (d.getPlayerName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public void addStatusUpdater() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (webAPI.isConnected()) {
                    try {
                        ATState state = currentState;
                        if (lastUpdateTimer == null || lastUpdateTimer.isFinished() || (prevState == null && state != null) || (state != null && prevState != null && !state.getName().equalsIgnoreCase(prevState.getName()))) {
                            //System.out.println("Updating status: PREV: " + (prevState == null ? "None" : prevState.getName()) + ", New: " + (state != null ? state.getName() : "None"));
                            lastUpdateTimer = new Timer(60000);
                            prevState = state;
                            webAPI.sendStatus(webAPI.hasAccount() && state != null && webAPI.getStatus() != WebAPI.Status.INACTIVE ? state.getName() : webAPI.getStatus().toString());
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "StatusUpdaterThread").start();
    }

    public void getNewSettings() {
        if (webAPI.isConnected() && webAPI.getWebConnection().id >= 0) {
            try {
                JsonElement el = webAPI.getWebConnection().getJson("bot/duel/config");
                System.out.println("StakeSettings response: " + el);
                if (el != null && el != JsonNull.INSTANCE) {
                    JsonArray arr = el.getAsJsonArray();
                    for (JsonElement element : arr) {
                        JsonObject obj = element.getAsJsonObject();
                        if (obj != null) {
                            JsonElement min_ele = obj.get("min_offer");
                            minAmount = min_ele != null && !min_ele.isJsonNull() ? min_ele.getAsInt() : minAmount;

                            JsonElement max_ele = obj.get("max_offer");
                            maxAmount = max_ele != null && !max_ele.isJsonNull() ? max_ele.getAsInt() : maxAmount;

                            JsonElement mod_ele = obj.get("returnPercent");
                            returnPercent = mod_ele != null && !mod_ele.isJsonNull() ? mod_ele.getAsInt() : returnPercent;

                            JsonElement _declineTimer = obj.get("decline_timer");
                            declineTime = _declineTimer != null && !_declineTimer.isJsonNull() ? _declineTimer.getAsInt() * 1000 : declineTime;

                            JsonElement _message = obj.get("message");
                            autoChatMessage = _message != null && !_message.isJsonNull() ? _message.getAsString() : autoChatMessage;

                            JsonElement equal_ele = obj.get("equal_offer");
                            equalOfferAtHighOdds = equal_ele != null && !equal_ele.isJsonNull() ? equal_ele.getAsBoolean() : equalOfferAtHighOdds;

                            JsonElement _relocate = obj.get("relocate");
                            walkBack = _relocate != null && !_relocate.isJsonNull() ? _relocate.getAsBoolean() : walkBack;

                            System.out.println("New settings obtained...");
                            System.out.println("Min Amount: " + minAmount);
                            System.out.println("Max Amount: " + maxAmount);
                            System.out.println("Modifier: " + returnPercent);
                            System.out.println("Message: " + autoChatMessage);
                            System.out.println("Decline Time: " + declineTime);
                            System.out.println("Equal at high odds: " + equalOfferAtHighOdds);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void startConfigListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (webAPI.isConnected()) {
                    getNewSettings();
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "ConfigListenerThread").start();
    }
}
