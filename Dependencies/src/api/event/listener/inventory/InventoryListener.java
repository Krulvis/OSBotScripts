package api.event.listener.inventory;

import org.osbot.rs07.api.model.Item;

/**
 * Created by Krulvis on 07-Jun-17.
 */
public interface InventoryListener {

    public void itemAdded(Item item, int amount);

    public void itemRemoved(Item item, int amount);
}
