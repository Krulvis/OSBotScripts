package api.webapi.actions;

import api.ATMethodProvider;
import api.webapi.WebAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
        JsonObject obj = new JsonObject();
        obj.addProperty("id", getId());
        webAPI.getWebConnection().sendJSON("bot/action", "PUT", obj);
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
