package api.wrappers.grandexchange;

import api.ATMethodProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Krulvis on 11-Apr-17.
 */
public class GrandExchange extends ATMethodProvider {

    public final ATMethodProvider parent;
    public final static String RSBOTS_API_URL = "http://rsbots.org/api/item/";
    public static final String GE_API_URL_BASE = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=";

    public GrandExchange(ATMethodProvider parent) {
        init(parent);
        this.parent = parent;
    }

    public static JsonElement getItemInfo(int id) {
        URLConnection conn = null;
        try {
            conn = new URL(RSBOTS_API_URL + id).openConnection();
            JsonParser parser = new JsonParser();
            InputStreamReader ipr = new InputStreamReader(conn.getInputStream());
            JsonElement parsed = parser.parse(ipr);
            if (parsed != null) {
                JsonArray ar = parsed.getAsJsonArray();
                for (JsonElement el : ar) {
                    if (el != null && el.isJsonObject()) {
                        return el.getAsJsonObject();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonNull.INSTANCE;
    }
}
