package generalstoreseller.util;

import api.ATMethodProvider;
import generalstoreseller.GeneralStoreSeller;
import org.osbot.rs07.api.def.ItemDefinition;

import java.util.ArrayList;


/**
 * Created by s120619 on 1-3-2017.
 */
public class SellableItems extends ATMethodProvider {

    public static int restockAmount = 200;

    //All items
    private ArrayList<SellableItem> all = new ArrayList<>();
    private ArrayList<SellableItem> current = new ArrayList<>();

    private boolean isChecking = false;

    public SellableItems(ATMethodProvider parent) {
        init(parent);
    }

    public void addSellable(SellableItem sellable) {
        this.all.add(sellable);
    }

    public ArrayList<SellableItem> getCurrent() {
        return current;
    }

    public int[] getSellables() {
        int[] ids = new int[current.size()];
        for (int i = 0; i < current.size(); i++) {
            ids[i] = current.get(i).getId();
        }
        return ids;
    }

    public int[] getAmounts() {
        int[] amounts = new int[current.size()];
        for (int i = 0; i < current.size(); i++) {
            amounts[i] = current.get(i).getAmount();
        }
        return amounts;
    }

    public static int calculateProfit(int buyPrice, int highAlchPrice) {
        int profit = 0;
        int lowering = (int) (highAlchPrice * (1.0 / 30.0));
        for (int i = 0; i < 5; i++) {
            profit += highAlchPrice - buyPrice - (lowering * i);
        }
        return profit;
    }


    public boolean isChecking() {
        return isChecking;
    }

    public void reCheckSellables() {
        isChecking = true;
        current = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int[] ids = new int[all.size()];
                for (int i = 0; i < all.size(); i++) {
                    ids[i] = all.get(i).getId();
                }
                prices.getDBPrices(ids);
                for (SellableItem item : all) {
                    item.checkPrices();
                    ItemDefinition def = ItemDefinition.forId(item.getId());
                    if (item.getProfit() > 80) {
                        System.out.println("Adding: " + (def != null ? def.getName() : "") + item.getId());
                        current.add(item);
                    } else {
                        System.out.println("Not Adding: " + (def != null ? def.getName() : "") + item.getId());
                    }
                }
                isChecking = false;
            }
        }, "Sellables re-checking Thread").start();
    }

}
