package api.util.accountcreation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Krulvis on 04-May-17.
 */
public class CheckAccount {

    private static String URL = "https://secure.runescape.com/m=offence-appeal/s=";
    private static String S;

    public static void main(String... args) {
        String username = args[0];
        String password = args[1];

        HashMap<String, String> params = new HashMap<String, String>() {{
            put("username", username);
            put("password", password);
            put("mod", "www");
            put("ssl", "1");
            put("dest", "account_history.ws?mod=emailNew&validate=true");

        }};
        CookieManager cookieManager = new CookieManager();
        ArrayList<String> cookies = getCookies("https://secure.runescape.com/m=weblogin/login.ws", params, cookieManager);
        if (cookies != null) {
            for (String key : cookies) {
                System.out.println("COOKIE: " + key);
            }
        }
        if (URL != null && S != null) {
            String body = response(URL + S + "/account_history.ws", cookies, cookieManager);
            if(body != null && body.contains("Macroing Major (ban)")){
                System.out.println("ACCOUNT IS BANNED");
            }
        }
    }

    public static ArrayList<String> getCookies(String targetURL, HashMap<String, String> params, CookieManager cookieManager) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Keep-Alive", "240");
            connection.setRequestProperty("Connection", " keep-alive");

            String urlParameters = getParams(params);

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            //Get cookies
            String headerName;
            ArrayList<String> cookies = new ArrayList<>();
            for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
                if (headerName.contains("Cookie")) {
                    String cookie = connection.getHeaderField(i);
                    System.out.println("REAL COOKIE: " + cookie);
                    String name = cookie.substring(0, cookie.indexOf('='));
                    String value = cookie.substring(cookie.indexOf('=') + 1, cookie.indexOf(';'));
                    //System.out.println(name + "=" + value);
                    //cookies.put(name, value);
                    cookies.add(cookie);
                }
            }
            //Raw body
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                if (line.contains("/s=")) {
                    String code = line.substring(line.indexOf("/s=") + 3);
                    System.out.println("Found s: " + (S = code.substring(0, code.indexOf("/"))));
                    break;
                }
                response.append('\n');
            }
            //System.out.println(response);
            rd.close();
            return cookies;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String response(String target, ArrayList<String> cookies, CookieManager cookieManager) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(target);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Keep-Alive", "240");
            connection.setRequestProperty("Connection", " keep-alive");
            connection.setUseCaches(true);
            connection.setDoOutput(true);
            for (String cookie : cookies) {
                connection.setRequestProperty("Set-Cookie", cookie);
            }
            HashMap<String, String> params = new HashMap<String, String>() {{
                put("mod", "www");
                put("ssl", "1");
                put("dest", "account_history.ws?mod");
                put("validate", "true");
            }};

            String urlParameters = getParams(params);
            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();


            InputStream is = connection.getInputStream();
            //Get cookies

            //Raw body
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getParams(HashMap<String, String> params) {
        String urlParameters = "";
        boolean first = true;
        for (String key : params.keySet()) {
            urlParameters += (first ? "" : "&") + key + "=" + params.get(key);
            first = false;
        }
        return urlParameters;
    }
}
