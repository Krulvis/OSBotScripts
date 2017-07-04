package api.wrappers;

import api.ATMethodProvider;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.utility.Condition;

import java.awt.*;

/**
 * Created by Joep on 04-Jul-17.
 */
public class ATTrade extends ATMethodProvider {

    public ATTrade(ATMethodProvider parent) {
        init(parent);
    }

    public String[] getOtherInv2() {
        return getRawInv2(OTHER_OFFER_MESSAGE);
    }

    public String[] getMyInv2() {
        return getRawInv2(MY_OFFER_MESSAGE);
    }

    public int[] getOtherAmount2() {
        return getAmount2(OTHER_OFFER_MESSAGE);
    }

    public int[] getMyAmount2() {
        return getAmount2(OTHER_OFFER_MESSAGE);
    }

    public int[] getMyInv() {
        return getInv(TRADE_MY_ITEMS_CONTAINER);
    }

    public int[] getMyInvStacks() {
        return getInvStacks(TRADE_MY_ITEMS_CONTAINER);
    }

    public int[] getOtherInv() {
        return getInv(TRADE_OTHER_ITEMS_CONTAINER);
    }

    public int[] getOtherInvStacks() {
        return getInvStacks(TRADE_OTHER_ITEMS_CONTAINER);
    }

    public int myAmountOf(int itemId) {
        return amountOf(itemId, getMyInv(), getMyInvStacks());
    }

    public int otherAmountOf(int itemId) {
        return amountOf(itemId, getOtherInv(), getOtherInvStacks());
    }

    private int amountOf(int itemId, int[] ids, int[] stacks) {
        int amount = 0;
        if (getOtherInv() != null && getOtherInv().length > 0) {
            for (int slot = 0; slot < ids.length; slot++) {
                int id = ids[slot];
                int stack = stacks[slot];
                if (id == itemId) {
                    amount += stack;
                }
            }
        }
        return amount;
    }

