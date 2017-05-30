package puropuro.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import api.web.PuroPath;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.input.mouse.EntityDestination;
import org.osbot.rs07.utility.Condition;
import puropuro.PuroPuro;
import puropuro.util.Impling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static puropuro.PuroPuro.MAGIC_NET;
import static puropuro.PuroPuro.NET;

/**
 * Created by Krulvis on 12-Mar-17.
 */
public class Hunting extends ATState<PuroPuro> {

    public static NPC target, next;
    public static PuroPath pp;

    public static final Position NE = new Position(2608, 4336, 0), SE = new Position(2608, 4303, 0),
            SW = new Position(2575, 4303, 0), NW = new Position(2575, 4336, 0);
    public static Position[] scoutTiles = new Position[]{NE, SE, SW, NW};
    public static Position scoutTile;
    public static Timer bindTimer = null;
    public static Position centerPuroTile = new Position(2590, 4320, 0);

    public Hunting(PuroPuro script) {
        super("Hunting", script);
    }

    @Override
    public int perform() {
        if (distance(centerPuroTile) > 50) {
            walkPath(centerPuroTile);
        } else if (script.spawnCamp && distance(script.spawnTile) > 15) {
            PuroPath pp = script.ppg.findPath(script.spawnTile);
            if (pp != null) {
                pp.traverse();
            }
        } else {
            final Item net = inventory.getItem(NET, MAGIC_NET);
            if (net != null) {
                net.interact();
            }
            if (target == null || !target.exists() || (script.spawnCamp ? target.getPosition().distance(script.spawnTile) > 15 : distance(target) > 10)) {
                target = getNearestTarget();
                System.out.println(target != null ? "Setting new target" : "No new target found");
                bindTimer = null;
            } else if (!isBinded() && (next = getNearestTarget()) != null && next != target && distance(next) + (distance(next) / 3) + 1 < distance(target)) {
                System.out.println("Setting target to closer one!");
                target = next;
                bindTimer = null;
            }

            if (target != null) {
                System.out.println("Visibility of target check: " + "On screen: " + target.isOnScreen() + ", Model Found: " + (target.getModel() != null));
                pp = script.ppg.findPath(target.getPosition());
                if (!isBinded() && script.bindSpell != null && distance(target) < 8) {
                    if (script.bindSpell.bind(this, target)) {
                        bindTimer = new Timer(script.bindSpell.getBindTime());
                    }
                } else if (pp != null && (distance(target) > 5 || pp.hasWheatObstruction())) {
                    if (pp != null) {
                        pp.traverse();
                    }
                } else {
                    if (!target.isOnScreen()) {
                        camera.toEntity(target);
                    }
                    int degree = getDegree(distance(target));
                    if (camera.getPitchAngle() > degree) {
                        camera.movePitch(degree);
                    }
                    if (magic.isSpellSelected()) {
                        magic.deselectSpell();
                    }
                    checkRun();
                    if (interact.interact(new EntityDestination(bot, target), "Catch", target.getName(), false)) {
                        final int count = lootCount();
                        waitFor(500, new Condition() {
                            @Override
                            public boolean evaluate() {
                                Character<?> interacting = myPlayer().getInteracting();
                                return interacting != null && interacting.getName().equals(target.getName());
                            }
                        });
                        Character<?> interacting = myPlayer().getInteracting();
                        if (interacting != null && interacting.getName().equals(target.getName())) {
                            waitFor(1500, new Condition() {
                                @Override
                                public boolean evaluate() {
                                    return lootCount() > count;
                                }
                            });
                        }
                    }
                    if (script.spawnCamp && script.spawnTile.distance(myPosition()) <= 2 && !target.exists()) {
                        if (script.bindSpell != null && !magic.isSpellSelected() && magic.castSpell(script.bindSpell.getSpell())) {
                            waitFor(1000, new Condition() {
                                @Override
                                public boolean evaluate() {
                                    return magic.isSpellSelected();
                                }
                            });
                        }
                        waitFor(8000, new Condition() {
                            @Override
                            public boolean evaluate() {
                                NPC target = getNearestTarget();
                                return target != null && target.getPosition().distance(script.spawnTile) < 2;
                            }
                        });
                    }
                }
            } else if (script.spawnCamp) {
                if (script.spawnTile.distance(myPosition()) > 1) {
                    System.out.println("Walking back to spawn");
                    PuroPath pp = script.ppg.findPath(closestEmptyTile(script.spawnTile));
                    if (pp != null) {
                        pp.traverse();
                    }
                } else if (getSpawnCampers() > 0) {
                    worldHopper.hop(true);
                } else if (script.bindSpell != null && !magic.isSpellSelected()) {
                    if (magic.castSpell(script.bindSpell.getSpell())) {
                        waitFor(2000, new Condition() {
                            @Override
                            public boolean evaluate() {
                                return magic.isSpellSelected();
                            }
                        });
                    }
                }
            } else if (!script.spawnCamp) {
                if (scoutTile == null) {
                    scoutTile = getClosestScoutPosition();
                } else if (scoutTile.distance(myPosition()) > 4) {
                    System.out.println("Walking around to scout new targets: " + scoutTile);
                    PuroPath pp = script.ppg.findPath(scoutTile);
                    if (pp != null) {
                        pp.traverse();
                    }
                } else {
                    int index = Arrays.asList(scoutTiles).indexOf(scoutTile);
                    scoutTile = scoutTiles[index >= scoutTiles.length - 1 ? 0 : index + 1];
                }
            }
        }
        return Random.smallSleep();
    }

    public Position getClosestScoutPosition() {
        Position closest = null;
        for (Position t : scoutTiles) {
            if (closest == null || t.distance(myPosition()) < closest.distance(myPosition())) {
                closest = t;
            }
        }
        return closest;
    }

    public boolean isBinded() {
        return bindTimer != null && !bindTimer.isFinished();
    }

    public NPC getNearestTarget() {
        return npcs.closest(new Filter<NPC>() {
            @Override
            public boolean match(NPC npc) {
                if (npc == null || npc.getName() == null || npc.getPosition().distance(script.spawnCamp ? script.spawnTile : myPosition()) > 15) {
                    return false;
                }
                for (Impling imp : script.implings) {
                    if (imp.getName().equalsIgnoreCase(npc.getName())) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public int getSpawnCampers() {
        List<Player> ps = players.getAll();
        List<Player> filtered = new ArrayList<>();
        for (Player p : ps) {
            if (p != null && !p.getName().equals(myPlayer().getName()) && p.getPosition().distance(script.spawnTile) <= 2) {
                filtered.add(p);
            }
        }
        return filtered.size();
    }

    public int lootCount() {
        int[] loots = new int[script.implings.length];
        for (int i = 0; i < loots.length; i++) {
            loots[i] = script.implings[i].getLoot();
        }
        return (int) inventory.getAmount(loots);
    }

    @Override
    public boolean validate() {
        return false;
    }
}
