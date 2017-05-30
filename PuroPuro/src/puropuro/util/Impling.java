package puropuro.util;

import api.ATMethodProvider;
import org.osbot.rs07.api.filter.NameFilter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;

/**
 * Created by Krulvis on 15-Mar-17.
 */
public enum Impling {
    LUKCY("Lucky impling", 89, 11256),
    DRAGON("Dragon impling", 83, 11256),
    NINJA("Ninja impling", 74, 11254),
    MAGPIE("Magpie impling", 65, 11252),
    NATURE("Nature impling", 58, 11250),
    ECLECTRIC("Eclectic impling", 50, 11248, new Position(2591, 4295, 0)),
    ESSENCE("Essence impling", 42, 11246, new Position(2575, 4337, 0)),
    EARTH("Earth impling", 36, 11244),
    GOURMET("Gourmet impling", 28, 11242, new Position(2614, 4298, 0)),
    YOUNG("Young impling", 22, 11240, new Position(2612, 4309, 0)),
    BABY("Baby impling", 17, 11238),;
    private String name;
    private int hunterLevel;
    private int loot;
    private Position[] spawnTiles;
    Impling(String Name, int levelRequirement, int lootJar, Position... spawnTiles) {
        this.name = Name;
        this.hunterLevel = levelRequirement;
        this.loot = lootJar;
        this.spawnTiles = spawnTiles != null ? spawnTiles : new Position[]{};
    }

    public int getLevelRequirement() {
        return hunterLevel;
    }

    public String getName() {
        return name;
    }

    public NPC getNearestTarget(ATMethodProvider mp) {
        return mp.npcs.closest(new NameFilter<NPC>(this.getName()));
    }

    public int getLoot() {
        return loot;
    }

    public static int[] getAllJars(){
        int[] loots = new int[values().length];
        for(int i = 0; i < loots.length; i++){
            loots[i] = values()[i].getLoot();
        }
        return loots;
    }

    public Position[] getSpawnTiles(){
        return spawnTiles;
    }

    public Position getSpawnTile(){
        return spawnTiles.length > 0 ? spawnTiles[0] : null;
    }

}
