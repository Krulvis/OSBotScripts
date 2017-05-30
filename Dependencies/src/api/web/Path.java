package api.web;

import api.ATMethodProvider;

/**
 * Created by Krulvis on 13-Mar-17.
 */
public abstract class Path {

    public ATMethodProvider mp;

    public Path(ATMethodProvider mp){
        this.mp = mp;
    }

    public abstract boolean traverse();

    public abstract int getCost();
}