    public String[] getRawInv2(int child) {
        RS2Widget wc = widgets.get(TRADE_INTERFACE_2, child);
        RS2Widget[] children = wc.getChildWidgets();
        String[] names = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            RS2Widget childi = children[i];
            if (childi == null || childi.getMessage() == null) {
                names[i] = "Null";
                continue;
            }
            names[i] = childi.getMessage();
        }
        return names;
    }

    public String getSingleStringSecondTrade(String[] string) {
        String result = "";
        if (string != null && string.length > 0) {
            for (int i = 0; i < string.length; i++) {
                String s = string[i];
                result += s;
                if (i < string.length - 1) {
                    result += " , ";
                }
            }
        }
        return result;
    }

    public int[] getInv(int child) {
        RS2Widget myOffer = widgets.get(TRADE_INTERFACE_1, child);
        RS2Widget[] myOfferChildren = myOffer.getChildWidgets();
        int[] items = null;
        if (myOfferChildren != null) {
            items = new int[myOfferChildren.length];
            for (int i = 0; i < myOfferChildren.length; i++) {
                if (myOfferChildren[i] == null) {
                    items[i] = 0;
                    continue;
                }
                int itemId = myOfferChildren[i].getItemId();
                items[i] = itemId;
            }
        }
        return items;
    }

    public int[] getInvStacks(int child) {
        RS2Widget myOffer = widgets.get(TRADE_INTERFACE_1, child);
        RS2Widget[] myOfferChildren = myOffer.getChildWidgets();
        int[] items = null;
        if (myOfferChildren != null) {
            items = new int[myOfferChildren.length];
            for (int i = 0; i < myOfferChildren.length; i++) {
                if (myOfferChildren[i] == null) {
                    items[i] = 0;
                    continue;
                }
                int itemId = myOfferChildren[i].getItemAmount();
                items[i] = itemId;
            }
        }
        return items;
    }

    public String[] getNames2(int child) {
        String[] items = getRawInv2(child);
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                String itemName = items[i];
                itemName = itemName.replaceAll("\\<[^>]*>", "");
                if (itemName.contains(" x ")) {
                    itemName.substring(0, itemName.lastIndexOf(" x "));
                }
                items[i] = itemName;
            }
        }
        return items;
    }

    public int[] getAmount2(int child) {
        String[] items = getRawInv2(child);
        int[] itemAmounts = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null || items[i].equalsIgnoreCase("Null")) {
                itemAmounts[i] = -1;
                continue;
            }
            String name = items[i].replaceAll("\\<[^>]*>", "");
            int amount = 1;
            if (name.contains(" x ")) {
                String amountStr = name.substring(name.lastIndexOf(" x ") + 3);
                if (amountStr.contains("(")) {
                    amount = Integer.parseInt(amountStr.substring(amountStr.lastIndexOf("(") + 1, amountStr.lastIndexOf(")")));
                } else {
                    amount = Integer.parseInt(amountStr);
                }
            }
            itemAmounts[i] = amount;
        }

        return itemAmounts;
    }

    public boolean isFirstTradeOpen() {
        return validateWidget(TRADE_INTERFACE_1);
    }

    public boolean isSecondTradeOpen() {
        return validateWidget(TRADE_INTERFACE_2);
    }

    public boolean isSecondTradeEmpty() {
        return validateWidget(TRADE_INTERFACE_2)
                && widgets.get(TRADE_INTERFACE_2, OTHER_OFFER_MESSAGE).getChildWidget(0).getMessage().equals("Absolutely nothing!");
    }

    public boolean isSecondTradeAccepted() {
        return getWidgetWithText(334, "Waiting for other player...") != null;
    }

    public boolean isFirstTradeAccepted() {
        return getWidgetWithText(335, "Waiting for other player...") != null;
    }

    public boolean acceptSecondTrade() {
        return isSecondTradeAccepted() || widgets.get(TRADE_INTERFACE_2, ACCEPT_BUTTON_2).interact("Accept");
    }

    public boolean acceptFirstTrade() {
        return isFirstTradeAccepted() || widgets.get(TRADE_INTERFACE_1, ACCEPT_BUTTON_1).interact("Accept");
    }

    public boolean isTradeOpen() {
        return isFirstTradeOpen() || isSecondTradeOpen();
    }

    public boolean offerExact(int id, int amount) {
        int required = amount - myAmountOf(id);
        if (required < 0) {
            return remove(id, -required);
        } else if (required > 0) {
            return offer(id, required);
        }
        return true;
    }

    public boolean offer(int id, int amount) {
        Item item = inventory.getItem(id);
        if (item != null && isFirstTradeOpen()) {
            switch (amount) {
                case OFFER_1:
                    return item.interact("Offer");
                case OFFER_5:
                    return item.interact("Offer-5");
                case OFFER_10:
                    return item.interact("Offer-10");
                case OFFER_ALL:
                    return item.interact("Offer-All");
                default:
                    if (!waitingForInput() && item.interact("Offer-X")) {
                        waitFor(2000, new Condition() {
                            @Override
                            public boolean evaluate() {
                                return waitingForInput();
                            }
                        });
                    }
                    if (waitingForInput()) {
                        return handleFillInX(amount);
                    }
            }
        } else {
            System.out.println("Can't find item!");
        }
        return false;
    }

    public boolean remove(int id, int amount) {
        if (isFirstTradeOpen()) {
            int[] ids = getMyInv();
            if (waitingForInput()) {
                return handleFillInX(amount);
            }
            for (int slot = 0; slot < ids.length; slot++) {
                int itemId = ids[slot];
                if (itemId == -1) {
                    continue;
                }
                if (itemId == id) {
                    return removeAtSlot(slot, amount);
                }
            }
            return false;
        } else {
            return false;
        }
    }

    private boolean removeAtSlot(int slot, int amount) {
        System.out.println("test withdrawatslot called?");
        int[] ids = getMyInv();
        if (slot < 0 || slot > ids.length) {
            return false;
        }
        Point center = getTradePoint(slot);
        Rectangle rect = new Rectangle(center.x - 10, center.y - 10, 20, 20);
        switch (amount) {
            case REMOVE_1:
                return interact.interact(rect, "Remove");
            case REMOVE_5:
                return interact.interact(rect, "Remove-5");
            case REMOVE_10:
                return interact.interact(rect, "Remove-10");
            case REMOVE_ALL:
                return interact.interact(rect, "Remove-All");
            default:
                if (!waitingForInput() && interact.interact(rect, "Remove-X")) {
                    waitFor(1500, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return waitingForInput();
                        }
                    });
                }
                if (waitingForInput()) {
                    return handleFillInX(amount);
                }
                break;
        }
        return false;
    }

    public boolean offer1(int id) {
        return offer(id, OFFER_1);
    }

    public boolean offer5(int id) {
        return offer(id, OFFER_5);
    }

    public boolean offer10(int id) {
        return offer(id, OFFER_10);
    }

    public boolean offerAll(int id) {
        return offer(id, OFFER_ALL);
    }

    public Point getTradePoint(int slot) {
        return getTradePoint(slot, true);
    }

    public Point getTradePoint(int slot, boolean mine) {
        int col = (slot % 4);
        int row = (slot / 4);
        int x = 41 + (col * 48);
        int y = 96 + (row * 32);

        return new Point(x + (mine ? 0 : 288), y + 3);
    }

    //First Screen
    public final int TRADE_INTERFACE_1 = 335;
    public final int TRADE_MY_ITEMS_CONTAINER = 25;
    public final int TRADE_OTHER_ITEMS_CONTAINER = 28;
    public final int ACCEPT_BUTTON_1 = 11;
    public final int DECLINE_BUTTON_1 = 14;
    public final int MY_VALUE_1 = 24;
    public final int OTHER_VALUE_1 = 27;
    public final int OTHER_PLAYER_1 = 31;

    //Second screen
    public final int TRADE_INTERFACE_2 = 334;
    public final int ACCEPT_BUTTON_2 = 13;
    public final int DECLINE_BUTTON_2 = 14;
    public final int MY_OFFER_MESSAGE = 28;
    public final int OTHER_OFFER_MESSAGE = 29;
    public final int MY_VALUE_2 = 23;
    public final int OTHER_VALUE_2 = 24;
    public final int OTHER_PLAYER_2 = 30;

    public final int OFFER_1 = 1;
    public final int OFFER_10 = 10;
    public final int OFFER_5 = 5;
    public final int OFFER_ALL = -1;
    public final int REMOVE_1 = 1;
    public final int REMOVE_10 = 10;
    public final int REMOVE_5 = 5;
    public final int REMOVE_ALL = -1;
}
