package api.webapi.actions;

import api.ATMethodProvider;
import api.webapi.WebAPI;

/**
 * Created by Krulvis on 30-May-17.
 */
public abstract class Action extends ATMethodProvider {

    public WebAPI webAPI;

    public Action(WebAPI webAPI) {
        init(webAPI);
        this.webAPI = webAPI;
    }

    public abstract int loop();
}
