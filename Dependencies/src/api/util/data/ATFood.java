package api.util.data;

import api.ATMethodProvider;
import org.osbot.rs07.api.model.Item;

/**
 * Created by Krulvis on 04-Apr-17.
 */
public class ATFood extends ATMethodProvider {

    public Food selectedFood;

    public enum Food {

        SHRIMP("Shrimp", 3, 315),
        CAKES("Cakes", 5, 1895, 1893, 2309, 1901, 1891),
        TROUT("Trout", 8, 333),
        SALMON("Salmon", 9, 329),
        PEACH("Peach", 9, 6883),
        TUNA("Tuna", 10, 361),
        LOBSTER("Lobster", 12, 379),
        BASS("Swordfish", 13, 365),
        SWORDFISH("Swordfish", 14, 373),
        POTATO_CHEESE("Potato with cheese", 16, 6705),
        MONKFISH("Monkfish", 16, 7946),
        SHARK("Shark", 20, 385),
        KARAMBWAN("Karambwan", 16, 3144),;

        private String name;
        public int[] ids;
        public int healing;

        Food(String name, int healing, int... id) {
            this.name = name;
            this.ids = id;
            this.healing = healing;
        }

        public int getId() {
            return ids[0];
        }

        public int[] getIds() {
            return ids;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }
    }

    public boolean hasFood(Food food) {
        return inventory.contains(food.getIds());
    }

    public boolean eat(Food food) {
        Item invF = inventory.getItem(food.getIds());
        return invF != null && invF.interact("Eat");
    }

    public boolean eatAnything() {
        for (Food f : Food.values()) {
            if (hasFood(f)) {
                return eat(f);
            }
        }
        return false;
    }

    public boolean hasAnyFood() {
        for (Food f : Food.values()) {
            if (hasFood(f)) {
                return true;
            }
        }
        return false;
    }

    public ATFood(ATMethodProvider parent) {
        init(parent);
    }
}
