package generalstoreseller.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import generalstoreseller.GeneralStoreSeller;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.utility.Condition;

/**
 * Created by Krulvis on 16-Feb-17.
 */
public class Selling extends ATState<GeneralStoreSeller> {

    public Selling(GeneralStoreSeller script) {
        super("Selling", script);
    }


    @Override
    public boolean validate() {
        return script.sellables != null && script.sellables.getSellables() != null && script.hasSellables();
    }

    @Override
    public int perform() throws InterruptedException {
        if (!shop.isOpen()) {
            atGE.close();
            bank.close();
            NPC chat = npcs.closest("Chadwell");
            if (distance(script.shopTile) > 4 || (chat != null && !canReach(chat.getPosition()))) {
                traverse();
            } else if (chat != null && interact(chat, "Trade")) {
                waitFor(3000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return shop.isOpen();
                    }
                });
            }
        } else if (isLoggedIn() && script.getSellable() != null) {

            Item[] sellables = script.getInvSellables();
            for (int i = 0; i < sellables.length; i++) {
                Item item = sellables[i];
                if (item != null && shop.sellTotal(item, 5)) {
                    if (i == sellables.length - 1) {
                        sleep(250);
                        waitFor(500, new Condition() {
                            @Override
                            public boolean evaluate() {
                                return script.getSellable() == null;
                            }
                        });
                    }
                }
            }
            if (script.firstWorldTimer == null) {
                script.firstWorldTimer = new Timer(310000);
            }
        } else if (script.firstWorldTimer != null && script.firstWorldTimer.isFinished()) {
            log("Hopping back to first world");
            script.firstWorldTimer = null;
            if (worlds.hop(script.startWorld)) {
                sleep(1000, 2000);
                waitFor(2000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return isLoggedIn() && worlds.getCurrentWorld() == script.startWorld;
                    }
                });
            }
        } else {
            worldHopper.hop(false);
            sleep(Random.nextGaussian(1500, 2000, 250));
        }
        return Random.smallSleep();
    }

    public boolean inEastArdougne() {
        return myPosition().getX() > 2560;
    }

    public boolean underground() {
        return myPosition().getY() > 9000;
    }

    public void traverse() {
        NPC clerk = npcs.closest("Grand Exchange clerk");
        if (clerk != null) {
            Item tab = inventory.getItem("Ardougne teleport");
            if (tab != null && tab.interact()) {
                waitFor(5000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return npcs.closest("Grand Exchange clerk") == null;
                    }
                });
            }
        } else if (underground()) {
            RS2Object pipe = objects.closest(new Filter<RS2Object>() {
                @Override
                public boolean match(RS2Object go) {
                    return go != null && go.getName().equals("Pipe") && go.hasAction("Climb-up");
                }
            });
            if (pipe != null && interact(pipe, "Climb-up")) {
                waitFor(6000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return !underground();
                    }
                });
            } else {
                walkPath(new Position(2514, 9741, 0));
            }
        } else if (inEastArdougne()) {
            RS2Object hole = objects.closest("Dug hole");
            if (hole != null && interact(hole, "Climb-down")) {
                waitFor(4000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return objects.closest("Dug hole") == null;
                    }
                });
            } else if (hole == null || canReach(hole.getPosition())) {
                walkPath(new Position(2567, 3332, 0));
            }
        } else {
            walkPath(script.shopTile);
        }
    }


}
