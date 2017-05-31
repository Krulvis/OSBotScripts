package api.webapi.actions;

import api.ATMethodProvider;
import api.webapi.WebAPI;

/**
 * Created by Krulvis on 30-May-17.
 */
public abstract class WebAction extends ATMethodProvider {

    public final String name;
    private int id;

    public WebAction(ATMethodProvider api, String name) {
        this.name = name;
        init(api);
    }


    public abstract boolean perform();

    public void sendComplete() {
        //webAPI.getWebConnection().sendJSON("");
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
