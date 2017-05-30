package api.util;

import api.ATScript;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Created by Krulvis on 12-Mar-17.
 */
public class Updater {

    private final static int UPDATE_INTERVAL = 10, MINUTE = 60 * 1000;

    private HashMap<String, Integer> gainedValues, gainedValuesDaily;
    private long lastUpdate;
    private ATScript script;
    private String name;

    public Updater(ATScript script) {
        this.script = script;
        this.name = script.getName().toLowerCase();
        lastUpdate = System.currentTimeMillis();
        gainedValues = new HashMap<>();
        gainedValuesDaily = new HashMap<>();
    }

    public void updateStatsDaily(String[] names, int[] values) {
        updateStats(script.getForumUsername(), names, values, true);
    }

    public void updateStats(String[] names, int[] values) {
        updateStats(script.getForumUsername(), names, values, false);
    }

    private void updateStats(String username, String[] names, int[] values, boolean daily) {
        try {
            int minutes = daily ? 10 : (int) (System.currentTimeMillis() - lastUpdate) / MINUTE;

            String toUpdate = "";
            for (int i = 0; i < names.length; i++) {
                int gained;
                if (daily) {
                    gained = (values[i] - (gainedValuesDaily.containsKey(names[i]) ? gainedValuesDaily.get(names[i]) : 0));
                    gainedValuesDaily.put(names[i], values[i]);
                } else {
                    gained = (values[i] - (gainedValues.containsKey(names[i]) ? gainedValues.get(names[i]) : 0));
                    gainedValues.put(names[i], values[i]);
                }
                toUpdate += "&" + names[i] + "=" + gained;
            }
            lastUpdate = System.currentTimeMillis();
            String link = "http://atscripts.com/scripts/updater" + (daily ? "daily" : "uid") + ".php?"
                    + "script=" + name
                    + "&username=" + username.toLowerCase().replaceAll(" ", "%20")
                    + "&runtime=" + minutes
                    + toUpdate.replaceAll(" ", "%20");
            send(link);
            if (!daily) {
                addNumber();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStats(String username, String[] names, int[] values) {
        updateStats(username, names, values, false);
    }

    public boolean scriptIsAllowed() {
        if (script.getForumUsername().equals("Krulvis")) {
            return true;
        }
        try {
            String url = "http://atscripts.com/scripts/allowed/allowed.php?script=" + this.name + "&username=" + script.getForumUsername();
            String s = getHTML(url);
            //System.out.println("Requesting: " + url);
            //System.out.println("Answer: " + (s != null ? s : "no response"));
            if (s != null && s.equals("yes")) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void checkAllowed() {
        if (!scriptIsAllowed()) {
            script.stop();
            throw new RuntimeException("Not allowed to use this script!");
        }
    }

    public void addNumber() {
        String username = script.getForumUsername();
        String rsname = script.myPlayer().getName();
        send("http://atscripts.com/scripts/accounts/account.php?"
                + "&username=" + username.toLowerCase().replaceAll(" ", "%20")
                + "&script=" + name.replaceAll(" ", "%20")
                + "&rsname=" + rsname.toLowerCase().replaceAll(" ", "%20")
        );
    }

    public static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9b5) Gecko/2008032620 Firefox/3.0b5";

    public static void send(final String url) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");

                    //add request header
                    con.setRequestProperty("User-Agent", USER_AGENT);
                    con.getResponseCode();

                    con.disconnect();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }.start();
    }

    private static final String atscripts = "atscripts.com/141.138.168.148";

    public static String getHTML(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();

            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("User-Agent", USER_AGENT);
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(30000);
            c.setReadTimeout(30000);
            c.connect();
            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String host = String.valueOf(c.getURL().getHost());
                    String urlHost = String.valueOf(u.getHost());
                    InetAddress address = InetAddress.getByName(host);
                    if (!urlHost.equals(host) || !address.toString().equals(atscripts)) {
                        System.out.println("User is faking host!");
                        System.out.println("Address correct: " + address.toString().equals(atscripts));
                        System.out.println("URL = HTTP: " + urlHost.equals(host));
                        System.out.println("URL: " + urlHost + ", HTTP: " + host);
                        return null;
                    }
                    boolean first = true;
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + (first ? "" : "\n"));
                        first = false;
                    }
                    br.close();
                    return sb.toString();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static JsonElement sendJSON(String location, String method, JsonObject object) {
        try {
            if (object.toString().length() < 750) {
                System.out.println("Body: " + object.toString());
            }
            byte[] postData = object.toString().getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            URL url = new URL(location);
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
