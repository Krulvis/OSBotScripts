package api.wrappers.staking;

import api.util.Timer;
import api.util.antiban.chats.StartFightChat;
import api.webapi.WebAPI;
import api.wrappers.staking.calculator.Odds;
import api.wrappers.staking.calculator.SPlayer;
import api.wrappers.staking.data.RuleSet;
import com.google.gson.*;
import org.osbot.rs07.api.model.Item;

import java.awt.*;

import static api.util.ATPainter.*;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Duel {

    private final int yy = 10;
    private int increaedReturn, returnPercent;
    private int myExact;
    private int otherExact;
    private int myRoundedMultiplied;
    private double multiplier;
    private SPlayer me, opponent;
    private Odds odds;
    public Timer declineTimer = null;
    private Timer fightTimer = null;
    private Item[] otherItems;
    private String cancelReason = null;
    private boolean won, finished;
    private boolean sendResults = false;

    private StartFightChat startFightChat;

    public Duel(SPlayer me, SPlayer opponent, Odds odds, int returnPercent, int myExact, int otherExact, int myRoundedMultiplied) {
        this.me = me;
        this.opponent = opponent;
        this.odds = odds;
        this.returnPercent = returnPercent;
        this.myExact = myExact;
        this.otherExact = otherExact;
        this.myRoundedMultiplied = myRoundedMultiplied;
        this.startFightChat = new StartFightChat();
    }

    public void checkTimer(int declineTime) {
        if (declineTimer == null) {
            declineTimer = new Timer(declineTime);
        }
    }

    public boolean shouldDecline() {
        return declineTimer != null && declineTimer.isFinished();
    }

    public SPlayer getOpponent() {
        return opponent;
    }

    public int getAttack() {
        return opponent.attack;
    }

    public int getStrength() {
        return opponent.strength;
    }

    public int getDefence() {
        return opponent.defense;
    }

    public int getHp() {
        return opponent.hitpoints;
    }

    public String getPlayerName() {
        return opponent.name;
    }

    public void setPlayerName(String playerName) {
        this.opponent.name = playerName;
    }

    public int getIncreaedReturn() {
        return increaedReturn;
    }

    public int getMyExact() {
        return myExact;
    }

    public int getOtherExact() {
        return otherExact;
    }

    public void setOtherExact(int otherExact) {
        this.otherExact = otherExact;
    }

    public Item[] getOtherItems() {
        return otherItems;
    }

    public void setOtherItems(Item[] otherItems) {
        this.otherItems = otherItems;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Timer getFightTimer() {
        return fightTimer;
    }

    public void resetFightTimer() {
        this.fightTimer = new Timer();
    }

    public void stopFightTimer() {
        if (this.fightTimer != null) {
            this.fightTimer.stop();
        }
    }

    public StartFightChat getStartFightChat() {
        return startFightChat;
    }

    public boolean hasSendResults() {
        return sendResults;
    }

    public void setSendResults(boolean sendResults) {
        this.sendResults = sendResults;
    }

    public void calculateMyOffer(boolean equalOffersAtHighOdds) {
        int difference = (int) Math.abs(odds.getRandomOdds() - returnPercent);
        increaedReturn = difference > 4.5 ? (int) Math.abs(difference / 4.5) + returnPercent : returnPercent;
        multiplier = odds.getMultiplier(increaedReturn);
        //System.out.println("X ing with: " + multiplier + ", returnPercent: " + returnPercent);
        myExact = (int) Math.round(otherExact * (equalOffersAtHighOdds && odds.getRandomOdds() >= increaedReturn ? 1 : multiplier));
        myRoundedMultiplied = (int) (myExact > 100000 ? Math.floor((myExact / 100000) * 100000) : Math.floor(myExact));
    }

    public int getMyRoundedMultiplied() {
        return myRoundedMultiplied;
    }

    public Odds getOdds() {
        return odds;
    }

    public boolean isOddsCalculated() {
        return getOdds() != null && getOdds().isCalculated();
    }

    public void resetVars() {
        this.myExact = 0;
        this.otherExact = 0;
        this.myRoundedMultiplied = 0;
        this.increaedReturn = 55;
        this.declineTimer = null;
        this.otherItems = null;
        this.cancelReason = null;
        this.won = false;
        this.finished = false;
        this.fightTimer = null;
        this.sendResults = false;
        this.startFightChat = new StartFightChat();
    }

    public SPlayer getMe() {
        return me;
    }

    public void drawOdds(Graphics2D g, RuleSet ruleSet) {
        g.setColor(BLACK_A);
        g.fillRect(550, 360, 120, 100);
        g.setColor(Color.WHITE);
        g.setFont(SMALL14);
        int oddsY = 390, oddsX = 555;
        drawStringCenter(g, ruleSet.getName(), 600, 380, Color.white);
        drawString(g, "Rnd Odds: ", oddsX, oddsY += yy);
        drawString(g, "Pid  Odds: ", oddsX, oddsY += yy);
        drawString(g, "NPd Odds: ", oddsX, oddsY += yy);
        drawString(g, "Multiply: ", oddsX, oddsY += yy);
        oddsY = 390;
        oddsX = 620;
        drawString(g, "" + getOdds().getRandomOdds(), oddsX, oddsY += yy);
        drawString(g, "" + getOdds().getPidOdds(), oddsX, oddsY += yy);
        drawString(g, "" + getOdds().getNoPidOdds(), oddsX, oddsY += yy);
        drawString(g, "" + multiplier, oddsX, oddsY += yy);
    }

    public void drawMoney(Graphics g) {
        g.setColor(BLACK_A);
        g.fillRect(670, 437, 60, 23);
        double value = getOtherExact() / 1000000.0 > 0.0 ? (getOtherExact() / 10000) / 100.D : 0;
        String valueS = value > 0 ? +value + " M" : "0";
        if (value > 0 && getOdds() != null) {
            double offer = (getMyRoundedMultiplied() / 10000) / 100.D;
            drawString(g, offer + " M", 673, 450);
        }
        int x = 275;
        int y = 347;
        //if(StakeOffers.otherExact > 0){
        drawString(g, "Other Exact: " + getOtherExact(), x, y += yy);
        drawString(g, "My Exact: " + getMyExact(), x, y += yy);
        drawString(g, "My Rounded: " + getMyRoundedMultiplied(), x, y += yy);
        drawString(g, "My multiplier: " + getIncreaedReturn(), x, y += yy);
    }

    public void drawStatsCompare(Graphics2D g) {
        g.setColor(BLACK_A);
        g.fillRect(384, 345, 130, 130 + 5);
        SPlayer opponent = getOpponent();
        int h_x = 384 + 4, h_y = 345 + 12;
        Font small11 = new Font("Helvetica", Font.BOLD, 12);
        Font small10 = new Font("Helvetica", Font.BOLD, 10);
        g.setFont(small11);
        drawString(g, "Opp: " + (getPlayerName() != null ? getPlayerName() : "-"), h_x + 15, h_y, Color.WHITE);
        h_y += 12;
        if (getPlayerName() != null) {
            SPlayer myPlayer = getMe();
            if (opponent != null && myPlayer != null) {
                g.setFont(small10);
                drawString(g, "HS Combat: ", h_x, h_y, GRAY);
                g.setFont(small11);
                drawString(g, "" + opponent.getCombatLevel(), h_x + 60, h_y, Color.WHITE);
                g.setFont(small11);
                h_y += 12;
                drawString(g, "  Skill", h_x, h_y, DARK_RED);
                drawString(g, "YOU", h_x + 50, h_y, DARK_RED);
                drawString(g, "HIM", h_x + 50 + 35, h_y, DARK_RED);
                String[] skills = new String[]{"Attack", "Strength", "Defense", "Hitpoints", "Prayer"};
//				for(int ix = 0; ix < hi.skill_.length; ix++){
                for (int i = 0; i < 5; i++) {
                    h_y += 12;
                    g.setFont(small10);
                    drawString(g, skills[i] + ": ", h_x, h_y, GRAY);
                    g.setFont(small11);

                    drawString(g, "" + myPlayer.SKILLS[i], h_x + 50 + 5, h_y, myPlayer.SKILLS[i] > opponent.SKILLS[i] ? Color.GREEN : (myPlayer.SKILLS[i] == opponent.SKILLS[i] ? Color.YELLOW : Color.RED));
                    drawString(g, "" + opponent.SKILLS[i], h_x + 50 + 35 + 5, h_y, opponent.SKILLS[i] > myPlayer.SKILLS[i] ? Color.GREEN : (opponent.SKILLS[i] == myPlayer.SKILLS[i] ? Color.YELLOW : Color.RED));
                }
            } else {
                g.setFont(small10);
                drawString(g, "Loading...", h_x, h_y, GRAY);
            }
        }
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void sendResults(WebAPI webAPI) {
        if (webAPI.isConnected()) {
            //convert naar JSON before sending dis shit
            JsonNull nul = JsonNull.INSTANCE;
            JsonObject object = new JsonObject();
            object.add("opponent_rsn", new JsonPrimitive(getPlayerName()));
            object.add("won", finished ? new JsonPrimitive(won) : new JsonPrimitive(false));
            object.add("bot_stake_value", new JsonPrimitive(getMyRoundedMultiplied() > 0 ? getMyRoundedMultiplied() : 0));
            object.add("opponent_stake_value", new JsonPrimitive(getOtherExact() > 0 ? getOtherExact() : 0));
            JsonArray bet_items = new JsonArray();
            Item[] items = getOtherItems();
            if (items != null && items.length > 0) {
                for (Item i : items) {
                    if (i != null && i.getId() > 0) {
                        bet_items.add(i);
                    }
                }
            }
            object.add("opponent_stake_items", bet_items);
            object.add("duration", new JsonPrimitive(fightTimer != null ? (int) (fightTimer.getElapsedTime() / 1000) : 0));
            Odds odds = getOdds();

            object.add("npid_odds", odds != null ? new JsonPrimitive(odds.getNoPidOdds()) : nul);
            object.add("pid_odds", odds != null ? new JsonPrimitive(odds.getPidOdds()) : nul);
            object.add("rnd_odds", odds != null ? new JsonPrimitive(odds.getRandomOdds()) : nul);
            object.add("returnPercent", odds != null ? new JsonPrimitive(getMultiplier()) : nul);
            object.add("finished", new JsonPrimitive(finished));
            if (cancelReason != null) {
                object.add("cancelled_reason", new JsonPrimitive(cancelReason));
            }
            JsonElement response = webAPI.getWebConnection().sendJSON("bot/duel", "POST", object);
            webAPI.sendInventoryScreenshot();
            webAPI.sendInventoryValue();
            System.out.println("Send stake results: " + response);
            setSendResults(true);
        }
    }
}
