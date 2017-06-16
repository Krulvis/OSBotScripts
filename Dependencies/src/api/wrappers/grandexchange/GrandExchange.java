package api.wrappers.grandexchange;

import api.ATMethodProvider;
import api.util.Random;
import api.util.Timer;
import com.google.gson.*;
import org.osbot.rs07.api.def.ItemDefinition;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.utility.Condition;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.osbot.rs07.api.GrandExchange.*;

/**
 * Created by Krulvis on 11-Apr-17.
 */
public class GrandExchange extends ATMethodProvider {

    public final static String RSBOTS_API_URL = "http://api.rsbots.org/item/";
    public static final String GE_API_URL_BASE = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=";
    public static HashMap<Integer, Integer> onlinePriceMap = new HashMap<>(), highAlchPrice = new HashMap<>();
    public static HashMap<Integer, String> itemNames = new HashMap<>();
    public HashMap<Integer, Integer> buyTries = new HashMap<>();
    public static HashMap<Integer, Timer> priceRetrievalTimers = new HashMap<>();
    private Box[] boxes = new Box[]{Box.BOX_1, Box.BOX_2, Box.BOX_3, Box.BOX_4, Box.BOX_5, Box.BOX_6, Box.BOX_7, Box.BOX_8};

    public GrandExchange(ATMethodProvider parent) {
        init(parent);
    }

    private final int GE_WIDGET_ID = 465;
    private final int MAIN_WIDGET_ID = 2;
    private final int COLLECT_SCREEN_ID = 15;
    private final int COLLECT_BUTTON_PARENT_ID = 6;
    private final int BUY_SELL_SCREEN_ID = 24;
    private final int BASE_PRICE_ID = 26;
    private final int BACK_BUTTON_ID = 4;
    private final int FIRST_SLOT_ID = 7;
    private final int COUNT_SETTING_ID = 563;
    private final int ITEM_ID_SETTING_ID = 1151;
    private final int CURRENT_PRICE_SETTING_ID = 1043;
    private final int CURRENT_COLLECT_TAB_SETTING_ID = 638;
    public final Position GE_LOCATION = new Position(3164, 3487, 0);

    /*
    -------------------------- BUY / SELL METHODS -------------------------------------------
     */

    public boolean sell(final int id, final int percent) {
        final ItemDefinition def = ItemDefinition.forId(id);
        final int[] ids;
        final int noted = id == 6332 ? 8836 : id + 1;
        if (def != null && def.getNotedId() == -1) {
            ids = new int[]{id};
        } else {
            ids = new int[]{id, noted};
        }
        if (inventory.getAmount(ids) == 0 && getOffer(id, false) == null) {
            return true;
        }
        if (inventory.contains(ids)) {
            final Item i = inventory.getItem(ids);
            if (placeSellOfferPercentage(i, (int) inventory.getAmount(ids), percent)) {
                waitFor(new Condition() {
                    @Override
                    public boolean evaluate() {
                        GrandExchangeOffer offer = getOffer(id, false);
                        return offer != null && offer.getStatus() == Status.FINISHED_SALE;
                    }
                }, 3000);
            }
        }
        if (!inventory.contains(ids)) {
            GrandExchangeOffer offer = getOffer(id, false);
            if (offer.getStatus() != Status.FINISHED_SALE) {
                if (abortOffer(offer)) {
                    waitFor(new Condition() {
                        @Override
                        public boolean evaluate() {
                            GrandExchangeOffer offer = getOffer(id, false);
                            return offer.getStatus() == Status.FINISHED_SALE;
                        }
                    }, 2000);
                    if (offer.getItemsTransferredCount() < offer.getTotalItemsToTransfer()) {
                        sellPricePercentMap.put(id, (percent - 2));
                    }
                }
            }
            if (offer.getStatus() == Status.FINISHED_SALE) {
                if (offer.getItemsTransferredCount() > 0) {
                    prices.setSellPrice(id, offer.getCoinsTransferred() / offer.getItemsTransferredCount());
                }
                final int count = (int) inventory.getAmount(995);
                if (collectAll()) {
                    waitFor(new Condition() {
                        @Override
                        public boolean evaluate() {
                            return inventory.getAmount(995) > count;
                        }
                    }, 2000);
                }
            }
        }
        return inventory.getAmount(ids) == 0 && getOffer(id, false) == null;
    }

