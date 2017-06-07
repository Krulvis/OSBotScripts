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

    public static boolean resupply = false, isBuying = true;
    public static int startCash = -1;


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
        if (!grandExchange.isOpen()) {
            if (shop.isOpen()) {
                shop.close();
            }
            grandExchange.open();
        } else if (!buyTeleports()) {
            return Random.smallSleep();
        } else if (isBuying && !hasAllSellables() && !inventory.isFull() && (sellables = script.sellables.getSellables()) != null) {
            System.out.println();
            int[] restockAmounts = script.sellables.getAmounts();
            for (int i = 0; i < sellables.length; i++) {
                final int id = sellables[i];
                if (inventory.isFull()) {
                    break;
                }
                if (grandExchange.getBuyTries(id) < 5 && inventory.getAmount(id, id + 1) < restockAmounts[i]) {
                    if (!grandExchange.buy(id, restockAmounts[i], 120)) {
                        break;
                    }
                }
            }
        } else if (!script.sellables.isChecking()) {
            grandExchange.resetBuyTries();
            script.costs += startCash - inventory.getAmount(995);
            startCash = -1;
            isBuying = true;
            resupply = false;
        }
        return Random.smallSleep();
    }

    public boolean buyTeleports() {
        if (!inventory.contains(8007)) {
            grandExchange.buy(8007, 10, 200);
        } else if (!inventory.contains(8011)) {
            grandExchange.buy(8011, 10, 200);
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
            if (grandExchange.getBuyTries(id) >= 5) {
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
