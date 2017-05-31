package staker.util.webapi;

import api.webapi.actions.WebAction;
import staker.Staker;

/**
 * Created by Krulvis on 31-May-17.
 */
public class ReloadSettings<S extends Staker> extends WebAction {

    private S staker;

    public ReloadSettings(S s) {
        super(s, "RELOAD_SETTINGS");
        this.staker = s;
    }

    @Override
    public boolean perform() {
        staker.getNewSettings();
        return true;
    }
}
