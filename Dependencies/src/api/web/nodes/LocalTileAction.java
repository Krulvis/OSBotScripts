package api.web.nodes;

import api.ATMethodProvider;
import api.web.Flag;
import api.web.PathGenerator;
import api.web.actions.ObjectAction;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Krulvis on 13-Mar-17.
 */
public class LocalTileAction extends WebAction {
    
    private float heuristic;
    private float cost;
    private WebAction sourceAction;
    private Position destinationTile;
    private Position finalTile;

    public LocalTileAction(ATMethodProvider mp, final WebAction src, final Position dir, final Position finalTile) {
        super(mp);
        this.sourceAction = src;
        this.destinationTile = dir;

        if (src == null) {
            cost = 0;
        } else {
            Position srcD = src.getDestinationTile();
            if (srcD.getX() == dir.getX()) {// nondiagonal
                if (srcD.getY() == dir.getY()) {// nomovement
                    cost = src.getCost() + 0;
                } else {
                    cost = src.getCost() + 10;
                }
            } else {// diagonal
                if (srcD.getY() == dir.getY()) {
                    cost = src.getCost() + 10;
                } else {
                    cost = src.getCost() + 14f;
                }
            }
        }
        this.finalTile = finalTile;
        heuristic = dir.distance(finalTile) * 10;
    }

    @Override
    public Position getSourceTile() {
        return sourceAction.getDestinationTile();
    }

