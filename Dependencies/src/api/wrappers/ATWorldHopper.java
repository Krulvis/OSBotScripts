package api.wrappers;

import api.ATMethodProvider;

import java.util.ArrayList;

/**
 * Created by Krulvis on 23-Mar-17.
 */
public class ATWorldHopper extends ATMethodProvider {

    public ATWorldHopper(ATMethodProvider parent) {
        init(parent);
    }

    public boolean hop(boolean randomHop) {
        return hop(getWorlds(true), randomHop);
    }

    public boolean hop(int[] world_list, boolean randomHop) {
        int curWorld = worlds.getCurrentWorld();
        int world;
        if (randomHop) {
            System.out.println("Random hop");
            do {
                world = world_list[random(world_list.length - 1)];
                world = world < 300 ? world + 300 : world;
            } while (world == -1 || world == curWorld);
        } else {
            System.out.println("Synchronized hop");
            world = world_list[0];
            world = world < 300 ? world + 300 : world;
            for (int i = 0; i < world_list.length; i++) {
                int selectionWorld = world_list[i];
                selectionWorld = selectionWorld < 300 ? selectionWorld + 300 : selectionWorld;
                if (selectionWorld == curWorld) {
                    world = i < (world_list.length - 1) ? world_list[i + 1] : world_list[0];
                    break;
                }
            }
        }
        if (world != -1) {
            log("Hopping to world " + world + ".");
            closeAllInterfaces();
            return worlds.hop(world);
        }
        return false;
    }

    public int[] getWorlds(boolean member) {
        final int totalLevel = getTotalLevel();
        System.out.println("Total level: " + totalLevel);
        ArrayList<Integer> worlds = new ArrayList<>();
        if (member) {
            for (int i = 0; i < WORLDS.length; i++) {
                Integer world = WORLDS[i];
                switch (world) {
                    case 349:
                        if (totalLevel >= 2000) {
                            System.out.println("Adding world: " + 349);
                            worlds.add(world);
                        }
                        break;
                    case 353:
                        if (totalLevel >= 1250) {
                            System.out.println("Adding world: " + 353);
                            worlds.add(world);
                        }
                        break;
                    case 361:
                        if (totalLevel >= 2000) {
                            System.out.println("Adding world: " + 361);
                            worlds.add(world);
                        }
                        break;
                    case 366:
                        if (totalLevel >= 1500) {
                            System.out.println("Adding world: " + 366);
                            worlds.add(world);
                        }
                        break;
                    case 373:
                        if (totalLevel >= 1750) {
                            System.out.println("Adding world: " + 373);
                            worlds.add(world);
                        }
                        break;
                    default:
                        worlds.add(world);
                        break;
                }
            }
        } else {
            return F2P_WORLDS;
        }
        int[] worlds_array = new int[worlds.size()];
        for (int i = 0; i < worlds.size(); i++) {
            System.out.print(", " + worlds.get(i));
            worlds_array[i] = (int) worlds.get(i);
        }
        return worlds_array;
    }

    public boolean closeAllInterfaces() {
        return bank.close() && shop.close() && atGE.close();
    }


    public static final int[] WORLDS = {
        /*301,*/ 302, 303, 304, 305, /*306, 308,*/ 309, 310, 311, 312, /*313,*/ 314, /*316, */ 317, 318,
            /*319, 320,*/ 321, 322, 323, 324, /*325, 326, */ 327, 328, 330, 331, 332, 333, 334, /*335, */336, /*337,  338,*/ 339,
            340, 341, 342, 343, 344, /*345,*/ 346, 347, 348, 349, 350, 351, 352, 353, 354, 355, 356, /*357,*/ 358, 359, 360,
            361, 362, /*365,*/ 366, 367, 368, 369, 370, 373, /*374,*/ 375, 376, 377, /*378,*/ 386
    };

    public static final int[] F2P_WORLDS = {
            301, 308, 316, 326, 335, 382, 383, 384, 393, 394
    };
}
