package generalstoreseller.states;

import api.ATState;
import api.util.Random;
import generalstoreseller.GeneralStoreSeller;


/**
 * Created by Krulvis on 28-Feb-17.
 */
public class Buying extends ATState<GeneralStoreSeller> {

    public Buying(GeneralStoreSeller script) {
        super("Buying", script);
    }

    public boolean resupply = false, isBuying = true;
    public int startCash = -1;

    @Override
    public boolean validate() {
        return resupply || !script.hasSellables();
    }

    @Override
    public int perform() {
        if (!resupply && !hasAllSellables()) {
            resupply = true;
            if (startCash == -1) {
                script.sellables.reCheckSellables();
                startCash = (int) inventory.getAmount(995);
            }
        }
        int[] sellables;
        if (!atGE.isOpen()) {
            if (shop.isOpen()) {
                shop.close();
            }
            atGE.open();
        } else if (!buyTeleports()) {
            return Random.smallSleep();
        } else if (isBuying && !hasAllSellables() && !inventory.isFull() && (sellables = script.sellables.getSellables()) != null) {
            int[] restockAmounts = script.sellables.getAmounts();
            for (int i = 0; i < sellables.length; i++) {
                final int id = sellables[i];
                if (inventory.isFull()) {
                    break;
                }
                if (atGE.getBuyTries(id) < 5 && inventory.getAmount(id, id + 1) < restockAmounts[i]) {
                    if (!atGE.buy(id, restockAmounts[i], 120)) {
                        break;
                    }
                }
            }
        } else if (!script.sellables.isChecking()) {
            atGE.resetBuyTries();
            script.costs += startCash - inventory.getAmount(995);
            startCash = -1;
            isBuying = true;
            resupply = false;
        }
        return Random.smallSleep();
    }

    public boolean buyTeleports() {
        if (!inventory.contains(8007)) {
            atGE.buy(8007, 10, 200);
        } else if (!inventory.contains(8011)) {
            atGE.buy(8011, 10, 200);
        } else {
            return true;
        }
        return false;
    }

    public boolean hasAllSellables() {
        int[] sellables = script.sellables.getSellables();
        int[] amounts = script.sellables.getAmounts();
        if (sellables == null || sellables.length <= 0) {
            return false;
        }
        for (int i = 0; i < sellables.length; i++) {
            int id = sellables[i];
            int amount = amounts[i];
            if (atGE.getBuyTries(id) >= 5) {
                //Skip cuz it's fucked in price
                continue;
            }
            if (inventory.getAmount(id, id + 1) < amount) {
                return false;
            }
        }
        return true;
    }

}
