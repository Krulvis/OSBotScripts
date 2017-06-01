package api.webapi.actions;

import api.ATMethodProvider;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.osbot.JS;
import org.osbot.rs07.api.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krulvis on 01-Jun-17.
 */
public class GetPid extends WebAction {

    public GetPid(ATMethodProvider api) {
        super(api, "GET_PID");
    }

    @Override
    public boolean perform() {
        if (isLoggedIn()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        List<Player> ps = players.getAll();
                        List<Player> nearMe = new ArrayList<>();
                        if (ps.size() > 10) {
                            for (Player p : ps) {
                                if (p != null && distance(p) <= 3) {
                                    nearMe.add(p);
                                }
                            }
                        }
                        JsonObject object = new JsonObject();
                        object.add("index", new JsonPrimitive(nearMe.indexOf(myPlayer())));
                        object.add("length", new JsonPrimitive(nearMe.size()));
                        webAPI.getWebConnection().sendJSON("bot/pid", "POST", object);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }).start();
            return true;
        }
        return false;
    }
}
