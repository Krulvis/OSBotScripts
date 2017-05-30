package api.web.actions;

import api.ATMethodProvider;
import api.util.Timer;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.utility.Condition;

public class ItemAction extends Action {

    private int itemID;
    private String action;
    private Position destination;

    public ItemAction(ATMethodProvider s, int itemID, String action, Position destination) {
        super(s);
        this.action = action;
        this.itemID = itemID;
        this.destination = destination;
    }

    public int getItemID() {
        return itemID;
    }

    public String getAction() {
        return action;
    }

    public Position getDestination() {
        return destination;
    }

    public boolean canUse() {
        return getItem() != null;
    }

    public Item getItem() {
        return inventory.getItem(getItemID());
    }

    @Override
    public boolean traverse() {
        Item itemWC = getItem();
        if (itemWC == null) {
            return false;
        }
        if (itemWC.interact(getAction())) {
            if (getItemID() == 8013) {
                sleep(3500, 4500);
                Timer timer = new Timer(15000);
                while (!timer.isFinished() && widgets.getWidgetContainingText("There'mp no place like home") != null) {
                    sleep(100, 150);
                }
                return true;
            }
            return waitFor(5000, new Condition() {
                @Override
                public boolean evaluate() {
                    return destination.distance(myPosition()) < 10;
                }
            });
        }
        return false;
    }

    @Override
    public double getCost() {
        return 5;
    }

    @Override
    public String toString() {
        return "ItemAction: {" + getAction() + " " + getItemID() + " -> " + getDestination() + "}";
    }
}