package generalstoreseller.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import generalstoreseller.GeneralStoreSeller;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.utility.Condition;

/**
 * Created by Krulvis on 16-Feb-17.
 */
public class Selling extends ATState<GeneralStoreSeller> {

    public Selling(GeneralStoreSeller script) {
        super("Selling", script);
    }

    private Position shopTile = new Position(2466, 3285, 0);

    @Override
    public boolean validate() {
        return !Buying.resupply && script.sellables != null && script.sellables.getSellables() != null && script.hasSellables();
    }

    @Override
    public int perform() throws InterruptedException {
        if (!shop.isOpen()) {
            atGE.close();
            bank.close();
            NPC chat = npcs.closest("Chadwell");
            if (distance(shopTile) > 4 || (chat != null && !canReach(chat.getPosition()))) {
                walkPath(shopTile);
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


}
