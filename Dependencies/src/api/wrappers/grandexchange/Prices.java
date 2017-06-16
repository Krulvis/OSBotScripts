package api.wrappers.grandexchange;

import api.ATMethodProvider;
import api.util.Updater;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Krulvis on 07-Jun-17.
 */
public class Prices extends ATMethodProvider{

    public Prices(ATMethodProvider parent){
        init(parent);
    }
    
    private HashMap<Integer, Integer> sellPriceMap = new HashMap<>();
    private HashMap<Integer, Integer> buyPriceMap = new HashMap<>();

    private HashMap<Integer, Long> sellTsMap = new HashMap<>();
    private HashMap<Integer, Long> buyTsMap = new HashMap<>();

    public int maxTimeMinutes = 30;

    public int getBuyPrice(final int itemId) {
        if (checkedBuyPrice(itemId, maxTimeMinutes)) {
            return buyPriceMap.get(itemId);
        }
        return GrandExchange.getPrice(itemId);
    }

    public int getSellPrice(final int itemId) {
        if (checkedSellPrice(itemId, maxTimeMinutes)) {
            return sellPriceMap.get(itemId);
        }
        return GrandExchange.getPrice(itemId);
    }

    public boolean checkedBuyPrice(final int itemId) {
        return buyPriceMap.containsKey(itemId);
    }

    public boolean checkedSellPrice(final int itemId) {
        return sellPriceMap.containsKey(itemId);
    }

    public boolean checkedSellPrice(final int itemId, final int maxTimeMinutes) {
        return sellPriceMap.containsKey(itemId) && getTimeSinceLUSell(itemId) < maxTimeMinutes * 60 * 1000;
    }

    public boolean checkedBuyPrice(final int itemId, final int maxTimeMinutes) {
        return buyPriceMap.containsKey(itemId) && getTimeSinceLUBuy(itemId) < maxTimeMinutes * 60 * 1000;
    }

    public long getTimeSinceLUBuy(final int itemId) {
        return getTimeSinceLU(itemId, true);
    }

    public long getTimeSinceLUSell(final int itemId) {
        return getTimeSinceLU(itemId, false);
    }

    public long getTimeSince(final long time) {
        return System.currentTimeMillis() - time;
    }

    public long getTimeSinceLU(final int itemId, final boolean buy) {
        if (buy) {
            if (buyTsMap.containsKey(itemId)) {
                return getTimeSince(buyTsMap.get(itemId));
            }
        } else if (sellTsMap.containsKey(itemId)) {
            return getTimeSince(sellTsMap.get(itemId));
        }
        return Long.MAX_VALUE;
    }

    public long getLastUpdateSell(final int itemId) {
        return sellTsMap.containsKey(itemId) ? sellTsMap.get(itemId) : 0;
    }

    public long getLastUpdateBuy(final int itemId) {
        return buyTsMap.containsKey(itemId) ? buyTsMap.get(itemId) : 0;
    }

    public void setBuyPrice(final int itemId, final int price) {
        setDBPrice(itemId, price, true);
        setBuyPrice(itemId, price, System.currentTimeMillis());
    }

    public void setSellPrice(final int itemId, final int price) {
        setDBPrice(itemId, price, false);
        setSellPrice(itemId, price, System.currentTimeMillis());
    }

    public void setBuyPrice(final int itemId, final int price, final long time) {
        setPrice(itemId, price, time, true);
    }

    public void setSellPrice(final int itemId, final int price, final long time) {
        setPrice(itemId, price, time, false);
    }

    public void setPrice(final int itemId, final int price, final long time, final boolean buy) {
        if (buy) {
            buyPriceMap.put(itemId, price);
            buyTsMap.put(itemId, time);
        } else {
            sellPriceMap.put(itemId, price);
            sellTsMap.put(itemId, time);
        }
    }

    public void getDBPrices(final int... ids) {
        if (ids == null || ids.length == 0) {
            return;
        }

        StringBuilder urlString = new StringBuilder("http://atscripts.com/scripts/prices/getPrice.php?");
        boolean first = true;
        for (int id : ids) {
            if (first) {
                urlString.append("item[]=").append(id);
                first = false;
            } else {
                urlString.append("&item[]=").append(id);
            }
        }
        long currTimeLong = System.currentTimeMillis();
        System.out.println("Getting prices from DB: " + urlString.toString());
        URLConnection conn;
        try {
            conn = new URL(urlString.toString()).openConnection();
            JsonParser parser = new JsonParser();
            InputStreamReader ipr = new InputStreamReader(conn.getInputStream());
            JsonElement parsed = parser.parse(ipr);
            if (parsed != null) {
                for (JsonElement ele : parsed.getAsJsonArray()) {
                    JsonObject obj = ele.getAsJsonObject();
                    if (obj != null) {
                        int id = obj.get("itemId") != null ? obj.get("itemId").getAsInt() : -1;
                        int sell_price = obj.get("sell_price") != null ? obj.get("sell_price").getAsInt() : -1;
                        int buy_price = obj.get("buy_price") != null ? obj.get("buy_price").getAsInt() : -1;
                        JsonElement sell_ts_json = obj.get("sell_update");
                        JsonElement buy_ts_json = obj.get("buy_update");
                        Timestamp sell_ts = sell_ts_json != null && !sell_ts_json.isJsonNull() ? Timestamp.valueOf(sell_ts_json.getAsString()) : new Timestamp(0);
                        Timestamp buy_ts = buy_ts_json != null && !buy_ts_json.isJsonNull() ? Timestamp.valueOf(buy_ts_json.getAsString()) : new Timestamp(0);
                        long sell = getConvertedTime(sell_ts);
                        long buy = getConvertedTime(buy_ts);
                        if (sell_price > 0 && getLastUpdateSell(id) < sell) {
                            setSellPrice(id, sell_price, sell);
                        }
                        if (buy_price > 0 && getLastUpdateBuy(id) < buy) {
                            setBuyPrice(id, buy_price, buy);
                        }
                        System.out.println("Found in DB: " + id + ", buy: " + buy_price + ", LU: " + getTimeSince(getLastUpdateBuy(id)) + ", sell: " + sell_price + ", LU: " + getTimeSince(getLastUpdateBuy(id)));
                    }
                }
            } else {
                System.out.println("Something failed trying to get price for: " + ids);
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }


    }

    public void setDBPrice(final int itemId, final int price, final boolean buy) {
        System.out.println("Setting DB Price for: " + itemId + ", " + (buy ? "buy: " : "sell: ") + price);
        Updater.send("http://atscripts.com/scripts/prices/setPrice.php?item=" + itemId + "&" + (buy ? "buy" : "sell") + "_price=" + price);
    }

    public void resetPrices() {
        for (Integer id : buyTsMap.keySet()) {
            buyTsMap.put(id, (long) 0);
        }
        for (Integer id : sellTsMap.keySet()) {
            sellTsMap.put(id, (long) 0);
        }
    }

    public static long getConvertedTime(Timestamp ts){
        Calendar calender = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("ECT"));

        calender.setTime(ts);
        //System.out.println("Original time: " + df.format(calender.getTime()));
        df.setTimeZone(TimeZone.getDefault());
        //System.out.println("Converted time: " + df.format(calender.getTime()));
        return calender.getTime().getTime();
    }

}

