package api.event.listener.inventory;

import api.ATScript;
import api.event.listener.EventHandler;
import org.osbot.rs07.api.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krulvis on 07-Jun-17.
 */
public class InventoryHandler extends EventHandler {

    public InventoryHandler(ATScript parent) {
        super(parent);
    }

    private Item[] cachedItems = null;

    @Override
    public void handle() {
        List<Integer> checkedItems = new ArrayList<Integer>();
        final Item[] currentItems = inventory.getItems();
        if (cachedItems != null && currentItems != null) {
            for (Item i : currentItems) {
                if (i != null && i.getId() > 0 && !checkedItems.contains(i.getId())) {
                    checkItem(i, currentItems, cachedItems);
                    checkedItems.add(i.getId());
                }
            }
            for (Item i : cachedItems) {
                if (i != null && i.getId() > 0 && !checkedItems.contains(i.getId())) {
                    checkItem(i, currentItems, cachedItems);
                    checkedItems.add(i.getId());
                }
            }
        }
        cachedItems = currentItems;
    }

    private void checkItem(Item item, Item[] newItems, Item[] oldItems) {
        if (item != null && item.getId() > 0) {
            final int currentAmount = getAmount(item.getId(), newItems);
            final int oldAmount = getAmount(item.getId(), oldItems);
            if (currentAmount != oldAmount) {
                int amount = currentAmount - oldAmount;
                if (amount > 0) {
                    ((InventoryListener) script).itemAdded(item, amount);
                } else {
                    ((InventoryListener) script).itemRemoved(item, -amount);
                }
            }
        }
    }

    private int getAmount(int item, Item[] iArray) {
        int temp = 0;
        for (Item i : iArray) {
            if (i != null && i.getId() == item) {
                temp += i.getAmount();
            }
        }
        return temp;
    }
}