    public boolean placeSellOfferPercentage(final Item item, final int quantity, final int percentage) {
        return placeSellOffer(item, quantity, -percentage);
    }

    public boolean placeSellOffer(final Item item, int quantity, int price) {
        if (item == null) {
            return false;
        }
        final int freeSlot = getRandomFreeSlot();
        if (freeSlot == -1) {
            return false;
        }
        if (quantity == 0 || quantity == -1) {
            quantity = (int) inventory.getAmount(item.getId());
        }
        if (isMainScreenOpen() || isSellOptionsOpen()) {
            final int currentID = getCurrentID();
            if ((item.getId() != 8836 && currentID != item.getId() && currentID != (item.getId() - 1)) || (item.getId() == 8836 && currentID != 6332)) {
                item.interact();
                return false;
            }
            if (getCurrentQuantity() != quantity) {
                setQuantity(quantity, true);
            } else {
                if (price < 0) {
                    final int basePrice = getBasePrice();
                    if (basePrice == -1) {
                        return false;
                    }
                    // System.out.println("(SELL) Using percentage: " + price + ", basePrice: " + basePrice);
                    price = (int) Math.ceil((-price * basePrice) / 100.0D);
                }
                int basePrice = getCurrentPrice();
                //System.out.println("(SELL) Base price for: " + getCurrentID() + ": "+ basePrice);
                if (setPrice(item.getDefinition().isNoted() ? item.getId() - 1 : item.getId(), price, false)) {
                    return pressConfirm();
                }
            }
        } else {
            //System.out.println("Opening Main screen TOPBOT CODE");
            openMainScreen();
        }
        return false;
    }

    /**
     * Buying item until amount is reached, keeps updating price once item doesn't buy
     *
     * @param id
     * @param amount
     * @param startPercent
     * @return
     */
    public boolean buy(final int id, final int amount, final int startPercent) {
        if (getBuyPricePercent(id) < startPercent) {
            buyPricePercentMap.put(id, startPercent);
        }
        final int percent = getBuyPricePercent(id);
        final ItemDefinition def = ItemDefinition.forId(id);
        final int[] ids;
        final int noted = id == 6332 ? 8836 : id + 1;
        if (def != null && def.getNotedId() == -1) {
            ids = new int[]{id};
        } else {
            ids = new int[]{id, noted};
        }
        if (inventory.getAmount(ids) >= amount) {
            return true;
        }
        final GrandExchangeOffer offer = getOffer(id, true);
        if (offer == null) {
            if (placeBuyOfferPercentage(id, amount - (int) inventory.getAmount(ids), percent)) {
                waitFor(new Condition() {
                    @Override
                    public boolean evaluate() {
                        GrandExchangeOffer offer = getOffer(id, true);
                        return offer != null && offer.getStatus() == Status.FINISHED_BUY;
                    }
                }, Random.nextGaussian(3000, 5000, 1000));
            }
        } else if (offer.getStatus() == Status.FINISHED_BUY) {
            if (offer.getItemsTransferredCount() > 0) {
                prices.setBuyPrice(id, offer.getCoinsTransferred() / offer.getItemsTransferredCount());
                //System.out.println("Bought: " + def.getName() + ", for: " + Prices.getBuyPrice(id) + " each.");
            }
            if (collectAll(true)) {
                waitFor(new Condition() {
                    @Override
                    public boolean evaluate() {
                        return (int) inventory.getAmount(id) >= amount;
                    }
                }, 2000);
            }
        } else if (abortOffer(offer)) {
            waitFor(new Condition() {
                @Override
                public boolean evaluate() {
                    GrandExchangeOffer offer = getOffer(id, true);
                    return offer.getStatus() == Status.FINISHED_BUY;
                }
            }, 2000);
            if (offer.getTotalItemsToTransfer() > offer.getItemsTransferredCount()) {
                addBuyTry(id);
                final int price = getPrice(id);
                buyPricePercentMap.put(id, price < 100 ? percent + 40 : price < 1000 ? (percent + 10) : (percent + 5));
                System.out.println("Increasing " + id + ", price: " + buyPricePercentMap.get(id) + ", buyTries: " + getBuyTries(id));
            }
        }
        return (int) inventory.getAmount(ids) >= amount;
    }

