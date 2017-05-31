package api.wrappers.grandexchange;

import api.ATMethodProvider;
import api.util.Timer;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by Krulvis on 11-Apr-17.
 */
public class GrandExchange extends ATMethodProvider {

    public final ATMethodProvider parent;
    public final static String RSBOTS_API_URL = "http://rsbots.org/api/item/";
    public static final String GE_API_URL_BASE = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=";
    public HashMap<Integer, Integer> onlinePriceMap = new HashMap<>();
    public HashMap<Integer, Timer> priceRetrievalTimers = new HashMap<>();

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

    public int getExchangePrice(final int id) {
        double price = -1;
        switch (id) {
            case 8016://enchant sapphire
                price = 800;
                break;
            default:
                try {
                    System.out.print("Getting GE price for: " + id);
                    URLConnection conn = new URL(GE_API_URL_BASE + id).openConnection();
                    JsonParser parser = new JsonParser();
                    InputStreamReader ipr = new InputStreamReader(conn.getInputStream());
                    BufferedReader br = new BufferedReader(ipr);
                    String s;
                    JsonElement parsed = parser.parse(ipr);
                    if (parsed != null) {
                        JsonElement el = parsed.getAsJsonObject().get("item");
                        if (el != null) {
                            JsonObject item = el.getAsJsonObject();
                            if (item != null) {
                                JsonElement curr = item.get("current");
                                if (curr != null) {
                                    JsonObject itemObj = curr.getAsJsonObject();
                                    if (itemObj != null) {
                                        JsonElement priceEl = itemObj.get("price");
                                        if (priceEl != null) {
                                            String priceString = priceEl.getAsString().replaceAll(",", "");
                                            if (priceString.contains("m")) {
                                                price = Double.parseDouble(priceString.replaceAll("m", ""));
                                                price = price * 1000000;
                                            } else if (priceString.contains("k")) {
                                                price = Double.parseDouble(priceString.replaceAll("k", ""));
                                                price = price * 1000;
                                            } else {
                                                price = Double.parseDouble(priceString);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ipr.close();
                } catch (JsonParseException | IllegalStateException e) {
                    priceRetrievalTimers.put(id, new Timer(5000));
                    e.printStackTrace();
                } catch (MalformedURLException ignored) {

                } catch (FileNotFoundException e) {
                    return -1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        System.out.println(": " + (int) price);
        return (int) price;
    }

    public int getPrice(int id) {
        int price = -1;
        switch (id) {
            case 8016://enchant sapphire
                price = 800;
                break;
            default:
                if (id == -1 || id == 0) {
                    return 0;
                }
                if (onlinePriceMap.containsKey(id) && onlinePriceMap.get(id) > -1) {
                    return onlinePriceMap.get(id);
                }
                if (priceRetrievalTimers.containsKey(id) && !priceRetrievalTimers.get(id).isFinished()) {
                    System.out.println("Timeout on price retrieving!");
                    return -1;
                }
                JsonElement el = getItemInfo(id);
                if (!el.isJsonNull()) {
                    JsonElement p = el.getAsJsonObject().get("price");
                    if (p != null && p.getAsInt() > 1) {
                        price = p.getAsInt();
                    } else {
                        price = getExchangePrice(id);
                    }
                } else {
                    price = getExchangePrice(id);
                }
                break;

        }
        if (price > 1) {
            onlinePriceMap.put(id, price);
        }
        return price;
    }
}
