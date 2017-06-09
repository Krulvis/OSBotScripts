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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

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
    public boolean loadGUI = true;
    public int restockAmount = 200;

    public Buying buyingState;

    @Override
    public void onStart() {
        timer = new Timer();
        String param = getParameters();
        if (param != null) {
            if (param.contains("load")) {
                System.out.println("Found parameter LOAD");
                loadGUI = false;
            }
        }
    }

    public void loadSettings() {
        System.out.println("Loading settings using params");
        sellables = new SellableItems(this);
        try {
            File file = new File(getSettingsFolder() + "settings.properties");
            Properties p = new Properties();
            p.load(new FileReader(file));
            restockWhenOneGone = Boolean.parseBoolean(p.getProperty("quick_restock", "true"));
            restockAmount = Integer.parseInt(p.getProperty("restock_amount", "200"));
            //Initiate standard
            for (int i = 0; i < GeneralStoreSeller.startSellables.length; i++) {
                int id = GeneralStoreSeller.startSellables[i];
                sellables.addSellable(new SellableItem(id, restockAmount, this));
            }
            for (String key : p.stringPropertyNames()) {
                if (key.contains("item_")) {
                    String prop = p.getProperty(key);
                    if (prop != null) {
                        try {
                            int id = Integer.parseInt(prop.contains(",") ? prop.substring(0, prop.indexOf(",")) : prop);
                            int amount = prop.contains(",") ? Integer.parseInt(prop.substring(prop.indexOf(",") + 1)) : restockAmount;
                            SellableItem si = new SellableItem(id, amount, this);
                            System.out.println("Added custom: " + si.getId() + ", " + si.getAmount());
                            sellables.addSellable(si);
                        } catch (NumberFormatException e) {
                            System.out.println("Wrong number format: " + prop);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sellables.reCheckSellables();
    }

    @Override
    public void update() {

    }

    @Override
    protected void initialize(LinkedList<ATState> statesToAdd) {
        statesToAdd.add(new Starting(this));
        statesToAdd.add(new Selling(this));
        statesToAdd.add(buyingState = new Buying(this));
    }

    @Override
    protected Class<? extends ATPainter> getPainterClass() {
        return Painter.class;
    }

    @Override
    protected Class<? extends GUIWrapper> getGUI() {
        return loadGUI ? GUI.class : null;
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
            buyingState.isBuying = false;
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
