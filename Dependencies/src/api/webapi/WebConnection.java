package api.webapi;

import api.util.Updater;
import api.webapi.actions.Restart;
import api.webapi.actions.Update;
import com.google.gson.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Krulvis on 30-May-17.
 */
public class WebConnection {

    public boolean isConnected = false;
    public WebAPI webAPI;
    public String key;
    public int id;

    public WebConnection(WebAPI webAPI) {
        this.webAPI = webAPI;
        //LoginHandler.killScriptOnBan = false;
        this.webAPI.setCurrentAccount(null);
        while (!isVerrified()) {
            try {
                System.out.println("Getting Key & ID");
                checkVerified();
                if (!isVerrified()) {
                    Thread.sleep(60000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isConnected = true;
    }

    /**
     * Initiating the communication with Web API, needs key + id to continue!
     */
    private void checkVerified() {
        JsonElement ele = getJson(WebAPI.URL + "server/verified/");
        if (ele == JsonNull.INSTANCE) {
            return;
        }
        for (JsonElement el : ele.getAsJsonArray()) {
            JsonObject ob = el.getAsJsonObject();
            if (!ob.isJsonNull()) {
                JsonObject o = el.getAsJsonObject();
                JsonElement k = o.get("key");
                key = k != null && !k.isJsonNull() ? k.getAsString() : null;
                JsonElement i = o.get("id");
                id = i != null && !i.isJsonNull() ? i.getAsInt() : -1;
                JsonElement os_ = o.get("os");
                if (os_ != null && !os_.isJsonNull()) {
                    String os = os_.getAsString();
                    if (os.toLowerCase().contains("nux")) {
                        webAPI.restart.startBot = "sh ./StartBots.sh";
                        webAPI.update.updateScripts = "sh ./Update.sh";
                        System.out.println("Recognized OS as Linux: " + webAPI.restart.startBot);
                    } else if (os.toLowerCase().contains("windows")) {
                        webAPI.restart.startBot = "cmd /c start StartBots.bat";
                        webAPI.update.updateScripts = "cmd /c start Update.bat";
                        System.out.println("Recognized OS as Windows: " + webAPI.restart.startBot);
                    }
                }
                System.out.println("key: " + key + ", id: " + id + (os_ != null && !os_.isJsonNull() ? ", os: " + os_.getAsString() : ""));
            }
        }
    }

    public boolean hasKey() {
        return key != null;
    }

    public boolean hasId() {
        return id >= 0;
    }

    public boolean isVerrified() {
        return hasKey() && hasId();
    }

    public StringBuilder getURLWithID(String location) {
        return getURL(location, true);
    }

    /**
     * Get the Correct URL (adds ID + Key)
     * If a full URL (with http) is send, it will return that instead.
     *
     * @param location
     * @param addKey
     * @return
     */
    public StringBuilder getURL(String location, boolean addKey) {
        if (location.startsWith("http")) {
            return new StringBuilder(location);
        }
        StringBuilder url = new StringBuilder(WebAPI.URL);
        url.append((WebAPI.URL.charAt(WebAPI.URL.length() - 1) == '/' ? "" : "/"));
        if (location.contains("bot")) {
            url.append("bot/" + id);
            url.append(location.length() > 3 ? location.substring(3) : "");
        } else {
            url.append(location);
        }
        if (addKey && key != null) {
            url.append("?key=" + key);
        }
        return url;
    }

    /**
     * Send URL with JSON body, used for POST, PUT requests
     *
     * @param location
     * @param method
     * @param object
     * @return
     */
    public JsonElement sendJSON(String location, String method, JsonObject object) {
        try {
            if (object == null) {
                object = new JsonObject();
            }
            if (key != null) {
                object.add("key", new JsonPrimitive(key));
            }
            URL url = new URL(getURL(location, false).toString());
            System.out.println("Send " + method.toUpperCase() + " Request to: " + url);
            if (object.toString().length() < 750) {
                System.out.println("With Body: " + object.toString());
            }
            byte[] postData = object.toString().getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.toUpperCase());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", Updater.USER_AGENT);
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            JsonParser parser = new JsonParser();
            InputStreamReader ipr = new InputStreamReader(conn.getInputStream());
            //dumpResponse(conn.getInputStream());
            JsonElement ele = parser.parse(ipr);
            if (ele != null && !ele.isJsonNull()) {
                return ele;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonNull.INSTANCE;
    }

    private void dumpResponse(InputStream ips) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(ips));
        String line;
        System.out.println("Dumping response.......");
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println("................");

    }

    /**
     * Sends GET requests, returns JsonElement
     *
     * @param location
     * @return
     */
    public JsonElement getJson(String location) {
        try {
            URL url = new URL(getURLWithID(location).toString());
            System.out.println("Send GET Request to: " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //Content-Type doesn't really matter for GET requests since it won't be sending bodies anyways.
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("User-Agent", Updater.USER_AGENT);
            conn.setRequestProperty("charset", "utf-8");
            JsonParser parser = new JsonParser();
            InputStreamReader ipr = new InputStreamReader(conn.getInputStream());
            JsonElement ele = parser.parse(ipr);
            if (ele != null && !ele.isJsonNull()) {
                return ele;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonNull.INSTANCE;
    }
}
