package api.util.requirements;

import api.ATMethodProvider;

/**
 * Created by s120619 on 25-3-2017.
 */
public class ItemRequirement extends Requirement {

    private final int itemID;
    private final int amount;

    public ItemRequirement(ATMethodProvider mp, int itemID, int amount) {
        super(mp);
        this.itemID = itemID;
        this.amount = amount;
    }

    public ItemRequirement(ATMethodProvider mp, int itemID) {
        this(mp, itemID, 1);
    }

    @Override
    public boolean hasRequirement() {
        return inventory.getAmount(itemID) >= amount;
    }

    public int getItemID() {
        return itemID;
    }

    public int getAmount() {
        return amount;
    }

}