package api.web.actions;

import api.ATMethodProvider;

public abstract class Action extends ATMethodProvider {

    public Action(ATMethodProvider mp) {
        init(mp);
    }

    public abstract boolean traverse();

    public abstract boolean canUse();

    public abstract double getCost();
}