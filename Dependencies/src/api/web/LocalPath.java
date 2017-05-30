package api.web;

import api.ATMethodProvider;
import api.web.nodes.WebAction;
import org.osbot.rs07.api.map.Position;

import java.awt.*;

/**
 * Created by Krulvis on 13-Mar-17.
 */
public class LocalPath extends Path {
    private double cost;
    private int currentOffset = 0;
    private WebAction[] actions;
    private boolean containsSpecialNodes = false;


    public LocalPath(ATMethodProvider mp) {
        super(mp);
    }

    public LocalPath(ATMethodProvider mp, WebAction[] actions) {
        super(mp);
        setWebActions(actions);
    }

    public void setWebActions(WebAction... actionsArray) {
        this.actions = actionsArray;
        for (WebAction p : actions) {
            if (p.getType() != 1) {
                containsSpecialNodes = true;
            }
            cost += p.getCost();
        }
    }

    public boolean containsSpecialAction() {
        return containsSpecialNodes;
    }

    public void draw(Graphics g) {
        for (int i = 0; i < actions.length; i++) {
            actions[i].draw(g);
        }
    }

    @Override
    public boolean traverse() {
        if (containsSpecialNodes) {
            WebAction firstSpecialNode = getFirstSpecialNode();
            if (firstSpecialNode != null) {
                int distance = mp.distance(firstSpecialNode.getSourceTile());
                if (distance < 5) {
                    return executeAndUpdate(firstSpecialNode);
                }
                return executeAndUpdate(getLastWalkingTileOnMM());
            }
            return executeAndUpdate(getLastWalkingTileOnMM());
        }
        return executeAndUpdate(getLastWalkingTileOnMM());
    }

    @Override
    public int getCost() {
        return (int)cost;
    }

    private boolean executeAndUpdate(WebAction wa) {
        if (wa == null)
            return false;
        if (wa.execute()) {
            currentOffset = getIndex(wa) + 1;
            return true;
        }
        return false;
    }

    private int getIndex(WebAction wa) {
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] == wa) {
                return i;
            }
        }
        return -1;
    }

    public WebAction getFirstSpecialNode() {
        for (int i = currentOffset; i < actions.length; i++) {
            if (actions[i].getType() != 1) {
                return actions[i];
            }
        }
        return null;
    }

    public boolean isOnCustomMiniMap(final Position t) {
        if (t.distance(mp.myPosition()) > 20) {
            return false;
        }
        return t.isOnMiniMap(mp.bot);
    }

    /**
     * @return null if offtrack
     */
    public WebAction getLastWalkingTileOnMM() {
        WebAction last = null;
        for (int i = currentOffset; i < actions.length; i++) {
            if (actions[i].getType() == 1 && isOnCustomMiniMap(actions[i].getDestinationTile())) {
                last = actions[i];
            } else {
                return last;
            }
        }
        return last;
    }

    public int getLength() {
        return actions.length;
    }

}
