package api.web;

import api.ATMethodProvider;
import org.osbot.rs07.accessor.XClippingPlane;
import org.osbot.rs07.api.map.Position;

/**
 * Created by Krulvis on 14-Mar-17.
 */
public class Flag {
    public static final int W_NW = 0b1;
    public static final int W_N = 0b10;
    public static final int W_NE = 0b100;
    public static final int W_E = 0b1000;
    public static final int W_SE = 0b10000;
    public static final int W_S = 0b100000;
    public static final int W_SW = 0x1000000;
    public static final int W_W = 0b10000000;
    public static final int BLOCKED = 0b100000000;
    public static final int BLOCKED2 = 0b1000000000000000000000;
    public static final int BLOCKED4 = 0b1000000000000000000;
    public static final int WATER = 0b1001010000000000100000000;


    //Used for RS2Objects
    public static final int WEST = 0x1;
    public static final int NORTH = 0x2;
    public static final int EAST = 0x4;
    public static final int SOUTH = 0x8;

    public static int getFlag(ATMethodProvider mp, Position pos) {
        XClippingPlane[] clippingPlanes = mp.client.accessor.getClippingPlanes();
        int[][] clippingPlane = clippingPlanes[mp.myPlayer().getZ()].getTileFlags();
        return clippingPlane[pos.getLocalX(mp.getBot())][pos.getLocalY(mp.getBot())];
    }

}
