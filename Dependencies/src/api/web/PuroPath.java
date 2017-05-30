package api.web;


import api.ATMethodProvider;
import api.web.nodes.WebAction;
import api.web.nodes.WheatNode;

import java.awt.*;

/**
 * Created by Krulvis on 5-7-2016.
 */
public class PuroPath extends LocalPath{

    private double cost;
    private int currentOffset = 0;
    private final WebAction[] actions;
    private boolean containsSpecialNodes = false;

    public PuroPath(ATMethodProvider s, WebAction[] actionsArray) {
        super(s);
        this.actions = actionsArray;
        for (int i = 0; i < actions.length; i++) {
            WebAction a = actions[i];
            if (i < actions.length - 1 && a instanceof WheatNode) {
                containsSpecialNodes = true;
            }
            cost += a.getCost();
        }
    }


    public boolean traverse() {
        if (containsSpecialNodes) {
            WebAction firstSpecialNode = getFirstSpecialNode();
            int index = -1;
            if (firstSpecialNode != null && (index = getIndex(firstSpecialNode)) < actions.length - 1) {
                int distance = firstSpecialNode.getSourceTile().distance(mp.myPosition());
                if (distance < 5) {
                    System.out.println("Wheat node found, walking that one");
                    return executeAndUpdate(firstSpecialNode);
                }
                return executeAndUpdate(getLastWalkingTileOnScreen());
            }
        }
        return executeAndUpdate(getLastWalkingTileOnScreen());
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
            if (actions[i] instanceof WheatNode) {
                //System.out.println("Found WheatNode at: " + i + ", " + actions[i].getSourceTile());
                return actions[i];
            } else {
                //System.out.println("Actiontype: " + actions[i].getClass().getName());
            }
        }
        //System.out.println("No wheat node in the package...");
        return null;
    }

    public WebAction getLastWalkingTileOnScreen() {
        WebAction last = null;
        for (int i = currentOffset; i < actions.length; i++) {
            if (actions[i].getType() == 1 && actions[i].getDestinationTile().distance(mp.myPosition()) <= 8) {
                last = actions[i];
            } else {
                return last;
            }
        }
        return last;
    }

    public void draw(Graphics g) {
        for (int i = 0; i < actions.length; i++) {
            WebAction a = actions[i];
            g.setColor(new Color(255, 255, 0, 255));
            actions[i].draw(g);
            if (a instanceof WheatNode) {
                g.setColor(new Color(255, 255, 0, 65));
            } else {
                g.setColor(new Color(255, 0, 255, 65));
            }
            //actions[i].fill(g);
        }
    }

    public boolean hasWheatObstruction() {
        return containsSpecialNodes;
    }

    @Override
    public int getCost() {
        return (int)cost;
    }
}
