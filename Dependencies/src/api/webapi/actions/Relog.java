package api.webapi.actions;

import api.ATMethodProvider;
import api.webapi.WebAPI;

/**
 * Created by Krulvis on 16-Feb-17.
 */
public class Relog extends Action {

    public Relog(WebAPI parent) {
        super(parent);
    }

    @Override
    public int loop() {
        if (client.isLoggedIn()) {
            webAPI.getWebConnection().sendJSON("bot/logout", "POST", null);
            logoutTab.logOut();
        }
        if (!client.isLoggedIn()) {
            return -1;
        }
        return 500;
    }
}
