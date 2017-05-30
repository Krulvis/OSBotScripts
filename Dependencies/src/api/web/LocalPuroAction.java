package api.web;


import api.ATMethodProvider;
import api.web.actions.ObjectAction;
import api.web.nodes.Door;
import api.web.nodes.LocalTileAction;
import api.web.nodes.WebAction;
import api.web.nodes.WheatNode;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.utility.Condition;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by Krulvis on 5-7-2016.
 */
public class LocalPuroAction extends LocalTileAction {

    private Position finalTile;

    public LocalPuroAction(ATMethodProvider mp, WebAction src, Position direction, Position finalTile) {
        super(mp, src, direction, finalTile);
        this.finalTile = finalTile;
    }

    @Override
    public WebAction[] getNeighbours(final PathGenerator web, final Collection<WebAction> open, final ObjectAction action, final RS2Object objectAction) {

        ArrayList<WebAction> q = new ArrayList<WebAction>(8);
        Position d = getDestinationTile();

        WebAction current = this;

        LocalPuroAction N = new LocalPuroAction(mp, this, new Position(d.getX(), d.getY() + 1, d.getZ()), finalTile);
        LocalPuroAction W = new LocalPuroAction(mp, this, new Position(d.getX() - 1, d.getY(), d.getZ()), finalTile);
        LocalPuroAction S = new LocalPuroAction(mp, this, new Position(d.getX(), d.getY() - 1, d.getZ()), finalTile);
        LocalPuroAction E = new LocalPuroAction(mp, this, new Position(d.getX() + 1, d.getY(), d.getZ()), finalTile);

        LocalPuroAction NW = new LocalPuroAction(mp, this, new Position(d.getX() - 1, d.getY() + 1, d.getZ()), finalTile);
        LocalPuroAction NE = new LocalPuroAction(mp, this, new Position(d.getX() + 1, d.getY() + 1, d.getZ()), finalTile);
        LocalPuroAction SW = new LocalPuroAction(mp, this, new Position(d.getX() - 1, d.getY() - 1, d.getZ()), finalTile);
        LocalPuroAction SE = new LocalPuroAction(mp, this, new Position(d.getX() + 1, d.getY() - 1, d.getZ()), finalTile);

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
                if (obj != null && obj.getName().equals("Magical wheat")) {
                    WheatNode wn = new WheatNode(mp, this, N.getDestinationTile(), finalTile, obj);
                    q.add(wn);
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
                if (obj != null && obj.getName().equals("Magical wheat")) {
                    WheatNode wn = new WheatNode(mp, this, S.getDestinationTile(), finalTile, obj);
                    q.add(wn);
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
                if (obj != null && obj.getName().equals("Magical wheat")) {
                    WheatNode wn = new WheatNode(mp, this, E.getDestinationTile(), finalTile, obj);
                    q.add(wn);
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
                if (obj != null && obj.getName().equals("Magical wheat")) {
                    WheatNode wn = new WheatNode(mp, this, W.getDestinationTile(), finalTile, obj);
                    q.add(wn);
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

        /*if(isFinalTile(N)){
            q.add(N);
        }
        if(isFinalTile(W)){
            q.add(W);
        }
        if(isFinalTile(S)){
            q.add(S);
        }
        if(isFinalTile(E)){
            q.add(E);
        }*/

        return q.toArray(new WebAction[q.size()]);
    }

    private boolean isFinalTile(LocalPuroAction lpa) {
        return finalTile.equals(lpa.getDestinationTile());
    }

    @Override
    public boolean execute() {
        if (mp.magic.isSpellSelected()) {
            mp.magic.deselectSpell();
        }
        final Position destTile = getDestinationTile();
        if (!destTile.isVisible(mp.getBot())) {
            return false;
        } else {
            if (!destTile.isVisible(mp.getBot())) {
                mp.camera.toPosition(destTile);
            }
            int degree = ATMethodProvider.getDegree(destTile.distance(mp.myPosition()));
            if (mp.camera.getPitchAngle() > degree) {
                mp.camera.movePitch(degree);
            }
            if (mp.walking.walk(destTile)) {
                mp.waitFor(1000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return mp.myPlayer().isMoving();
                    }
                });
                if (mp.myPlayer().isMoving()) {
                    mp.waitFor(2000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return destTile.distance(mp.myPosition()) <= 2;
                        }
                    });
                }
            }
            return true;
        }
    }
}
