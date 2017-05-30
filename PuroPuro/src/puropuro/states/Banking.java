package puropuro.states;

import api.ATState;
import api.util.Random;
import api.web.PuroPath;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.utility.Condition;
import puropuro.PuroPuro;

import static puropuro.PuroPuro.*;

/**
 * Created by Krulvis on 12-Mar-17.
 */
public class Banking extends ATState<PuroPuro> {

    public Banking(PuroPuro script) {
        super("Banking", script);
    }

    public int[] REQUIREMENTS = {NET, MAGIC_NET, JAR, WATER, EARTH, NATURE};

    @Override
    public int perform() {
        if (!bank.isOpen()) {
            if (Hunting.centerPuroTile.distance(myPosition()) < 50 && Hunting.centerPuroTile.distance(myPosition()) > 5) {
                PuroPath pp = script.ppg.findPath(Hunting.centerPuroTile);
                if (pp != null) {
                    pp.traverse();
                }
            } else {
                NPC banker = npcs.closest("Banker");
                final Item net = inventory.getItem(NET, MAGIC_NET);
                if (banker != null && net != null) {
                    net.interact();
                }
                //openWebBank(zenarisBank);
            }
        } else if (!inventory.onlyContains(REQUIREMENTS)) {
            if (bank.depositAllExcept(REQUIREMENTS)) {
                waitFor(2000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return inventory.onlyContains(REQUIREMENTS);
                    }
                });
            }
        } else if (!inventory.contains(JAR)) {
            if (bank.contains(JAR) && bank.withdrawAll(JAR)) {
                waitFor(2000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return inventory.contains(JAR);
                    }
                });
            } else if (isOutOfAll(JAR)) {
                log("Out of impling jars, stopping script!");
                if (script.restockWhenOut) {
                    stop();
                    //Restock.restock = true;
                } else {
                    stop();
                }
            }
        } else if (script.bindSpell != null && !script.bindSpell.hasRequirements(this)) {
            if (bank.contains(NATURE) && bank.withdrawAll(NATURE)) {
                waitFor(2000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return inventory.contains(NATURE);
                    }
                });
            }
            if (bank.contains(WATER) && bank.withdrawAll(WATER)) {
                waitFor(2000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return inventory.contains(WATER);
                    }
                });
            }
            if (bank.contains(EARTH) && bank.withdrawAll(EARTH)) {
                waitFor(2000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return inventory.contains(EARTH);
                    }
                });
            }
            if (isOutOfAll(NATURE, WATER, EARTH) && !script.bindSpell.hasRequirements(this)) {
                log("Out of runes");
                if (script.restockWhenOut) {
                    stop();
                    //Restock.restock = true;
                } else {
                    stop();
                }
            }
        }
        return Random.smallSleep();
    }

    private boolean hasRequirements() {
        return inventory.contains(JAR)
                && (inventory.getItem(NET, MAGIC_NET) != null || hasNetEquipped())
                && (script.bindSpell == null || script.bindSpell.hasRequirements(this));
    }

    private boolean hasNetEquipped() {
        return equipment.getItem(NET, MAGIC_NET) != null;
    }

    @Override
    public boolean validate() {
        return !hasRequirements();
    }
}
