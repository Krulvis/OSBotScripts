package api.util.accountcreation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;


public class HttpConnection {

    private final static String USER_AGENT = "Mozilla/5.0";
    private final static String BASE = "parent.util.accountcreation.http://127.0.0.1:5000/";
    public static Proxy proxy = null;

    // HTTP GET request
    public static String sendGet(String endPoint) {

        try {
            String url = BASE + endPoint;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();


            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                System.out.println("inputLine: " + inputLine);
            }
            in.close();


            return response.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    // HTTP POST request
    public static String executePost(String targetURL, HashMap<String, String> params) {
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

            String urlParameters = "";
            boolean first = true;
            for (String key : params.keySet()) {
                urlParameters += (first ? "" : "&") + key + "=" + URLEncoder.encode(params.get(key), "UTF-8");
                first = false;
            }
            System.out.println("Params: ");
            System.out.println(urlParameters);


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
            //Raw body
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
            rd.close();
            return "Response: " + response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR OCCURED";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void setProxy(){
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.1", 8080));
        //conn = new URL(urlString).openConnection(proxy);

        Authenticator authenticator = new Authenticator() {

            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication("user",
                        "password".toCharArray()));
            }
        };
        Authenticator.setDefault(authenticator);
    }

}