package staker.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import staker.util.delays.RelocateDelay;
import api.wrappers.staking.calculator.SPlayer;
import api.wrappers.staking.data.Settings;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Option;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.input.mouse.PointDestination;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.utility.Condition;
import staker.Staker;

import java.awt.*;
import java.util.*;

import static api.util.Random.nextGaussian;
import static api.wrappers.staking.data.Data.DUEL_INTERFACE_1;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Waiting extends ATState<Staker> {

    public Waiting(Staker script) {
        super("Waiting", script);
        challengeMessageRectangle = new RectangleDestination(bot, challengeMessageRect);
    }

    public Timer logoutTimer;

    @Override
    public int perform() throws InterruptedException {
        if (!isLoggedIn()) {
            return Random.bigSleep();
        } else if (!webAPI.handleWebActions()) {
            return Random.smallSleep();
        }
        script.resetValues();
        if (script.myPlayer == null && client.isLoggedIn()) {
            script.myPlayer = new SPlayer(myPlayer().getName(), new int[]{skills.getStatic(Skill.ATTACK), skills.getStatic(Skill.STRENGTH), skills.getStatic(Skill.DEFENCE), skills.getStatic(Skill.HITPOINTS), skills.getStatic(Skill.PRAYER)});
        }
        if (script.startPos == null && client.isLoggedIn()) {
            script.startPos = myPosition();
        } else if (script.startPos != null && script.walkBack && distance(script.startPos) > 0) {
            RelocateDelay.execute();
            walkPath(script.startPos);
        }
        Settings.Weapon w = script.ruleSet.getWeapon();
        if (!w.isWearing(this) && !script.debug) {
            Item invWeapon = inventory.getItem(w.itemId);
            if (invWeapon != null && invWeapon.interact()) {
                waitFor(1000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return w.isWearing(script);
                    }
                });
            } else if (invWeapon == null) {
                log("Does not have correct weapon.");
                stop();
            }
        }
        if (logoutTimer == null) {
            logoutTimer = new Timer(Random.nextGaussian(30000, 240000, 20000));
        } else if (logoutTimer.isFinished()) {
            interact.moveMouseRandomly();
            logoutTimer = null;
        }
        RS2Widget tradeTab = chat.getTradeTab();
        if (tradeTab != null && tradeTab.getSpriteIndex1() == 1019) {
            RS2Widget trade = chat.getTradeTab();
            RectangleDestination target = trade != null ? new RectangleDestination(bot, trade.getBounds()) : null;
            if (target != null) {
                interact.interact(target);
            }
            sleep(300);
        }

        chat.setMessage(script.autoChatMessage, true);

        if (acceptChallenge(script.maxDistance + 1, null)) {
            script.resetValues();
            if (script.trayMessage) {
                trayMessage(myPlayer().getName(), "Has a challenge!");
            }

            waitFor(2000, new Condition() {
                @Override
                public boolean evaluate() {
                    return stake.isFirstScreenOpen();
                }
            });
        }
        return Random.smallSleep();
    }

    private final Rectangle challengeMessageRect = new Rectangle(11, 443, 100, 10);
    public final RectangleDestination challengeMessageRectangle;

    public boolean acceptChallenge(int maxDistance, ArrayList<String> blacklist) {
        RS2Widget w = widgets.get(162, 43);
        if (w != null) {
            RS2Widget[] lines = w.getChildWidgets();
            String lastMessage = "";
            for (int i = 0; i < lines.length; i++) {
                if (!lines[i].getMessage().equals("")) {
                    lastMessage = lines[i].getMessage();
                    break;
                }
            }
            if (stake.isChallenge(lastMessage)) {
                String username = lastMessage.replaceAll("<[^>]*>", "").replaceAll(" wishes to duel with you.", "").replaceAll("\u00A0", " ").trim();
                Player player = getPlayer(username);
                System.out.println("Username: " + username + ", player null: " + (player == null));
                if (player == null
                        || distance(player) > maxDistance
                        || (blacklist != null && blacklist.contains(username))
                        ) {
                    System.out.println("Ignore player " + (player == null ? "player is null" : "distance: " + distance(player) + "too long"));
                    return false;
                }
                int width = challengeMessageRect.width;
                int x = challengeMessageRect.x;
                int max_w = challengeMessageRect.x + width;
                int length = challengeMessageRect.height;
                int y = challengeMessageRect.y;
                int max_y = challengeMessageRect.y + length;
                final Point p = new Point(nextGaussian(x, max_w, x + (width / 2), (width / 2) / 2), nextGaussian(y, max_y, y + (length / 2), (length / 2) / 2));
                //TODO MOVE MOUSE DELAY
                if (mouse.move(new PointDestination(bot, p))) {
                    java.util.List<Option> menuItems = menu.getMenu();
                    if (challengeMessageRectangle.getBoundingBox().contains(mouse.getPosition())
                            && menuItems != null) {
                        //TODO DELAY
                        //Build in Challenge accept delay
                        sleep(100, 500);
                        String leftClick = menuItems.size() > 0 ? menuItems.get(0).action : null;
                        if (distance(player) < maxDistance && leftClick != null && leftClick.contains("Accept challenge")) {
                            mouse.click(false);
                            waitFor(1000, new Condition() {
                                @Override
                                public boolean evaluate() {
                                    //check if it's being lured
                                    return validateWidget(DUEL_INTERFACE_1);
                                }
                            });
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }
}
