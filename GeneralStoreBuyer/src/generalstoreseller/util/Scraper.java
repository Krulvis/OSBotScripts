package generalstoreseller.util;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by Krulvis on 16-Feb-17.
 */
public class Scraper {

    private final static String RSBUDDY_SUMMARY = "https://rsbuddy.com/exchange/summary.json";

    public static void main(String... args) throws IOException {

        URL url = new URL(RSBUDDY_SUMMARY);
        InputStreamReader ips = new InputStreamReader(url.openConnection().getInputStream());
        ArrayList<JsonObject> profitList = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(ips);
        if (el != null && !el.isJsonNull()) {
            JsonObject o = el.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> objects = o.entrySet();
            //System.out.println(o);
            Iterator<Map.Entry<String, JsonElement>> it = objects.iterator();
            int i = 0;
            Map.Entry<String, JsonElement> object;
            while (i < 10000 && it.hasNext() && (object = it.next()) != null) {
                int id = Integer.parseInt(object.getKey());
                JsonObject info = object.getValue().getAsJsonObject();
                int buy = info.get("overall_average").getAsInt();
                int ha = (int) (info.get("sp").getAsInt() * 0.6);
                int profit = SellableItems.calculateProfit(buy, ha);
                if (profit < 500 || profit > 10000 || buy <= 0) {
                    continue;
                }
                info.add("costs", new JsonPrimitive(profit));
                //System.out.println("Name: " + info.get("name").getAsString() + ", Profit: " + costs);
                checkList(profitList, info, profit);
                i++;
            }
        }
        System.out.println("FINISHED PARSING ITEMS!!!");

        for (int i = 0; i < 100 && i < profitList.size(); i++) {
            JsonObject obj = profitList.get(i);
            System.out.println(obj.get("name").getAsString() + ", Buy:  " + obj.get("overall_average").getAsInt() + ", Profit: " + obj.get("costs").getAsInt());
        }

        /*System.out.println("Profit of: Adamant Warhammer is: " + calculateProfit(GrandExchange.getPrice(1237), GrandExchange.getHighAlch(1237)));
        for (int id : GeneralStoreSeller.startSellables) {
            long start = System.currentTimeMillis();
            System.out.println("Profit of: " + id + " is: " + calculateProfit(GrandExchange.getPrice(id), GrandExchange.getHighAlch(id)));
            System.out.println("Took: " + (System.currentTimeMillis() - start));
        }*/
    }


    private static void checkList(ArrayList<JsonObject> list, JsonObject item, int profit) {
        boolean added = false;
        for (int i = 0; i < list.size(); i++) {
            JsonObject o = list.get(i);
            int p = o.get("costs").getAsInt();
            if (profit > p) {
                list.add(i, item);
                added = true;
                break;
            }
        }
        if (!added) {
            list.add(item);
        }
    }

}
