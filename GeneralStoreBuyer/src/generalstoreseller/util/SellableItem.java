package generalstoreseller.util;

import api.ATMethodProvider;
import api.webapi.WebAPI;
import api.wrappers.ItemDef;
import api.wrappers.grandexchange.GrandExchange;
import generalstoreseller.GeneralStoreSeller;
import org.osbot.rs07.api.def.ItemDefinition;

/**
 * Created by s120619 on 2-3-2017.
 */
public class SellableItem extends ATMethodProvider {

    private int id;
    private int amount;
    private int ha;
    private int buy;
    private int profit;

    public SellableItem(int id, GeneralStoreSeller script) {
        this(id, script.restockAmount, script);
    }

    public SellableItem(int id, int restockAmount, GeneralStoreSeller api) {
        init(api);
        this.id = id;
        this.amount = restockAmount;
    }

    public void checkPrices() {
        this.ha = GrandExchange.getHighAlch(id);
        this.buy = prices.getBuyPrice(id);
        this.profit = SellableItems.calculateProfit(buy, ha);
    }

    public boolean hasInInventory() {
        ItemDefinition def = ItemDefinition.forId(id);
        return def != null && def.getNotedId() == -1 ? inventory.contains(id) : inventory.contains(id + 1);
    }

    @Override
    public String getName() {
        ItemDefinition def = ItemDefinition.forId(id);
        return def == null ? atGE.getItemName(id) : def.getName();
    }

    public int getId() {
        return this.id;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getHighAlch() {
        return this.ha;
    }

    public int getBuy() {
        return this.buy;
    }

    public int getProfit() {
        return profit;
    }
}
