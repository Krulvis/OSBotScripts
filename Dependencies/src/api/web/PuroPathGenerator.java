package api.web;

import api.ATMethodProvider;
import api.web.actions.ObjectAction;
import api.web.nodes.WebAction;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by Krulvis on 5-7-2016.
 */
public class PuroPathGenerator extends PathGenerator{

    private ATMethodProvider s;
    private WebAction startAction;
    private WebAction next;
    public final Collection<WebAction> open = new ArrayList<WebAction>(500);
    public final Collection<WebAction> closed = new LinkedList<WebAction>();
    private Collection<Integer> doorIDs = new ArrayList<Integer>();
    private int maxAttempts = 2500;
    private Position finalTile;

    public PuroPathGenerator(ATMethodProvider mp){
        super(mp);
    }

    public synchronized PuroPath findPath(final Position end){
        return findPath(s.myPosition(), end);
    }

    public synchronized PuroPath findPath(final Position begin, final Position end) {
        open.clear();
        closed.clear();
        this.finalTile = end;
        next = null;
        startAction = null;
        if (begin == null || end == null) {
            return null;
        }
        //System.out.println("FIND PATH: " + begin + " -> " + end);
        int attempts = 0;
        this.startAction = new LocalPuroAction(mp, null, begin, end) {
            @Override
            public Position getSourceTile() {
                return begin;
            }
        };

        open.add(startAction);

        while (!open.isEmpty() && attempts < maxAttempts) {
            attempts++;
            next = getBest(open);
            if (next == null) {
                System.out.println("Can't find best");
                break;
            }
            open.remove(next);
            closed.add(next);
            if (end.equals(next.getDestinationTile())) {
                //System.out.println("ATTEMPTS FOR PATH:" + attempts);
                return buildPath(next);
            }
            WebAction[] actions = next.getNeighbours(this, open, new ObjectAction(mp, "Magical wheat", "Push-through"), null);
            for (WebAction q : actions) {
                if (q == null || closed.contains(q) || open.contains(q)) {
                    continue;
                }
                open.add(q);
            }
        }
        //debugMessage("ATTEMPTS FOR NULL: " + attempts);
        return null;
    }

    private WebAction getBest(Collection<WebAction> open) {
        WebAction best = null;
        float bestF = 0;
        for (WebAction q : open) {
            if (q == null)
                continue;
            if (best == null) {
                best = q;
                bestF = q.getHeuristic() + q.getCost();
                continue;
            }
            float currentF = q.getHeuristic() + q.getCost();
            if (currentF < bestF) {
                best = q;
                bestF = currentF;
            }
        }
        return best;
    }

    private PuroPath buildPath(WebAction last) {
        LinkedList<WebAction> path = new LinkedList<WebAction>();
        WebAction r = last;
        while (r != null) {
            path.add(r);
            r = r.getParent();
        }
        Collections.reverse(path);
        return new PuroPath(mp, path.toArray(new WebAction[path.size()]));
    }
    
    public boolean isSpecialAction(final RS2Object g){
        if(g != null && g.getName().equals("Magical wheat") && (g.hasAction("Push-through") || g.getPosition().equals(finalTile))){
           return true;
        }
        return false;
    }

}
