package generalstoreseller;

import api.ATScript;
import api.ATState;
import api.event.listener.inventory.InventoryListener;
import api.util.ATPainter;
import api.util.Timer;
import api.util.gui.GUIWrapper;
import generalstoreseller.states.Buying;
import generalstoreseller.states.Selling;
import generalstoreseller.states.Starting;
import generalstoreseller.util.GUI;
import generalstoreseller.util.SellableItem;
import generalstoreseller.util.SellableItems;
import org.osbot.rs07.api.def.ItemDefinition;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Krulvis on 16-Feb-17.
 */
@ScriptManifest(name = "GeneralStoreSeller", author = "Krulvis", version = 1.01D, logo = "", info = "")
public class GeneralStoreSeller extends ATScript implements InventoryListener {

    public static int[] startSellables = {
            56,
            58,
            62,
            64,
            805,
            829,
            830,
            847,
            851,
            881,
            892,
            1517,
            1519,
            1635,
            1660,
            1654,
            1673,
            1692,
            2357,
            2568,
            4542,
            11069,
            19580,
            19582};

    public SellableItems sellables;
    public boolean restockWhenOneGone = false;
    public int startWorld = -1;
    public int turnOver = 0, costs = 0;
    public Timer firstWorldTimer = null;

    @Override
    public void onStart() {
        timer = new Timer();
        new GUI(this);
    }

    @Override
    public void update() {

    }

    @Override
    protected void initialize(LinkedList<ATState> statesToAdd) {
        statesToAdd.add(new Starting(this));
        statesToAdd.add(new Selling(this));
        statesToAdd.add(new Buying(this));
    }

    @Override
    protected Class<? extends ATPainter> getPainterClass() {
        return Painter.class;
    }

    @Override
    protected Class<? extends GUIWrapper> getGUI() {
        return GUI.class;
    }

    public Item[] getInvSellables() {
        Item[] invItems = inventory.getItems();
        ArrayList<Item> invSellables = new ArrayList<>();
        for (Item i : invItems) {
            ItemDefinition def = i.getDefinition();
            if (def != null && isSellable(def)) {
                invSellables.add(i);
            }
        }
        return invSellables.toArray(new Item[invSellables.size()]);
    }

    public Item getSellable() {
        Item[] items = inventory.getItems();
        for (Item i : items) {
            ItemDefinition def = ItemDefinition.forId(i.getId());
            if (def != null && isSellable(def) && hasPlaceInShop(def) && shop.amountOf(def.isNoted() ? def.getId() - 1 : def.getId()) < 5) {
                //System.out.println("Can still sell: " + def.getName() + ": " + i.getID());
                return i;
            }
        }
        return null;
    }

    private boolean hasPlaceInShop(ItemDefinition def) {
        final int itemId = def.getUnnotedId();
        return !shop.isFull() || shop.contains(itemId);
    }

    public boolean isSellable(ItemDefinition def) {
        if (def == null) {
            return false;
        }
        for (int id : GeneralStoreSeller.startSellables) {
            if (def.isNoted() ? def.getId() - 1 == id : def.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSellables() {
        return restockWhenOneGone ? !isMissingOneSellable() : inventory.contains(GeneralStoreSeller.startSellables);
    }

    public boolean isMissingOneSellable() {
        for (SellableItem i : sellables.getCurrent()) {
            if (!i.hasInInventory()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onMessage(Message m) {
        if (m != null && m.getTypeId() == 0 && m.getMessage().contains("You haven't got enough.")) {
            Buying.isBuying = false;
        }
    }

    @Override
    public void itemAdded(Item i, int amount) {
        ATState s = currentState;
        if (i != null && i.getId() == 995 && (s instanceof Selling)) {
            turnOver += amount;
        }
    }

    @Override
    public void itemRemoved(Item item, int amount) {

    }
}
