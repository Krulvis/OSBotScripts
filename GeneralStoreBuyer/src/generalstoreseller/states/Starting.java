package generalstoreseller.states;

import api.ATState;
import api.util.Random;
import generalstoreseller.GeneralStoreSeller;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.utility.Condition;

/**
 * Created by Krulvis on 07-Jun-17.
 */
public class Starting extends ATState<GeneralStoreSeller> {

    public Starting(GeneralStoreSeller script) {
        super("Starting", script);
    }

    @Override
    public int perform() throws InterruptedException {
        if (script.startWorld == -1 && isLoggedIn()) {
            script.startWorld = worlds.getCurrentWorld();
        }

        if (!script.loadGUI && script.sellables == null) {
            script.loadSettings();
        }

        if (inventory.contains("A magical scroll")) {
            if (!canContinue()) {
                Item scroll = inventory.getItem("A magical scroll");
                if (scroll != null && scroll.interact()) {
                    waitFor(new Condition() {
                        @Override
                        public boolean evaluate() {
                            return canContinue();
                        }
                    }, 3000);
                }
            } else {
                spaceBar();
                sleep(300, 500);
            }
        }
        return Random.smallSleep();
    }

    @Override
    public boolean validate() {
        return script.startWorld == -1 || inventory.contains("A magical scroll");
    }
}