    public boolean placeBuyOfferPercentage(final int itemID, final int quantity, final int percentage) {
        final int freeSlot = getRandomFreeSlot();
        //System.out.println("Slot: " + freeSlot);
        if (freeSlot == -1) {
            return false;
        }
        return placeBuyOffer(itemID, quantity, -percentage, freeSlot);
    }

    public boolean placeBuyOffer(final int itemID, final int quantity, int price, final int slot) {
        if (isMainScreenOpen()) {
            //System.out.println("Opening buy option");
            final RS2Widget buyButton = getBuyButton(slot);
            if (buyButton != null) {
                buyButton.interact();
                waitFor(new Condition() {
                    @Override
                    public boolean evaluate() {
                        return isSearchOpen();
                    }
                }, 3000);
                if (!isSearchOpen()) {
                    return false;
                }
            }
        }
        if (isSearchOpen()) {
            //System.out.println("Search is open!");
            final ItemDefinition def = ItemDefinition.forId(itemID);
            String name;
            if (def == null) {
                name = getItemName(itemID);
            } else {
                name = def.getName();
            }
            //final Widget mainScreen = Widgets.getWidget(548);
            final RS2Widget searchInputWidget = getWidgetChild(162, new Filter<RS2Widget>() {
                @Override
                public boolean match(final RS2Widget v) {
                    return v.getMessage().contains("What would you like to buy?");
                }
            });
            if (searchInputWidget != null && searchInputWidget.isVisible()) {
                String txt = searchInputWidget.getMessage();
                String currentSearch = txt.substring(txt.indexOf("</col>") + 7, txt.length() - (txt.endsWith("*") ? 1 : 0));
                if (!currentSearch.equalsIgnoreCase(name.replaceAll("\\(\\d\\)", ""))) {
                    for (int i = 0; i < currentSearch.length(); i++) {
                        if (name.startsWith(currentSearch)) {
                            break;
                        }
                        backSpace();
                    }
                    txt = searchInputWidget.getMessage() != null ? searchInputWidget.getMessage() : "";
                    currentSearch = txt.substring(txt.contains("</col>") ? txt.indexOf("</col>") + 7 : 0, txt.length() - (txt.endsWith("*") ? 1 : 0));
                    //System.out.println("Cur search: " + currentSearch + ", name: " + name);
                    final String charactersRemaining = name.length() <= currentSearch.length() ? "" : name.substring(currentSearch.length()).toLowerCase().replaceAll("\\(\\d\\)", "");
                    keyboard.typeString(charactersRemaining, false);
                    waitFor(new Condition() {
                        @Override
                        public boolean evaluate() {
                            return searchInputWidget.getMessage().equalsIgnoreCase(name.replaceAll("\\(\\d\\)", ""));
                        }
                    }, 1500);
                }
                txt = searchInputWidget.getMessage();
                currentSearch = txt.substring(txt.length() > 7 ? txt.indexOf("</col>") + 7 : 0, txt.length() - 1);
                //System.out.println("Text: " + currentSearch + ", needs to be: " + name.replaceAll("\\(\\d\\)", ""));
                if (currentSearch.equalsIgnoreCase(name.replaceAll("\\(\\d\\)", ""))) {
                    //System.out.println("Clicking item");
                    clickSearchedItem(name);
                }
            } else {

            }
            // TODO
        } else if (isBuyOptionsOpen()) {
            //System.out.println("Buy option is open!");
            if (getCurrentID() != itemID && getCurrentID() > -1) {
                //System.out.println("Current: " + getCurrentID() + ", isn't equal to: " + itemID);
                openMainScreen();
                return false;
            } else if (getCurrentID() == itemID) {
                if (setQuantity(quantity, false)) {
                    //System.out.println("Quantity set");
                    if (setPrice(itemID, price, true)) {
                        if (pressConfirm()) {
                            return true;
                        }
                    }
                }
            }
        } else {
            //System.out.println("Opening main screen");
            openMainScreen();
        }
        return false;
    }

