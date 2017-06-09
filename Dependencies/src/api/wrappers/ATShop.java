package api.wrappers;

import api.ATMethodProvider;
import org.osbot.rs07.api.def.ItemDefinition;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.utility.Condition;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Krulvis on 07-Jun-17.
 */
public class ATShop extends ATMethodProvider {

    public ATShop(ATMethodProvider parent) {
        init(parent);
    }

    public final int SHOP_INTERFACE = 300;
    public final int CLOSE_BUTTON = 11;
    public final int ITEM_CONTAINER = 2;
    public final int BUY_1 = 1;
    public final int BUY_5 = 5;
    public final int BUY_10 = 10;
    public final int BUY_50 = 50;
    private final int NULLITEM = 6512;

    public RS2Widget getContainerWidget() {
        if (!isOpen()) {
            return null;
        }
        return widgets.get(SHOP_INTERFACE, ITEM_CONTAINER);
    }

    public int[] getItemIds() {
        RS2Widget container = getContainerWidget();
        if (container == null) {
            return new int[]{};
        } else {
            RS2Widget[] children = container.getChildWidgets();
            ArrayList<Integer> ids = new ArrayList<>();
            for (RS2Widget child : children) {
                if (child == null || child.getItemId() == NULLITEM) {
                    ids.add(6512);
                } else {
                    ids.add(child.getItemId());
                }
            }
            int[] idArray = new int[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                idArray[i] = ids.get(i);
            }
            return idArray;
        }
    }

    public int[] getItemStackSize() {
        RS2Widget container = getContainerWidget();
        if (container == null) {
            return null;
        } else {
            RS2Widget[] children = container.getChildWidgets();
            ArrayList<Integer> stacks = new ArrayList<>();
            for (RS2Widget child : children) {
                if (child == null || child.getItemId() == NULLITEM) {
                    stacks.add(0);
                } else {
                    stacks.add(child.getItemAmount());
                }
            }
            int[] stackArray = new int[stacks.size()];
            for (int i = 0; i < stacks.size(); i++) {
                stackArray[i] = stacks.get(i);
            }
            return stackArray;
        }
    }

    public boolean isFull() {
        if (!isOpen()) {
            return false;
        }
        boolean hasSpace = false;
        int[] ids = getItemIds();
        for (int id : ids) {
            if (id == -1 || id == 6512) {
                hasSpace = true;
            }
        }
        return !hasSpace;
    }

    public boolean isOpen() {
        return validateWidget(SHOP_INTERFACE, 2);
    }

    public boolean close() {
        if (!isOpen()) {
            return true;
        }
        Rectangle rect = widgets.get(SHOP_INTERFACE, 1).getChildWidget(CLOSE_BUTTON).getBounds();
        if (rect == null) {
            return false;
        }
        return interact.interact(new RectangleDestination(bot, rect));
    }

    public boolean containsOneOf(int... ids) {
        if (!isOpen()) {
            return false;
        }
        int[] shopIds = getItemIds();
        int[] shopStacks = getItemStackSize();
        for (int slot = 0; slot < shopIds.length; slot++) {
            int itemId = shopIds[slot];
            int stack = shopStacks[slot];
            if (itemId == -1) {
                continue;
            }
            for (int find_ : ids) {
                if (itemId == find_) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAll(int... ids) {
        if (!isOpen()) {
            return false;
        }
        int[] shopIds = getItemIds();
        for (int slot = 0; slot < shopIds.length; slot++) {
            int id = shopIds[slot];
            boolean contains = false;
            for (int find : ids) {
                if (find == id) {
                    contains = true;
                }
            }
            if (!contains) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(int id) {
        return containsOneOf(id);
    }

    public int amountOf(int itemId) {
        if (!isOpen()) {
            return -1;
        }
        int amount = 0;
        int[] ids = getItemIds();
        int[] stacks = getItemStackSize();
        if (ids == null || stacks == null) {
            return -1;
        }
        for (int slot = 0; slot < ids.length; slot++) {
            int id = ids[slot];
            if (id == itemId) {
                int stack = stacks[slot];
                amount += stack;
            }
        }
        return amount;
    }

    public boolean sellTotal(Item item, int amount) {
        if (item == null) {
            return false;
        }
        ItemDefinition def = item.getDefinition();
        if (def == null) {
            return false;
        }
        if (!isOpen()) {
            System.out.println("Shop is closed: " + (!isOpen()));
            return false;
        }
        int shopId = def.isNoted() ? item.getId() - 1 : item.getId();
        int shopAmount = amountOf(shopId);
        //System.out.println("Shop has: " + shopAmount + " of: " + def.getName());
        if (shopAmount >= amount) {
            //System.out.println("Shop already has too many of: " + def.getName());
            return true;
        }
        if (isFull() && !contains(shopId)) {
            //System.out.println("Can't sell: " + def.getName() + ", shop is full.");
            return false;
        }
        int amountToSell = amount - shopAmount;
        String action = "Sell 1";
        if (amountToSell >= 5) {
            action = "Sell 5";
        }
        if (amountToSell >= 10) {
            action = "Sell 10";
        }
        return item.interact(action);
    }

    public boolean buyAtSlot(int slot, int amount) {
        if (!isOpen()) {
            return false;
        }
        int[] ids = getItemIds();
        int[] stacks = getItemStackSize();
        if (slot < 0 || slot > ids.length) {
            return false;
        }
        int itemId = ids[slot];
        Rectangle rect = getShopArea(slot);


        boolean interaction = false;
        switch (amount) {
            case BUY_50:
                interaction = interact.interact(rect, "Buy 50");
                break;
            case BUY_1:
                interaction = interact.interact(rect, "Buy 1");
                break;
            case BUY_5:
                interaction = interact.interact(rect, "Buy 5");
                break;
            case BUY_10:
                interaction = interact.interact(rect, "Buy 10");
                break;
            default:
                System.out.println("no buying amount preset: " + amount);
                break;
        }
//		sleep(random(60, 120));
        return interaction;
    }


    public boolean buy(int id, int amount) {
        if (!isOpen() || getContainerWidget() == null) {
            return false;
        }
        int[] ids = getItemIds();
        int[] stacks = getItemStackSize();
        for (int slot = 0; slot < ids.length; slot++) {
            int itemId = ids[slot];
            int stack = stacks[slot];
            if (itemId == -1) {
                continue;
            }
            if (itemId == id) {
                if (stack - amount <= 0 || (amount > 10 && amount != 50)) {
                    amount = 10;
                }
                boolean interact = buyAtSlot(slot, amount);
                return interact;
            }
        }
        return false;
    }

    public boolean buy1(int id) {
        return buy(id, BUY_1);
    }

    public boolean buy5(int id) {
        return buy(id, BUY_5);
    }

    public boolean buy10(int id) {
        return buy(id, BUY_10);
    }

    public Rectangle getShopArea(int slot) {
        if (!isOpen()) {
            return null;
        }
        return widgets.get(SHOP_INTERFACE, ITEM_CONTAINER).getChildWidget(slot).getBounds();
    }

    public Condition SHOP_IS_OPEN = new Condition() {
        public boolean evaluate() {
            return isOpen();
        }
    };

}
