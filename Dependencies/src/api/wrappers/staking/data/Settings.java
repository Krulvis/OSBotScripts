package api.wrappers.staking.data;


import api.ATMethodProvider;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.utility.Condition;

/**
 * Created by Krulvis on 11-1-2016.
 */
public class Settings {

    public enum Weapon {
        BOX(0, 0, 0, -1),
        ABYSSAL_WHIP(82, 82, 0, 4151),
        VINE_WHIP(90, 86, 0, 12006),
        HASTA(85, 75, 13, -1),
        DRAGON_SCIMITAR(67, 66, 1, 4587),
        DRAGON_DAGGER(40, 40, 0, 5698);
        public int strBonus;
        public int attBonus;
        public int defBonus;
        public int itemId;

        Weapon(int a, int s, int d, int id) {
            attBonus = a;
            strBonus = s;
            defBonus = d;
            itemId = id;
        }

        public boolean isWearing(ATMethodProvider api) {
            return api.equipment.getItem(itemId) != null;
        }

        public boolean equip(ATMethodProvider api) {
            if (isWearing(api)) {
                return true;
            } else {
                Item i = api.inventory.getItem(itemId);
                if (i != null && i.interact()) {
                    api.waitFor(new Condition() {
                        @Override
                        public boolean evaluate() {
                            return isWearing(api);
                        }
                    }, 1500);
                }
            }
            return isWearing(api);
        }

        public boolean hasWeapon(ATMethodProvider api) {
            return isWearing(api) || hasInInventory(api);
        }

        public boolean hasInInventory(ATMethodProvider api) {
            return api.inventory.contains(itemId);
        }
    }

    public enum Style {
        ACCURATE(3, 0, 0),
        AGGRESSIVE(0, 3, 0),
        DEFENSIVE(0, 0, 3),
        CONTROLLED(1, 1, 1),;
        public int att, str, def;

        Style(int a, int s, int d) {
            this.att = a;
            this.str = s;
            this.def = d;
        }
    }
}