    public boolean setQuantity(final int x, final boolean sell) {
        final int currentQuality = getCurrentQuantity();
        if (x == currentQuality) {
            return true;
        }
        final RS2Widget buySellScreen = getBuySellScreenWidget();
        if (buySellScreen == null) {
            return false;
        }
        final RS2Widget enterAmountButton = getWidgetWithAction(buySellScreen, "Enter quantity");
        if (enterAmountButton != null && enterAmountButton.interact()) {
            waitFor(new Condition() {
                @Override
                public boolean evaluate() {
                    return waitingForInput();
                }
            }, 1000);
            if (handleFillInX(x, sell ? "How many do you wish to sell?" : "How many do you wish to buy?")) {
                waitFor(new Condition() {
                    @Override
                    public boolean evaluate() {
                        return getCurrentQuantity() == x;
                    }
                }, 2000);
            }
            //enterAmountButton.click();
        }
        return getCurrentQuantity() == x;
    }

    public boolean setPrice(int itemId, int price, boolean buy) {
        if (price < 0) {
            //int currentPrice = buy ? Prices.getBuyPrice(itemId) : Prices.getSellPrice(itemId);
            final int basePrice = getBasePrice();
            //System.out.println("(BUY) base price for: " + getCurrentID() + ": " + basePrice);
            if (basePrice == -1) {
                return false;
            }
            price = (int) ((-price * basePrice) / 100.0D);
        }
        System.out.println("Setting price: " + price);
        final int requiredPrice = price;
        final int currentPrice = getCurrentPrice();
        if (price == currentPrice) {
            //System.out.println("correct price");
            return true;
        }
        final RS2Widget buySellScreen = getBuySellScreenWidget();
        if (buySellScreen == null) {
            //System.out.println("Price set widget null");
            return true;
        }
        final RS2Widget enterPriceButton = getWidgetWithAction(buySellScreen, "Enter price");
        if (enterPriceButton != null && enterPriceButton.interact()) {
            //System.out.println("Clicking option");
            waitFor(new Condition() {
                @Override
                public boolean evaluate() {
                    return waitingForInput();
                }
            }, 1000);
            if (handleFillInX(price, "Set a price for each item:")) {
                waitFor(new Condition() {
                    @Override
                    public boolean evaluate() {
                        return getCurrentPrice() == requiredPrice;
                    }
                }, 1000);
            }
            //enterPriceButton.click();
        } else if (enterPriceButton == null) {
            //System.out.println("Cant find button to set price");
        }
        return getCurrentPrice() == requiredPrice;
    }

