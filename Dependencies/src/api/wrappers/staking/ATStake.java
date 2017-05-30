package api.wrappers.staking;


import api.ATMethodProvider;
import api.util.*;
import api.util.Timer;
import api.util.filter.TextFilter;
import api.wrappers.grandexchange.GrandExchange;
import api.wrappers.staking.data.RuleSet;
import api.wrappers.staking.data.Settings;
import com.google.gson.*;
import org.osbot.rs07.api.def.ItemDefinition;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Option;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.mouse.PointDestination;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.utility.Condition;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;

import static api.util.Random.*;
import static api.wrappers.staking.data.Data.*;

/**
 * Created by s120619 on 29-May-17.
 */
public class ATStake extends ATMethodProvider {

    public boolean hitOnSecond = false;
    public HashMap<Integer, Integer> pricemap = new HashMap<>();
    public HashMap<String, Integer> pricemapNames = new HashMap<>();
    public ArrayList<Integer> ignoreList = new ArrayList<>();
    public Player opponent = null;

    public final ATMethodProvider parent;

    public ATStake(ATMethodProvider parent) {
        init(parent);
        this.parent = parent;
        challengeMessageRectangle = new RectangleDestination(bot, challengeMessageRect);
        pricemap.put(13204, 1000);
        pricemapNames.put("platinum token", 1000);
    }

    public boolean canAttackPlayer = false;

    public boolean isFirstScreenOpen() {
        return validateWidget(DUEL_INTERFACE_1);
    }

    public boolean isSecondScreenOpen() {
        return validateWidget(DUEL_INTERFACE_2);
    }

    public boolean isThirdScreenOpen() {
        return validateWidget(DUEL_INTERFACE_3);
    }

    public boolean isStakeScreenOpen() {
        return isFirstScreenOpen() || isSecondScreenOpen();
    }

    public boolean isFirstScreenAccepted() {
        RS2Widget wc = widgets.get(DUEL_INTERFACE_1, ACCEPTED_CHECK_1);
        return validateWidget(wc) && wc.getMessage().equals("Waiting for other player...");
    }

    public boolean isSecondScreenAccepted() {
        RS2Widget widget = widgets.singleFilter(DUEL_INTERFACE_2, new TextFilter("Waiting for other player..."));
        return validateWidget(widget);
    }

    public boolean isThirdScreenAccepted() {
        RS2Widget wc = widgets.singleFilter(DUEL_INTERFACE_3, new TextFilter("Waiting for other player..."));
        return validateWidget(wc);
    }

    public boolean acceptFirstScreen() {
        RS2Widget wc = widgets.get(DUEL_INTERFACE_1, ACCEPT_1);
        if (wc != null && wc.getMessage().toLowerCase().contains("wait")) {
            waitFor(3000, new Condition() {
                @Override
                public boolean evaluate() {
                    RS2Widget wc = widgets.get(DUEL_INTERFACE_1, ACCEPT_1);
                    return wc != null && !wc.getMessage().toLowerCase().contains("wait");
                }
            });
        }
        wc = widgets.get(DUEL_INTERFACE_1, ACCEPT_1);
        return wc != null && !wc.getMessage().toLowerCase().contains("wait") && wc.interact();
    }

    public boolean acceptSecondScreen() {
        RS2Widget wc = widgets.get(DUEL_INTERFACE_2, ACCEPT_2);
        if (wc != null && wc.getMessage().toLowerCase().contains("wait")) {
            waitFor(3000, new Condition() {
                @Override
                public boolean evaluate() {
                    RS2Widget wc = widgets.get(DUEL_INTERFACE_2, ACCEPT_2);
                    return wc != null && !wc.getMessage().toLowerCase().contains("wait");
                }
            });
        }
        wc = widgets.get(DUEL_INTERFACE_2, ACCEPT_2);
        return wc != null && !wc.getMessage().toLowerCase().contains("wait") && wc.interact();
    }