    @Override
    public Position getDestinationTile() {
        return destinationTile;
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public float getHeuristic() {
        return heuristic;
    }

    @Override
    public WebAction getParent() {
        return sourceAction;
    }

    @Override
    public WebAction[] getNeighbours(final PathGenerator web, final Collection<WebAction> open, final ObjectAction action, final RS2Object objectAction) {

        ArrayList<WebAction> q = new ArrayList<WebAction>(8);
        Position d = getDestinationTile();

        WebAction current = this;

        LocalTileAction N = new LocalTileAction(mp, this, new Position(d.getX(), d.getY() + 1, d.getZ()), finalTile);
        LocalTileAction W = new LocalTileAction(mp, this, new Position(d.getX() - 1, d.getY(), d.getZ()), finalTile);
        LocalTileAction S = new LocalTileAction(mp, this, new Position(d.getX(), d.getY() - 1, d.getZ()), finalTile);
        LocalTileAction E = new LocalTileAction(mp, this, new Position(d.getX() + 1, d.getY(), d.getZ()), finalTile);

        LocalTileAction NW = new LocalTileAction(mp, this, new Position(d.getX() - 1, d.getY() + 1, d.getZ()), finalTile);
        LocalTileAction NE = new LocalTileAction(mp, this, new Position(d.getX() + 1, d.getY() + 1, d.getZ()), finalTile);
        LocalTileAction SW = new LocalTileAction(mp, this, new Position(d.getX() - 1, d.getY() - 1, d.getZ()), finalTile);
        LocalTileAction SE = new LocalTileAction(mp, this, new Position(d.getX() + 1, d.getY() - 1, d.getZ()), finalTile);

        if (!blocked(N, Flag.BLOCKED | Flag.WATER | Flag.BLOCKED2 | Flag.BLOCKED4, false) || web.shouldIgnoreCollision(N.getDestinationTile())) {
            if (!blocked(current, Flag.W_N, true) || web.shouldIgnoreCollision(N.getDestinationTile())) {
                q.add(N);
            } else {
                RS2Object door = PathGenerator.getDoor(mp, web, current, Flag.NORTH);
                if (door != null) {
                    Door doorNode = new Door(mp, this, N.getDestinationTile(), finalTile, door);
                    q.add(doorNode);
                } else {
                    door = PathGenerator.getDoor(mp, web, N, Flag.SOUTH);
                    if (door != null) {
                        Door doorNode = new Door(mp, this, N.getDestinationTile(), finalTile, door);
                        q.add(doorNode);
                    }
                }
            }
        } else {
            if (!blocked(current, Flag.W_N, true) || web.shouldIgnoreCollisionNear(N.getDestinationTile())) {
                RS2Object obj = PathGenerator.getSpecialAction(mp, web, N);
                if (obj != null) {
                    if (obj.equals(objectAction)) {
                        ObjectNode node = new ObjectNode(mp, this, N.getDestinationTile(), finalTile, action);
                        q.add(node);
                    } else {
                        ObjectNode node = new ObjectNode(mp, this, N.getDestinationTile(), finalTile, obj);
                        q.add(node);
                    }
                }
            }
        }
        if (!blocked(S, Flag.BLOCKED | Flag.WATER | Flag.BLOCKED2 | Flag.BLOCKED4, false) || web.shouldIgnoreCollision(S.getDestinationTile())) {
            if (!blocked(current, Flag.W_S, true) || web.shouldIgnoreCollision(S.getDestinationTile())) {
                q.add(S);
            } else {
                RS2Object door = PathGenerator.getDoor(mp, web, current, Flag.SOUTH);
                if (door != null) {
                    Door doorNode = new Door(mp, this, S.getDestinationTile(), finalTile, door);
                    q.add(doorNode);
                } else {
                    door = PathGenerator.getDoor(mp, web, S, Flag.NORTH);
                    if (door != null) {
                        Door doorNode = new Door(mp, this, S.getDestinationTile(), finalTile, door);
                        q.add(doorNode);
                    }
                }
            }
        } else {
            if (!blocked(current, Flag.W_S, true) || web.shouldIgnoreCollisionNear(S.getDestinationTile())) {
                RS2Object obj = PathGenerator.getSpecialAction(mp, web, S);
                if (obj != null) {
                    if (obj.equals(objectAction)) {
                        ObjectNode node = new ObjectNode(mp, this, S.getDestinationTile(), finalTile, action);
                        q.add(node);
                    } else {
                        ObjectNode node = new ObjectNode(mp, this, S.getDestinationTile(), finalTile, obj);
                        q.add(node);
                    }
                }
            }
        }//
        if (!blocked(E, Flag.BLOCKED | Flag.WATER | Flag.BLOCKED2 | Flag.BLOCKED4, false) || web.shouldIgnoreCollision(E.getDestinationTile())) {
            if (!blocked(current, Flag.W_E, true) || web.shouldIgnoreCollision(E.getDestinationTile())) {
                q.add(E);
            } else {
                RS2Object door = PathGenerator.getDoor(mp, web, current, Flag.EAST);
                if (door != null) {
                    Door doorNode = new Door(mp, this, E.getDestinationTile(), finalTile, door);
                    q.add(doorNode);
                } else {
                    door = PathGenerator.getDoor(mp, web, E, Flag.WEST);
                    if (door != null) {
                        Door doorNode = new Door(mp, this, E.getDestinationTile(), finalTile, door);
                        q.add(doorNode);
                    }
                }
            }
        } else {
            if (!blocked(current, Flag.W_E, true) || web.shouldIgnoreCollisionNear(E.getDestinationTile())) {
                RS2Object obj = PathGenerator.getSpecialAction(mp, web, E);
                if (obj != null) {
                    if (obj.equals(objectAction)) {
                        ObjectNode node = new ObjectNode(mp, this, E.getDestinationTile(), finalTile, action);
                        q.add(node);
                    } else {
                        ObjectNode node = new ObjectNode(mp, this, E.getDestinationTile(), finalTile, obj);
                        q.add(node);
                    }
                }
            }
        }
        if (!blocked(W, Flag.BLOCKED | Flag.WATER | Flag.BLOCKED2 | Flag.BLOCKED4, false) || web.shouldIgnoreCollision(W.getDestinationTile())) {
            if (!blocked(current, Flag.W_W, true) || web.shouldIgnoreCollision(W.getDestinationTile())) {
                q.add(W);
            } else {
                RS2Object door = PathGenerator.getDoor(mp, web, current, Flag.WEST);
                if (door != null) {
                    Door doorNode = new Door(mp, this, W.getDestinationTile(), finalTile, door);
                    q.add(doorNode);
                } else {
                    door = PathGenerator.getDoor(mp, web, W, Flag.EAST);
                    if (door != null) {
                        Door doorNode = new Door(mp, this, W.getDestinationTile(), finalTile, door);
                        q.add(doorNode);
                    }
                }
            }
        } else {
            if (!blocked(current, Flag.W_W, true) || web.shouldIgnoreCollisionNear(W.getDestinationTile())) {
                RS2Object obj = PathGenerator.getSpecialAction(mp, web, W);
                if (obj != null) {
                    if (obj.equals(objectAction)) {
                        ObjectNode node = new ObjectNode(mp, this, W.getDestinationTile(), finalTile, action);
                        q.add(node);
                    } else {
                        ObjectNode node = new ObjectNode(mp, this, W.getDestinationTile(), finalTile, obj);
                        q.add(node);
                    }
                }
            }
        }
        if (!blocked(NE, Flag.BLOCKED | Flag.WATER | Flag.BLOCKED2 | Flag.BLOCKED4, false) && !blocked(current, Flag.W_NE | Flag.W_N | Flag.W_E, true) && !blocked(N, Flag.BLOCKED | Flag.BLOCKED2 | Flag.BLOCKED4 | Flag.W_E | Flag.WATER, false) && !blocked(E, Flag.BLOCKED | Flag.BLOCKED2 | Flag.BLOCKED4 | Flag.W_N | Flag.WATER, false)) {
            q.add(NE);
        }
        if (!blocked(NW, Flag.BLOCKED | Flag.WATER | Flag.BLOCKED2 | Flag.BLOCKED4, false) && !blocked(current, Flag.W_NW | Flag.W_N | Flag.W_W, true) && !blocked(N, Flag.BLOCKED | Flag.BLOCKED2 | Flag.BLOCKED4 | Flag.W_W | Flag.WATER, false) && !blocked(W, Flag.BLOCKED | Flag.BLOCKED2 | Flag.BLOCKED4 | Flag.W_N | Flag.WATER, false)) {
            q.add(NW);
        }
        if (!blocked(SE, Flag.BLOCKED | Flag.WATER | Flag.BLOCKED2 | Flag.BLOCKED4, false) && !blocked(current, Flag.W_SE | Flag.W_S | Flag.W_E, true) && !blocked(S, Flag.BLOCKED | Flag.BLOCKED2 | Flag.BLOCKED4 | Flag.W_E | Flag.WATER, false) && !blocked(E, Flag.BLOCKED | Flag.BLOCKED2 | Flag.BLOCKED4 | Flag.W_S | Flag.WATER, false)) {
            q.add(SE);
        }
        if (!blocked(SW, Flag.BLOCKED | Flag.WATER | Flag.BLOCKED2 | Flag.BLOCKED4, false) && !blocked(current, Flag.W_SW | Flag.W_S | Flag.W_W, true) && !blocked(S, Flag.BLOCKED | Flag.BLOCKED2 | Flag.BLOCKED4 | Flag.W_W | Flag.WATER, false) && !blocked(W, Flag.BLOCKED | Flag.BLOCKED2 | Flag.BLOCKED4 | Flag.W_S | Flag.WATER, false)) {
            q.add(SW);
        }

        // open.add(N);
        // open.add(W);
        // open.add(S);
        // open.add(E);
        return q.toArray(new WebAction[q.size()]);
    }

    public boolean blocked(final WebAction locatable, final int value, final boolean current) {
        Position pos = locatable.getDestinationTile();
        int val = Flag.getFlag(mp, pos);
        if (val == 0) {
            return false;
        }
        return (val & value) != 0;
    }

    @Override
    public boolean execute() {
        Position destPosition = getDestinationTile();
        if (!destPosition.isOnMiniMap(mp.bot)) {
            return false;
        } else {
            if (destPosition.isVisible(mp.getBot()) && !mp.bank.isOpen() && destPosition.distance(mp.myPosition()) < 4) {
                return mp.walking.walk(destPosition);
            } else {
                return mp.walking.walk(destPosition);
            }
        }
    }

    @Override
    public String toString() {
        return this.getSourceTile() + "->" + this.getDestinationTile();
    }

    @Override
    public int hashCode() {
        return getDestinationTile().hashCode() + getSourceTile().hashCode();
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof LocalTileAction) {
            LocalTileAction wta = (LocalTileAction) o;
			/*
			 * Position src = this.getSourceTile(); Position src2 = wta.getSourceTile(); boolean yes = false; if (src==null&&src2==null){ yes = true; } else if (src == null || src2 == null){ yes = false; } else { yes = src.equals(src2); }
			 */
            return wta.getType() == this.getType() && wta.getDestinationTile().equals(this.getDestinationTile());
        }
        return false;
    }
}