    /*
    ----------------------------- GENERAL METHODS -------------------------------------------
     */
    public boolean close() {
        if (isOpen()) {
            final RS2Widget mainWidget = widgets.get(GE_WIDGET_ID, MAIN_WIDGET_ID);
            final RS2Widget closeButton = getWidgetWithAction(mainWidget, "Close");
            if (closeButton != null && closeButton.isVisible() && closeButton.interact()) {
                waitFor(2000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return !isOpen();
                    }
                });
            }
        }
        return !isOpen();
    }

    public boolean open() {
        if (!isOpen()) {
            bank.close();
            deselectEverything();
            RS2Object booth = objects.closest(new Filter<RS2Object>() {
                @Override
                public boolean match(RS2Object go) {
                    return go != null && go.getName().equalsIgnoreCase("Grand Exchange booth") && go.hasAction("Exchange");
                }
            });
            if (booth != null && distance(booth) < 15) {
                if (interact(booth, "Exchange")) {
                    waitFor(2000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return isOpen();
                        }
                    });
                }
            } else {
                NPC clerk = npcs.closest("Grand Exchange Clerk");
                if (clerk != null && distance(clerk) <= 15) {
                    System.out.println("Interacting with clerk");
                    if (interact(clerk, "Exchange")) {
                        waitFor(2000, new Condition() {
                            @Override
                            public boolean evaluate() {
                                return isOpen();
                            }
                        });
                    }
                } else if (distance(GE_LOCATION) > 100 && inventory.contains(8007)) {//&& !ROW.hasWith()
                    Item tab = inventory.getItem(8007);
                    if (tab != null && tab.interact()) {
                        waitFor(6000, new Condition() {
                            @Override
                            public boolean evaluate() {
                                return distance(GE_LOCATION) < 90;
                            }
                        });
                    }
                } /*else if (distance(GE_LOCATION) > 25 && ROW.hasWith()) {
                    if (ROW.teleport("ge")) {
                        waitFor(6000, new Condition() {
                            @Override
                            public boolean evaluate() {
                                return distance(GE_LOCATION) < 25;
                            }
                        });
                    }
                }*/ else {
                    walkPath(GE_LOCATION);
                }
            }
        }
        return isOpen();
    }


    public boolean collectAll() {
        return collectAll(true);
    }

    public boolean collectAll(final boolean inventory) {
        if (!isMainScreenOpen()) {
            openMainScreen();
            sleep(200, 300);
        }
        final RS2Widget wc = widgets.get(GE_WIDGET_ID, COLLECT_BUTTON_PARENT_ID);
        if (wc != null && wc.isVisible()) {
            final RS2Widget collectButton = wc.getChildWidget(1);
            if (collectButton != null && collectButton.isVisible()) {
                return interact.interact(collectButton.getBounds(), inventory ? "Collect to inventory" : "Collect to bank");
            }
        }
        return false;
    }

    public boolean abortOffer(final GrandExchangeOffer offer) {
        if (offer == null) {
            return false;
        }
        System.out.println("Aborting offer: " + offer.getItemDef().getName() + ", Slot: " + offer.getSlot());
        if (!isMainScreenOpen()) {
            openMainScreen();
            sleep(200, 300);
        }
        final RS2Widget offerWidget = getSlotWidget(offer.getSlot());
        if (offerWidget == null || !offerWidget.isVisible()) {
            return false;
        }
        return offerWidget.interact("Abort offer");
    }

    public boolean isOpen() {
        final RS2Widget mainChild = widgets.get(GE_WIDGET_ID, MAIN_WIDGET_ID);
        return mainChild != null && mainChild.isVisible();
    }

    public boolean isMainScreenOpen() {
        final RS2Widget mainChild = widgets.get(GE_WIDGET_ID, MAIN_WIDGET_ID);
        return mainChild != null && mainChild.isVisible() && !getGrandExchange().isBuyOfferOpen() && !getGrandExchange().isSellOfferOpen() && !isCollectScreenOpen();
    }

    public GrandExchangeOffer getOfferInSlot(final int slot) {
        final GrandExchangeOffer[] offers = getOffers();
        for (int i = 0; i < offers.length; i++) {
            if (offers[i].getSlot() == slot) {
                return offers[i];
            }
        }
        return null;
    }

    public GrandExchangeOffer getOffer(final int itemID) {
        final GrandExchangeOffer[] offers = getOffers();
        for (int i = 0; i < offers.length; i++) {
            if (offers[i] == null) {
                continue;
            }
            if (offers[i].getItemID() == itemID) {
                return offers[i];
            }
        }
        return null;
    }

    public GrandExchangeOffer getOffer(final String itemName) {
        final GrandExchangeOffer[] offers = getOffers();
        for (int i = 0; i < offers.length; i++) {
            if (offers[i] == null) {
                continue;
            }
            if (offers[i].getItemDef().getName().equalsIgnoreCase(itemName)) {
                return offers[i];
            }
        }
        return null;
    }

    public GrandExchangeOffer getOffer(final int itemId, final boolean buy) {
        return getOffer(new Filter<GrandExchangeOffer>() {
            @Override
            public boolean match(GrandExchangeOffer go) {
                return go != null && go.getItemID() == itemId && (buy == go.isBuyOffer());
            }
        });
    }

    public GrandExchangeOffer getOffer(final Filter<GrandExchangeOffer> filter) {
        if (filter != null) {
            for (GrandExchangeOffer geo : getOffers()) {
                if (geo != null && filter.match(geo))
                    return geo;
            }
        }
        return null;
    }

    /**
     * Gets an array of all active offers
     *
     * @return
     */
    public GrandExchangeOffer[] getOffers() {
        final List<GrandExchangeOffer> offers = new ArrayList<GrandExchangeOffer>();

        for (int i = 0; i < boxes.length; i++) {
            GrandExchangeOffer offer = new GrandExchangeOffer(boxes[i], this);
            if (offer.getItemID() > 0) {
                if (offer.isActive()) {
                    offers.add(offer);
                }
            }
        }
        if (offers.size() == 0) {
            return new GrandExchangeOffer[0];
        }
        return offers.toArray(new GrandExchangeOffer[offers.size()]);
    }

    public int getRandomFreeSlot() {
        final int amountOfSlots = worlds.isMembersWorld() ? 8 : 3;

        final List<Integer> freeSlots = new ArrayList<Integer>();

        for (int i = 0; i < amountOfSlots; i++) {
            if (getGrandExchange().getStatus(getBox(i)) == Status.EMPTY) {
                freeSlots.add(i);
            }
        }
        if (freeSlots.isEmpty()) {
            return -1;
        }
        return freeSlots.get(Random.nextGaussian(0, freeSlots.size(), freeSlots.size() / 2));
    }

    public boolean openMainScreen() {
        if (bank.isOpen()) {
            bank.close();
            return false;
        }
        if (isMainScreenOpen()) {
            return true;
        }
        if (isBuyOptionsOpen() || isSearchOpen() || isSellOptionsOpen() || isCollectScreenOpen()) {
            return pressBack();
        }
        final NPC geClerk = npcs.closest("Grand Exchange Clerk");
        if (geClerk != null && geClerk.isOnScreen()) {
            if (geClerk.interact("Exchange Grand Exchange Clerk")) {
                waitFor(Random.bigSleep(), new Condition() {
                    @Override
                    public boolean evaluate() {
                        return isMainScreenOpen();
                    }
                });
            }
        } else {
            walking.walk(GE_LOCATION);
        }
        return isMainScreenOpen();
    }

    private int getCurrentQuantity() {
        int setting = configs.get(COUNT_SETTING_ID);
        if (setting < 0) {
            setting = 2147483647 + setting + 1;
        }
        return setting;
    }

    public int getCurrentID() {
        return configs.get(ITEM_ID_SETTING_ID);
    }

    private int getBasePrice() {
        final RS2Widget wc = widgets.get(GE_WIDGET_ID, BASE_PRICE_ID);

        if (wc != null && wc.isVisible()) {
            try {
                return Integer.parseInt(wc.getMessage().replace(",", ""));
            } catch (Exception ignroed) {
                ignroed.printStackTrace();
            }
        }
        return -1;
    }

    private int getCurrentPrice() {
        return configs.get(CURRENT_PRICE_SETTING_ID);
    }

    private RS2Widget getBuySellScreenWidget() {
        final RS2Widget buySellScreenChild = widgets.get(GE_WIDGET_ID, BUY_SELL_SCREEN_ID);
        if (buySellScreenChild == null || !buySellScreenChild.isVisible()) {
            return null;
        }
        return buySellScreenChild;
    }

    private RS2Widget getSlotWidget(final int slotID) {
        return widgets.get(GE_WIDGET_ID, FIRST_SLOT_ID + slotID);
    }

    public RS2Widget getSellButton(final int slotID) {
        final RS2Widget slotWidget = getSlotWidget(slotID);
        return slotWidget == null ? null : getWidgetWithAction(slotWidget, "Create <col=ff9040>Sell</col> offer");
    }

    public RS2Widget getBuyButton(final int slotID) {
        final RS2Widget slotWidget = getSlotWidget(slotID);
        return slotWidget == null ? null : getWidgetWithAction(slotWidget, "Create <col=ff9040>Buy</col> offer");
    }

    public boolean isBuyOptionsOpen() {
        return getGrandExchange().isBuyOfferOpen();
    }

    public boolean isSellOptionsOpen() {
        return getGrandExchange().isSellOfferOpen();
    }

    public boolean isSearchOpen() {
        final RS2Widget searchWC = getWidgetChild(162, v -> {
            final String txt = v.getMessage();
            return txt != null && txt.contains("What would you like to buy");
        });
        return searchWC != null && searchWC.isVisible();
    }

    public boolean isCollectScreenOpen() {
        final RS2Widget collectScreenChild = widgets.get(GE_WIDGET_ID, COLLECT_SCREEN_ID);
        return collectScreenChild != null && collectScreenChild.isVisible();
    }

    private boolean pressBack() {
        final RS2Widget backButton = widgets.get(GE_WIDGET_ID, BACK_BUTTON_ID);
        if (backButton == null || !backButton.isVisible()) {
            return false;
        }
        return backButton.interact();
    }

    private boolean pressConfirm() {
        final RS2Widget buySellScreen = getBuySellScreenWidget();
        if (buySellScreen == null) {
            return false;
        }
        final RS2Widget confirmButton = getWidgetWithText(buySellScreen, "<col=ffffff>Confirm</col>");
        if (confirmButton != null) {
            return confirmButton.interact();
        }
        return false;
    }

    private void clickSearchedItem(final String name) {
        final RS2Widget searchResults = getWidgetChild(162, new Filter<RS2Widget>() {
            @Override
            public boolean match(final RS2Widget v) {
                RS2Widget[] children = v.getChildWidgets();
                return children != null && children.length > 0 && v.getWidth() == 485 && v.getHeight() == 104;
            }
        });
        if (searchResults == null || !searchResults.isVisible()) {
            return;
        }
        final RS2Widget searchedItem = getWidgetWithText(searchResults, name);
        if (searchedItem != null && searchedItem.isVisible()) {
            if (scrollTo(searchedItem, searchResults)) {
                sleep(600, 850);
                searchedItem.interact();
            }
        }
    }

    public Box getBox(int slot) {
        return boxes[slot];
    }

    private boolean handleFillInX(final int count, final String msg) {
        //System.out.println("FILL IN X: " + count + ":" + msg);
        final int moduloK = count % 1000;
        final int moduloM = count % 1000000;
        final String numberString = count == 0 ? count + "" : moduloM == 0 ? (count / 1000000) + "m" : moduloK == 0 ? (count / 1000) + "k" : "" + count;
        final RS2Widget ea = getWidgetWithText(162, msg);
        if (ea == null || !ea.isVisible()) {
            return false;
        }
        if (waitingForInput()) {
            keyboard.typeString(numberString, true);
            return true;
        }
        return false;
    }

    /*
    ------------------------------ SAVED BUY / SELL PRICES -----------------------
     */
    public static HashMap<Integer, Integer> buyPricePercentMap = new HashMap<>();
    public static HashMap<Integer, Integer> sellPricePercentMap = new HashMap<>();

    public static int getBuyPricePercent(final int id) {
        if (buyPricePercentMap != null && buyPricePercentMap.containsKey(id)) {
            return buyPricePercentMap.get(id);
        }
        return 100;
    }

    public static int getSellPricePercent(final int id) {
        if (sellPricePercentMap != null && sellPricePercentMap.containsKey(id)) {
            return sellPricePercentMap.get(id);
        }
        return 100;
    }

    public int getBuyTries(final int itemId) {
        return buyTries.containsKey(itemId) ? buyTries.get(itemId) : 0;
    }

    public void addBuyTry(final int itemId) {
        buyTries.put(itemId, buyTries.containsKey(itemId) ? (buyTries.get(itemId) + 1) : 1);
    }

    public void resetBuyTries() {
        buyTries = new HashMap<>();
    }


    /*
     --------------- STATIC METHODS FOR PRICE GETTING FROM WEB -------------------
     */
    public static int getHighAlch(int id) {
        if (highAlchPrice.containsKey(id) && highAlchPrice.get(id) > -1) {
            return highAlchPrice.get(id);
        }
        int price = -1;
        JsonElement el = getItemInfo(id);
        if (el != null && !el.isJsonNull()) {
            JsonElement p = el.getAsJsonObject().get("high_alch_value");
            if (p != null) {
                highAlchPrice.put(id, price = p.getAsInt());
            }
        } else if (el != null && el.isJsonNull()) {
            //return getHighAlch(id - 1);
        }
        return price;
    }

    public static JsonElement getItemInfo(int id) {
        URLConnection conn = null;
        try {
            conn = new URL(RSBOTS_API_URL + id).openConnection();
            JsonParser parser = new JsonParser();
            InputStreamReader ipr = new InputStreamReader(conn.getInputStream());
            JsonElement parsed = parser.parse(ipr);
            if (parsed != null) {
                JsonArray ar = parsed.getAsJsonArray();
                for (JsonElement el : ar) {
                    if (el != null && el.isJsonObject()) {
                        return el.getAsJsonObject();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonNull.INSTANCE;
    }

    public String getItemName(final int unnotedID) {
        JsonElement el = getItemInfo(unnotedID);
        String name = null;
        if (el != null && !el.isJsonNull()) {
            JsonElement p = el.getAsJsonObject().get("name");
            if (p != null) {
                itemNames.put(unnotedID, name = p.getAsString());
            }
        } else if (el != null && el.isJsonNull()) {
            //return getHighAlch(id - 1);
        }
        return name;
    }

    public static int getExchangePrice(final int id) {
        double price = -1;
        switch (id) {
            case 8016://enchant sapphire
                price = 800;
                break;
            default:
                try {
                    System.out.print("Getting GE price for: " + id);
                    URLConnection conn = new URL(GE_API_URL_BASE + id).openConnection();
                    JsonParser parser = new JsonParser();
                    InputStreamReader ipr = new InputStreamReader(conn.getInputStream());
                    BufferedReader br = new BufferedReader(ipr);
                    String s;
                    JsonElement parsed = parser.parse(ipr);
                    if (parsed != null) {
                        JsonElement el = parsed.getAsJsonObject().get("item");
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
                                            if (priceString.contains("m")) {
                                                price = Double.parseDouble(priceString.replaceAll("m", ""));
                                                price = price * 1000000;
                                            } else if (priceString.contains("k")) {
                                                price = Double.parseDouble(priceString.replaceAll("k", ""));
                                                price = price * 1000;
                                            } else {
                                                price = Double.parseDouble(priceString);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ipr.close();
                } catch (JsonParseException | IllegalStateException e) {
                    priceRetrievalTimers.put(id, new Timer(5000));
                    e.printStackTrace();
                } catch (MalformedURLException ignored) {

                } catch (FileNotFoundException e) {
                    return -1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        System.out.println(": " + (int) price);
        return (int) price;
    }

    public static int getPrice(int id) {
        int price = -1;
        switch (id) {
            case 8016://enchant sapphire
                price = 800;
                break;
            default:
                if (id == -1 || id == 0) {
                    return 0;
                }
                if (onlinePriceMap.containsKey(id) && onlinePriceMap.get(id) > -1) {
                    return onlinePriceMap.get(id);
                }
                if (priceRetrievalTimers.containsKey(id) && !priceRetrievalTimers.get(id).isFinished()) {
                    System.out.println("Timeout on price retrieving!");
                    return -1;
                }
                JsonElement el = getItemInfo(id);
                if (!el.isJsonNull()) {
                    JsonElement p = el.getAsJsonObject().get("price");
                    if (p != null && p.getAsInt() > 1) {
                        price = p.getAsInt();
                    } else {
                        price = getExchangePrice(id);
                    }
                } else {
                    price = getExchangePrice(id);
                }
                break;

        }
        if (price > 1) {
            onlinePriceMap.put(id, price);
        }
        return price;
    }
}