    public boolean acceptThirdScreen() {
        RS2Widget wc = widgets.get(DUEL_INTERFACE_3, ACCEPT_3);
        if (wc != null && wc.getMessage().toLowerCase().contains("wait")) {
            waitFor(3000, new Condition() {
                @Override
                public boolean evaluate() {
                    RS2Widget wc = widgets.get(DUEL_INTERFACE_3, ACCEPT_3);
                    return wc != null && !wc.getMessage().toLowerCase().contains("wait");
                }
            });
        }
        wc = widgets.get(DUEL_INTERFACE_3, ACCEPT_3);
        return wc != null && !wc.getMessage().toLowerCase().contains("wait") && wc.interact();
    }

    public int fairOffer() {
        int my_amount = myOfferedCashAmount();
        int other_amount = otherOfferedCashAmount();

        return other_amount - my_amount;
    }

    public boolean loadPreset() {
        if (!isFirstScreenOpen()) {
            return false;
        }
        return widgets.get(DUEL_INTERFACE_1, LOAD_PRESET).interact();
    }

    public String getOtherName1() {
        final RS2Widget nameWidget = widgets.get(DUEL_INTERFACE_1, OTHER_NAME_1);
        if (nameWidget != null && nameWidget.getMessage() != null) {
            return nameWidget.getMessage().replaceAll("\u00A0", " ").substring(nameWidget.getMessage().indexOf("Dueling with: ") + 14);
        }
        return null;
    }

    public boolean offer(int amount) {
        if (amount < 0) {
            return remove(-amount);
        }
        Item coins = inventory.getItem(995);
        if (coins == null) {
            //System.out.println(ATStake.class.getName() + ": can't find coins @ offer(int amount)");
        }
        if (interact(coins, "Stake X")) {
            //System.out.println("Interacted with coins");
            sleep(500, 700);
            waitFor(2000, new Condition() {
                @Override
                public boolean evaluate() {
                    return waitingForInput();
                }
            });
            if (!waitingForInput()) {
                System.out.println("waitingForInput() not appearing!!");
            }
            return waitingForInput() && handleFillInX(amount);
        } else {
            //System.out.println("something going wrong @ interacting with coins");
            //System.out.println("Inventory is " + (Inventory.isOpen() ? "open" : "closed"));
        }
        return false;
    }

    public boolean remove(int amount) {
        RS2Widget wc = getRS2WidgetForMyId(995);
        if (wc != null && wc.interact("Remove X")) {
            waitFor(2000, new Condition() {
                @Override
                public boolean evaluate() {
                    return waitingForInput();
                }
            });
            sleep(500, 1000);
        }
        return waitingForInput() && handleFillInX(amount);
    }


