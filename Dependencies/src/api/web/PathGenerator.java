package api.web;

import api.ATMethodProvider;
import api.web.actions.ObjectAction;
import api.web.nodes.LocalTileAction;
import api.web.nodes.ObjectNode;
import api.web.nodes.WebAction;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;

import java.util.*;

/**
 * Created by Krulvis on 13-Mar-17.
 */
public class PathGenerator {
    private WebAction startAction;
    private WebAction next;
    public final Collection<WebAction> open = new ArrayList<WebAction>(500);
    public final Collection<WebAction> closed = new LinkedList<WebAction>();
    private Collection<Integer> doorIDs = new ArrayList<Integer>();
    private int maxAttempts = 2500;
    private ObjectAction objectAction;
    private RS2Object endObject;
    public ATMethodProvider mp;

    public PathGenerator(ATMethodProvider mp) {
        this.mp = mp;
    }

    public PathGenerator(int maxAttempts) {
        this.setMaxAttempts(maxAttempts);
    }

    public LocalPath findPath(final Position end) {
        return findPath(mp.myPosition(), end);
    }

    public LocalPath findObjectActionPath(final ObjectAction oa) {
        return findObjectActionPath(mp.myPosition(), oa);
    }

    public synchronized LocalPath findObjectActionPath(final Position begin, final ObjectAction oa) {
        this.objectAction = oa;
        this.endObject = oa.getRS2Object();
        open.clear();
        closed.clear();
        next = null;
        startAction = null;
        if (endObject == null) {
            return null;
        }
        Position end = endObject.getPosition();
        if (begin == null || end == null) {
            return null;
        }
        debugMessage("FIND PATH: " + begin + " -> " + end);
        int attempts = 0;
        this.startAction = new LocalTileAction(mp, null, begin, end) {
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
                debugMessage("NEXT NULL?");
                break;
            }
            open.remove(next);
            closed.add(next);
            if (end.equals(next.getDestinationTile()) && next instanceof ObjectNode) {
                debugMessage("ATTEMPTS FOR PATH:" + attempts);
                return buildPath(next);
            }
            WebAction[] actions = next.getNeighbours(this, open, oa, this.endObject);
            for (WebAction q : actions) {
                if (closed.contains(q) || open.contains(q)) {
                    continue;
                }
                open.add(q);
            }
        }
        debugMessage("ATTEMPTS FOR NULL: " + attempts);
        return null;
    }

    public synchronized LocalPath findPath(final Position begin, final Position end) {
        this.objectAction = null;
        open.clear();
        closed.clear();
        next = null;
        startAction = null;
        if (begin == null || end == null) {
            return null;
        }
        debugMessage("FIND PATH: " + begin + " -> " + end);
        int attempts = 0;
        this.startAction = new LocalTileAction(mp, null, begin, end) {
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
                debugMessage("NEXT NULL?");
                break;
            }
            open.remove(next);
            closed.add(next);
            if (end.equals(next.getDestinationTile())) {
                debugMessage("ATTEMPTS FOR PATH:" + attempts);
                return buildPath(next);
            }
            WebAction[] actions = next.getNeighbours(this, open, null, null);
            for (WebAction q : actions) {
                if (q == null || closed.contains(q) || open.contains(q)) {
                    continue;
                }
                open.add(q);
            }
        }
        debugMessage("ATTEMPTS FOR NULL: " + attempts);
        return null;
    }

    public boolean debug = false;

    private void debugMessage(String msg) {
        if (debug) {
            System.out.println(msg);
        }
    }

    private LocalPath buildPath(WebAction last) {
        LinkedList<WebAction> path = new LinkedList<WebAction>();
        WebAction r = last;
        while (r != null) {
            path.add(r);
            r = r.getParent();
        }
        Collections.reverse(path);
        return new LocalPath(mp, path.toArray(new WebAction[path.size()]));
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

    public void addDoor(int id) {
        doorIDs.add(id);
    }

    public void removeDoor(int id) {
        doorIDs.remove(id);
    }

    public void addDoors(int... doors) {
        for (int door : doors) {
            doorIDs.add(door);
        }
    }

    public void addDoors(ArrayList<Integer> doors) {
        doors.addAll(doors);
    }

    public void setDoorIDs(ArrayList<Integer> doors) {
        doorIDs = doors;
    }

    public Collection<Integer> getDoorIDs() {
        return doorIDs;
    }

    public boolean canUseDoor(final RS2Object door) {
        final Position loc = door.getPosition();
        if (loc.equals(new Position(2611, 3394, 0))) {
            return mp.skills.getDynamic(Skill.FISHING) >= 68;
        }
        if (loc.equals(new Position(2515, 9575, 0))) {
            return false; // dungeon tree
        }
        if (loc.equals(new Position(2568, 9893, 0))) {
            return false; // waterfall door 1
        }
        if (loc.equals(new Position(2566, 9901, 0))) {
            return false; // waterfall door 2
        }
        if (loc.equals(new Position(2924, 9803, 0))) {
            return false; // taverley blue dragons gate
        }
        // gnome
        return true;
    }

    public static RS2Object getDoor(ATMethodProvider mp, PathGenerator pathGen, WebAction current, int orientation) {
        List<RS2Object> go = mp.objects.get(current.getDestinationTile().getX(), current.getDestinationTile().getY());
        for (RS2Object b : go) {
            if (b == null)
                continue;
            String name = b.getName();
            if ((name.equals("Door") || name.equals("Glass door") || name.equals("Gate") || name.equals("Large door") || name.equals("Castle door") || name.equals("Gate of War") || name.equals("Rickety door") || name.equals("Oozing barrier") || name.equals("Portal of Death") || name.equals("Magic guild door") || name.equals("Prison door") || name.equals("Barbarian door")) && b.hasAction("Open")) {
                int objOri = b.getOrientation();
                if (objOri == orientation && pathGen.canUseDoor(b)) {
                    return b;
                }
            } else {
                int objOri = b.getOrientation();
                if (objOri == orientation) {
                    if (pathGen.isDoor(b) && pathGen.canUseDoor(b)) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    public static RS2Object getSpecialAction(ATMethodProvider mp, PathGenerator pathgen, WebAction current) {
        List<RS2Object> go = mp.objects.get(current.getDestinationTile().getX(), current.getDestinationTile().getY());
        for (RS2Object b : go) {
            if (b == null)
                continue;
            if (pathgen.objectAction != null) {
                if (pathgen.endObject != null) {
                    if (pathgen.endObject.getPosition().equals(b.getPosition()) && pathgen.endObject.getId() == b.getId()) {
                        return b;
                    }
                }
            }
            if ((b.getPosition().equals(new Position(2606, 3152, 0)) || // fight
                    // arena
                    b.getPosition().equals(new Position(3107, 3162, 0)) // wizard
                    // tower
            )
                    && b.getName().equals("Door")) {
                return b;
            }
            if (pathgen.isSpecialAction(b)) {
                return b;
            }
        }
        return null;
    }

    public boolean isSpecialAction(final RS2Object go) {
        if (go.getName().equalsIgnoreCase("Wilderness Ditch")) {
            return true;
        }
        return false;
    }

    public boolean isDoor(final RS2Object b) {
        int objID = b.getId();
        for (int id : getDoorIDs()) {
            if (objID == id) {
                return true;
            }
        }
        return false;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getRealDistanceTo(final Position tile) {
        LocalPath path = findPath(tile);
        if (path == null) {
            return -1;
        }
        return path.getLength();
    }

    public boolean shouldIgnoreCollisionNear(final Position n) {
        return false;
    }

    public boolean shouldIgnoreCollision(final Position destinationTile) {
        return false;
    }
}
