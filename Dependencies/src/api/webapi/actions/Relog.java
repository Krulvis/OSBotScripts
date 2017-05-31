package api.webapi.actions;

import api.ATMethodProvider;
import api.webapi.WebAPI;

/**
 * Created by Krulvis on 16-Feb-17.
 */
public class Relog extends WebAction {

    public Relog(ATMethodProvider parent) {
        super(parent, "RELOG");
    }

    @Override
    public boolean perform() {
        if (client.isLoggedIn()) {
            webAPI.getWebConnection().sendJSON("bot/logout", "POST", null);
            logoutTab.logOut();
        }
        if (!client.isLoggedIn()) {
            return true;
        }
        return false;
    }
}