    public RS2Widget getRS2WidgetForMyId(int id) {
        RS2Widget[] inventory = widgets.get(DUEL_INTERFACE_2, MY_OFFER_2).getChildWidgets();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i].getItemId() == id) {
                return inventory[i];
            }
        }
        return null;
    }

    public int myOfferUsedSlots() {
        int count = 0;
        RS2Widget[] inventory = widgets.get(DUEL_INTERFACE_2, MY_OFFER_2).getChildWidgets();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i].getItemId() > 0) {
                count++;
            }
        }
        return count;
    }

    public int[] getIds(int parent, int child) {
        int[] offer = new int[30];
        RS2Widget wc = widgets.get(parent, child);
        if (validateWidget(wc)) {
            for (int i = 0; i < wc.getChildWidgets().length; i++) {
                RS2Widget c = wc.getChildWidget(i);
                if (c != null) {
                    offer[i] = c.getItemId();
                }
            }
        }
        return offer;
    }

    public int[] getStacks(int parent, int child) {
        int[] offer = new int[30];
        RS2Widget wc = widgets.get(parent, child);
        if (validateWidget(wc)) {
            for (int i = 0; i < wc.getChildWidgets().length; i++) {
                RS2Widget c = wc.getChildWidget(i);
                if (c != null) {
                    offer[i] = c.getItemAmount();
                }
            }
        }
        return offer;
    }

    public int[] getMyIds() {
        return getIds(DUEL_INTERFACE_2, MY_OFFER_2);
    }

    public int[] getOtherIds() {
        return getIds(DUEL_INTERFACE_2, OTHER_OFFER_2);
    }

    public int[] getMyStacks() {
        return getStacks(DUEL_INTERFACE_2, MY_OFFER_2);
    }

    public int[] getOtherStacks() {
        return getStacks(DUEL_INTERFACE_2, OTHER_OFFER_2);
    }

    public int myOfferedCashAmount() {
        int[] ids = getMyIds();
        int[] amounts = getMyStacks();
        int my_amount = 0;

        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == 995) {
                my_amount = amounts[i];
                //System.out.println("my Amount: " + my_amount);
                return my_amount;
            }
        }
        return my_amount;
    }

    public int myOfferedAmount() {
        int[] ids = getMyIds();
        int[] amounts = getMyStacks();
        int my_amount = 0;

        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            if (id == -1) {
                continue;
            }
            int amount = amounts[i];
            if (id == 995) {
                my_amount += amount;
            } else {
                int price = getLowestPrice(id);
                my_amount += price * amount;
                ItemDefinition def = ItemDefinition.forId(id);
                if (def != null) {
                    pricemapNames.put(def.getName().toLowerCase(), price);
                }
            }
        }
        return my_amount;
    }

    public int otherOfferedCashAmount() {
        int[] ids = getOtherIds();
        int[] amounts = getOtherStacks();
        int other_amount = 0;

        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == 995) {
                other_amount = amounts[i];
                return other_amount;
            }
        }
        return other_amount;
    }

    public Item[] otherOfferedItems() {
        int[] ids = getOtherIds();
        int[] amounts = getOtherStacks();
        ArrayList<Item> items = new ArrayList<>();

        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            int amount = amounts[i];
            items.add(new Item(null, id, amount));

        }
        return items.toArray(new Item[items.size()]);
    }

    public int otherOfferedAmount() {
        int[] ids = getOtherIds();
        int[] amounts = getOtherStacks();
        int other_amount = 0;

        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            int amount = amounts[i];
            if (id <= 0 || ignoreList.contains(id)) {
                continue;
            }
            System.out.println("Item: " + id + ": " + amount);
            if (id == 995) {
                other_amount += amount;
            } else if (id == 13204) {
                other_amount += amount * 1000;
            } else {
                int price = getLowestPrice(id);
                other_amount += price * amount;
                ItemDefinition def = ItemDefinition.forId(id);
                if (def != null) {
                    pricemapNames.put(def.getName().toLowerCase(), price);
                }
            }
        }
        return other_amount;
    }

    public int otherOfferedAmountThird() {
        if (!isThirdScreenOpen()) {
            return -1;
        }
        int total = 0;
        final String otherOfferString = widgets.get(DUEL_INTERFACE_3, OTHER_OFFER_3).getMessage();
        if (otherOfferString != null && !otherOfferString.contains("0 gp")) {
            String[] other_items = otherOfferString.split("<br>");
            for (String item : other_items) {
                item = item.replaceAll("\\<[^>]*>", "");
                String name = "";
                int amount = 0;
                if (!item.contains(" x ")) {
                    amount = 1;
                    name = item;
                } else if (item.contains(" x ")) {
                    String amountStr = item.substring(item.lastIndexOf(" x ") + 3).replaceAll(",", "");
                    if (amountStr.contains("(")) {
                        amount = Integer.parseInt(amountStr.substring(amountStr.lastIndexOf("(") + 1, amountStr.lastIndexOf(")")));
                    } else {
                        amount = Integer.parseInt(amountStr);
                    }
                    name = item.substring(0, item.lastIndexOf(" x ") - 1);
                }
                int price = name.equalsIgnoreCase("Coins") || !pricemapNames.containsKey(name.toLowerCase()) ? 1 : pricemapNames.get(name.toLowerCase());
                total += amount * price;
                //System.out.println("other third trade: " + name + ", amount: " + amount + ", value: " + amount * price);
            }
        }
        return total;
    }

    public int myOfferedAmountThird() {
        if (!isThirdScreenOpen()) {
            return -1;
        }
        int total = 0;
        final String myOfferString = widgets.get(DUEL_INTERFACE_3, MY_OFFER_3).getMessage();
        //System.out.println("Text of my offer third interface: " + myOfferString);
        if (myOfferString != null && !myOfferString.contains("Absolutely nothing")) {
            String[] other_items = myOfferString.split("<br>");
            for (String item : other_items) {
                item = item.replaceAll("\\<[^>]*>", "");
                String name = "";
                int amount = 0;
                if (!item.contains(" x ")) {
                    amount = 1;
                    name = item;
                } else if (item.contains(" x ")) {
                    String amountStr = item.substring(item.lastIndexOf(" x ") + 3).replaceAll(",", "");
                    if (amountStr.contains("(")) {
                        amount = Integer.parseInt(amountStr.substring(amountStr.lastIndexOf("(") + 1, amountStr.lastIndexOf(")")));
                    } else {
                        amount = Integer.parseInt(amountStr);
                    }
                    name = item.substring(0, item.lastIndexOf(" x "));
                }
                int price = name.equals("Coins") || !pricemapNames.containsKey(name.toLowerCase()) ? 1 : pricemapNames.get(name.toLowerCase());
                total += amount * price;
                //System.out.println("my third trade: " + name + ", amount: " + amount + ", value: " + amount * price);
            }
        }
        return total;
    }

    public boolean declineFirst() {
        RS2Widget closing = widgets.get(DUEL_INTERFACE_1, DECLINE_1);
        if (closing != null && closing.isVisible() && closing.interact()) {
            waitFor(2000, new Condition() {
                @Override
                public boolean evaluate() {
                    return !validateWidget(DUEL_INTERFACE_1);
                }
            });
        }
        return !isStakeScreenOpen();
    }

    public boolean declineSecond() {
        RS2Widget closing = widgets.get(DUEL_INTERFACE_2, DECLINE_2);
        if (closing != null && closing.isVisible() && closing.interact()) {
            waitFor(2000, new Condition() {
                @Override
                public boolean evaluate() {
                    return !validateWidget(DUEL_INTERFACE_2);
                }
            });
        }
        return !isStakeScreenOpen();
    }

    public boolean declineThird() {
        RS2Widget closing = widgets.get(DUEL_INTERFACE_3, DECLINE_3);
        if (closing != null && closing.isVisible() && closing.interact()) {
            waitFor(2000, new Condition() {
                @Override
                public boolean evaluate() {
                    return !validateWidget(DUEL_INTERFACE_3);
                }
            });
        }
        return !isStakeScreenOpen();
    }

    public boolean shouldAccept(int myRequired, int otherRequired) {
        int myAmount = myOfferedAmountThird();
        int otherAmount = otherOfferedAmountThird();

        boolean accept;
        //System.out.println("my amount: " + myAmount + ", required: " + myRequired);
        //System.out.println("other amount: " + otherAmount + ", required: " + otherRequired);

        accept = (myRequired == -1 && otherRequired == -1 && myAmount <= otherAmount)
                || (myRequired != -1 && otherRequired != -1 && myAmount <= myRequired && otherAmount >= otherRequired);//accept if he offers more

        //System.out.println("should accept: " + accept);
        return accept;
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
            if (isChallenge(lastMessage)) {
                String username = lastMessage.replaceAll("<[^>]*>", "").replaceAll(" wishes to duel with you.", "").replaceAll("\u00A0", " ").trim();
                Player player = getPlayer(username);
                System.out.println("Username: " + username + ", player null: " + (player == null));
                if (player == null
                        || distance(player) > maxDistance
                        || (blacklist != null && blacklist.contains(username))
                        ) {
                    System.out.println("Ignore player " + (player == null ? "player is null" : "distance: " + distance(player) + "too long"));
                    sleep(200, 250);
                    return false;
                }
                sleep(500, 1000);
                int width = challengeMessageRect.width;
                int x = challengeMessageRect.x;
                int max_w = challengeMessageRect.x + width;
                int length = challengeMessageRect.height;
                int y = challengeMessageRect.y;
                int max_y = challengeMessageRect.y + length;
                final Point p = new Point(nextGaussian(x, max_w, x + (width / 2), (width / 2) / 2), nextGaussian(y, max_y, y + (length / 2), (length / 2) / 2));
                if (mouse.move(new PointDestination(bot, p))) {
                    List<Option> menuItems = menu.getMenu();
                    if (challengeMessageRectangle.getBoundingBox().contains(mouse.getPosition())
                            && menuItems != null) {
                        //System.out.println("Challenge was found");
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

    public boolean isChallenge(String text) {
        return text.startsWith("<col=7e3200>");
    }


    public Player getClosePlayer(final String n) {
        final String name = n.replaceAll("\u00A0", " ");
        return players.singleFilter(players.getAll(), new Filter<Player>() {
            @Override
            public boolean match(Player p) {
                return p != null && distance(p) < 10 && p.getName().replaceAll("\u00A0", " ").equalsIgnoreCase(name);
            }
        });

    }

    public Rectangle getCenterPoint(Player p) {
        Position t = p.getPosition();
        if (t != null && t.getPolygon(bot) != null && t.getPolygon(bot).getBounds() != null) {
            Rectangle rect = t.getPolygon(bot).getBounds();

            return new Rectangle((int) rect.getCenterX() - 4, (int) rect.getCenterY() - 4, 8, 8);
        }
        return null;
    }

    public boolean attackOpponent() {
        if (opponent == null) {
            return false;
        } else if (!canAttackPlayer) {
            if (!checkAttackThread.isAlive()) {
                checkAttackThread.start();
            }
        } else {
            if (camera.getPitchAngle() < 93) {
                camera.movePitch(random(94, 99));
            }
            Character<?> check = myPlayer().getInteracting();
            if (check == null || !check.getName().equalsIgnoreCase(opponent.getName()) || !check.isHitBarVisible()) {
                if (opponent.getPosition().isVisible(bot)) {
                    Rectangle target = getCenterPoint(opponent);
                    if (target != null && interact.interact(new RectangleDestination(bot, target), "Fight", opponent.getName(), false)) {
                        waitFor(100, new Condition() {
                            @Override
                            public boolean evaluate() {
                                final Character check = myPlayer().getInteracting();
                                return check != null && check.getName().equalsIgnoreCase(opponent.getName());
                            }
                        });
                    }
                } else {
                    camera.toPosition(opponent.getPosition());
                }
            }
            try {
                sleep(nextGaussian(50, 500, 200, 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final Character opp = myPlayer().getInteracting();
            return opp != null && opp.getName().equalsIgnoreCase(opponent.getName());
        }
        return false;
    }

    public boolean fight(Player p, final RuleSet mode) {
        if (p == null) {
            return false;
        }
        opponent = p;
        if (mode == RuleSet.DDS && atCombat.getSpecialAttack() >= 25) {
            if (!Settings.Weapon.DRAGON_DAGGER.isWearing(this)) {
                openInventory();
                if (Settings.Weapon.DRAGON_DAGGER.equip(this)) {
                    waitFor(1000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return Settings.Weapon.DRAGON_DAGGER.isWearing(parent);
                        }
                    });
                }
            } else if (tabs.getOpen() != Tab.ATTACK) {
                if (tabs.open(Tab.ATTACK)) {
                    waitFor(1000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return tabs.getOpen() == Tab.ATTACK;
                        }
                    });
                }
            }
            if (Settings.Weapon.DRAGON_DAGGER.isWearing(this) && tabs.getOpen() == Tab.ATTACK) {
                if (!combat.isSpecialActivated()) {
                    combat.toggleSpecialAttack(true);
                    waitFor(1000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return combat.isSpecialActivated();
                        }
                    });
                }
                if (combat.isSpecialActivated()) {
                    return attackOpponent();
                }
            }
        } else {
            if (!mode.getWeapon().isWearing(this)) {
                if (mode.getWeapon().equip(this)) {
                    waitFor(2000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return mode.getWeapon().isWearing(parent);
                        }
                    });
                }
            }
            if (mode.getWeapon().isWearing(this)) {
                return attackOpponent();
            }
        }
        return false;
    }

    public int[] getOtherSkills() {
        return new int[]{Integer.parseInt(widgets.get(DUEL_INTERFACE_1, ATT_LEVEL_REAL).getMessage()), Integer.parseInt(widgets.get(DUEL_INTERFACE_1, STR_LEVEL_REAL).getMessage()),
                Integer.parseInt(widgets.get(DUEL_INTERFACE_1, DEF_LEVEL_REAL).getMessage()), Integer.parseInt(widgets.get(DUEL_INTERFACE_1, HP_LEVEL_REAL).getMessage()),
                Integer.parseInt(widgets.get(DUEL_INTERFACE_1, PRAY_LEVEL_REAL).getMessage()), Integer.parseInt(widgets.get(DUEL_INTERFACE_1, RANG_LEVEL_REAL).getMessage()),
                Integer.parseInt(widgets.get(DUEL_INTERFACE_1, MAG_LEVEL_REAL).getMessage())};
    }

    public int[] getMySkills() {
        return new int[]{skills.getStatic(Skill.ATTACK), skills.getStatic(Skill.STRENGTH), skills.getStatic(Skill.DEFENCE),
                skills.getStatic(Skill.HITPOINTS), skills.getStatic(Skill.PRAYER), skills.getStatic(Skill.RANGED), skills.getStatic(Skill.MAGIC)};
    }

    public int getLowestPrice(int id) {
        double price = -1;
        if (id <= 0) {
            return 0;
        }
        if (pricemap.containsKey(id) && pricemap.get(id) > -1) {
            return pricemap.get(id);
        }
        JsonElement el = GrandExchange.getItemInfo(id);
        if (!el.isJsonNull()) {
            JsonElement p = el.getAsJsonObject().get("price");
            if (p != null) {
                int tempPrice = p.getAsInt();
                if (tempPrice > 1) {
                    price = tempPrice;
                    System.out.println("RSBots API Price: " + price);
                }
            }
        }
        switch (id) {
            case 8016://enchant sapphire
                price = 800;
                break;
            default:
                try {
                    System.out.println("Getting price for: " + id);
                    URLConnection conn = new URL(GrandExchange.GE_API_URL_BASE + id).openConnection();
                    JsonParser parser = new JsonParser();
                    InputStreamReader ipr = new InputStreamReader(conn.getInputStream());
                    BufferedReader br = new BufferedReader(ipr);
                    String s;
                    JsonElement parsed = parser.parse(ipr);
                    if (parsed != null) {
                        el = parsed.getAsJsonObject().get("item");
                        if (el != null) {
                            JsonObject item = el.getAsJsonObject();
                            if (item != null) {
                                JsonElement curr = item.get("current");
                                if (curr != null) {
                                    JsonObject itemObj = curr.getAsJsonObject();
                                    if (itemObj != null) {
                                        JsonElement priceEl = itemObj.get("price");
                                        if (priceEl != null) {
                                            String priceString = priceEl.getAsString().replaceAll(",", "");
                                            double p;
                                            if (priceString.contains("m")) {
                                                p = Double.parseDouble(priceString.replaceAll("m", "")) * 1000000;
                                            } else if (priceString.contains("k")) {
                                                p = Double.parseDouble(priceString.replaceAll("k", "")) * 1000;
                                            } else {
                                                p = Double.parseDouble(priceString);
                                            }
                                            System.out.println("RSGE Price: " + p);
                                            if (p > 500000 && price < p / 2) {
                                                addToForbidden(id);
                                            }
                                            price = p < price ? p : price;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ipr.close();
                } catch (JsonParseException | IllegalStateException e) {
                    e.printStackTrace();
                } catch (MalformedURLException ignored) {

                } catch (FileNotFoundException e) {
                    return -1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        pricemap.put(id, (int) price);
        return (int) price;
    }

    public final Thread checkAttackThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Timer attackTimer = new Timer(5000);
            while (!attackTimer.isFinished()) {
                try {
                    if (opponent != null) {
                        String text = opponent.getHeadMessage();
                        if (text.equals("3")) {
                            break;
                        }
                    }
                    Thread.sleep(nextGaussian(50, 150, 100, 50));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            canAttackPlayer = true;
        }
    }, "CanAttackPlayer Thread");

    private void addToForbidden(int id) {
        ignoreList.add(id);

        JsonObject obj = new JsonObject();
        obj.add("item_id", new JsonPrimitive(id));
        Updater.sendJSON("http://parent.rsbots.org/item/forbidden", "POST", obj);

        ItemDefinition def = ItemDefinition.forId(id);
        System.out.println("Added To Forbidden: " + (def != null ? def.getName() : id));
    }
}