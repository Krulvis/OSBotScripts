package api.wrappers.grandexchange;

import api.ATMethodProvider;
import org.osbot.rs07.api.*;
import org.osbot.rs07.api.GrandExchange;
import org.osbot.rs07.api.def.ItemDefinition;

/**
 * Created by Krulvis on 07-Jun-17.
 */
public class GrandExchangeOffer extends ATMethodProvider {

    private int slot = -1;
    private GrandExchange.Box box;

    public GrandExchangeOffer(final int slot, ATMethodProvider parent) {
        this.slot = slot;
        init(parent);
    }

    public GrandExchangeOffer(final GrandExchange.Box box, ATMethodProvider parent) {
        this.box = box;
        init(parent);
    }

    public int getSlot() {
        if (slot == -1) {
            switch (this.box) {
                case BOX_1:
                    return slot = 0;
                case BOX_2:
                    return slot = 1;
                case BOX_3:
                    return slot = 2;
                case BOX_4:
                    return slot = 3;
                case BOX_5:
                    return slot = 4;
                case BOX_6:
                    return slot = 5;
                case BOX_7:
                    return slot = 6;
                case BOX_8:
                    return slot = 7;
            }
        }
        return slot;
    }

    public boolean isActive() {
        return getStatus() != GrandExchange.Status.EMPTY;
    }

    public GrandExchange.Status getStatus() {
        return getGrandExchange().getStatus(getBox());
    }

    public boolean isBuyOffer() {
        GrandExchange.Status status = getStatus();
        return status == GrandExchange.Status.CANCELLING_BUY || status == GrandExchange.Status.COMPLETING_BUY
                || status == GrandExchange.Status.FINISHED_BUY || status == GrandExchange.Status.INITIALIZING_BUY
                || status == GrandExchange.Status.PENDING_BUY;
    }

    public int getItemID() {

        return getGrandExchange().getItemId(getBox());
    }

    public ItemDefinition getItemDef() {
        return ItemDefinition.forId(getItemID());
    }

    public int getCoinsTransferred() {
        return getGrandExchange().getAmountSpent(getBox());
    }

    public int getItemsTransferredCount() {
        return getGrandExchange().getAmountTraded(getBox());
    }

    public int getTotalItemsToTransfer() {
        return getGrandExchange().getAmountRemaining(getBox()) + getItemsTransferredCount();
    }

    public int getUnitPrice() {
        return getGrandExchange().getItemPrice(getBox());
    }

    public boolean isCompleted() {
        return getGrandExchange().getAmountRemaining(getBox()) == 0;
    }

    public boolean isAborted() {
        GrandExchange.Status status = getStatus();
        return (status == GrandExchange.Status.FINISHED_BUY || status == GrandExchange.Status.FINISHED_SALE) && getItemsTransferredCount() != getTotalItemsToTransfer();
    }

    public GrandExchange.Box getBox() {
        if (this.box != null) {
            return this.box;
        }
        switch (this.slot) {
            case 0:
                return box = GrandExchange.Box.BOX_1;
            case 1:
                return box = GrandExchange.Box.BOX_2;
            case 2:
                return box = GrandExchange.Box.BOX_3;
            case 3:
                return box = GrandExchange.Box.BOX_4;
            case 4:
                return box = GrandExchange.Box.BOX_5;
            case 5:
                return box = GrandExchange.Box.BOX_6;
            case 6:
                return box = GrandExchange.Box.BOX_7;
            case 7:
                return box = GrandExchange.Box.BOX_8;
            default:
                return null;
        }
    }

}
