package api.web.nodes;


import api.ATMethodProvider;
import api.util.ATPainter;
import api.web.PathGenerator;
import api.web.actions.ObjectAction;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;

import java.awt.*;
import java.util.Collection;

public abstract class WebAction {

    public ATMethodProvider mp;

    public WebAction(ATMethodProvider mp) {
        this.mp = mp;
    }

    public abstract WebAction getParent();

    public abstract Position getSourceTile();

    public abstract Position getDestinationTile();

    public abstract float getCost();

    public abstract float getHeuristic();

    public abstract int getType();

    public abstract WebAction[] getNeighbours(PathGenerator web, Collection<WebAction> open, ObjectAction objectAction, RS2Object actionObject);

    public void draw(Graphics g) {
        ATPainter painter = mp.painter;
        painter.drawPosition(g, getDestinationTile(), new Color(255,255,0,255), new Color(255,255,0,65));
    }

    public abstract boolean execute();

}