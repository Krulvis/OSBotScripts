package api.webapi;

import api.ATMethodProvider;
import api.event.random.Account;
import api.util.Base64Coder;
import api.webapi.actions.WebAction;
import api.webapi.actions.Relog;
import api.webapi.actions.Restart;
import api.webapi.actions.Update;
import api.wrappers.staking.calculator.SPlayer;
import com.google.gson.*;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.Script;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import static api.webapi.WebAPI.Status.ACTIVE;

/**
 * Created by Krulvis on 30-May-17.
 */
public class WebAPI extends ATMethodProvider {

    public Account currentAccount;
    public WebConnection webConnection;
    public WebAction currentAction;
    private Status status = Status.STARTING;
    private ArrayList<WebAction> actions;
    private LinkedList<WebAction> actionQueue = new LinkedList<>();
    public Relog relog;
    public Update update;
    public Restart restart;
    public static String URL = "http://api.rsbots.org/";

    public void disconnect() {
        if (getWebConnection() != null) {
            getWebConnection().isConnected = false;
            webConnection = null;
        }
    }

    public boolean isConnected() {
        Script curr = bot.getScriptExecutor().getCurrent();
        if (curr == null) {
            System.out.println("No current script running, disconnecting");
            disconnect();
        }
        return getWebConnection() != null && getWebConnection().isConnected;
    }

    public WebAPI(ATMethodProvider parent) {
        init(parent);
        actions = new ArrayList<>();
    }

    /**
     * Only call this once, will time-out script
     */
    public void connect() {
        webConnection = new WebConnection(this);
        if (webConnection.isConnected) {
            System.out.println("Getting account from Web API");
            getNewAccount();
            addStatusListener();
            addOnlineStatusThread();
        }
    }

    /**
     * Handles actions that are currently set in Queue, also logs out if Status is INACTIVE
     *
     * @return
     */
    public boolean handleWebActions() {
        if (getWebConnection() == null || !getWebConnection().isConnected) {
            return true;
        } else if (status == Status.INACTIVE && client.isLoggedIn()) {
            getWebConnection().sendJSON("bot/logout", "POST", null);
            return logoutTab.logOut();
        } else if (currentAction != null) {
            if (currentAction.perform()) {
                currentAction.sendComplete();
                currentAction = null;
            } else {
                return false;
            }
        } else if (actionQueue.size() > 0) {
            currentAction = actionQueue.remove(0);
            return false;
        }
        return true;
    }

    public WebConnection getWebConnection() {
        return webConnection;
    }

    public boolean hasAccount() {
        return currentAccount != null;
    }

    /**
     * Update the status of the script, this is used to display in the database what the bot is currently up to
     *
     * @param status (Most likely these are ATState names)
     */
    public void sendStatus(String status) {
        JsonObject object = new JsonObject();
        object.add("status", new JsonPrimitive(status.toUpperCase().replaceAll(" ", "_")));
        Script s = bot.getScriptExecutor().getCurrent();
        object.add("script", new JsonPrimitive(s != null ? s.getName().toUpperCase().replaceAll(" ", "_") : "NONE"));
        JsonElement output = getWebConnection().sendJSON("bot/activity", "POST", object);
    }

    /**
     * Sends info about in-game account like stats in RSN. To fill the DB up with more useful information.
     *
     * @param sPlayer
     */
    public void sendAccountInfo(SPlayer sPlayer) {
        JsonObject object = new JsonObject();
        object.add("attack", new JsonPrimitive(sPlayer.attack));
        object.add("strength", new JsonPrimitive(sPlayer.strength));
        object.add("defense", new JsonPrimitive(sPlayer.defense));
        object.add("hitpoints", new JsonPrimitive(sPlayer.hitpoints));
        object.add("rsn", new JsonPrimitive(sPlayer.name));
        JsonElement response = getWebConnection().sendJSON("bot", "PUT", object);
        System.out.println("Response: " + response);
    }

    /**
     * Sends a screenshot of the inventory in Base64 string (JSON) to DB
     */
    public void sendInventoryScreenshot() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Sending Inventory Screenshot...");
                    BufferedImage image = bot.getCanvas().getGameBuffer().getSubimage(552, 206, 731 - 552, 426 - 206);
                    //Save image to files
                    //ImageUtils.saveImage(image, "ss", TBot.getBot().getScriptHandler().getScript().getStorageDirectory() + File.separator);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", baos);
                    byte[] bytes = baos.toByteArray();
                    String data = new String(Base64Coder.encode(bytes));
                    JsonObject object = new JsonObject();
                    object.add("image", new JsonPrimitive(data));
                    JsonElement response = getWebConnection().sendJSON("bot/screenshot", "PUT", object);
                    baos.close();
                    System.out.println("Send Inventory Screenshot: " + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * Sends the value of the bot's inventory
     */
    public void sendInventoryValue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JsonObject object = new JsonObject();
                long cash = inventory.getAmount(995) + inventory.getAmount(13204) * 1000;
                int value = (int) cash;
                ArrayList<Integer> handled = new ArrayList<>();
                JsonArray inv_items = new JsonArray();
                for (Item i : inventory.getItems()) {
                    if (i != null && i.getId() > 0 && !handled.contains(i.getId()) && i.getId() != 995 && i.getId() != 13204) {
                        int amountInInv = (int) inventory.getAmount(i.getId());
                        JsonArray single_item = new JsonArray();
                        single_item.add(new JsonPrimitive(i.getId()));
                        single_item.add(new JsonPrimitive(amountInInv));
                        inv_items.add(single_item);
                        value += atGE.getPrice(i.getId()) * amountInInv;
                        handled.add(i.getId());
                    }
                }
                object.add("inventory_value", new JsonPrimitive(value));
                object.add("cash", new JsonPrimitive(cash));
                object.add("inventory_items", inv_items);
                getWebConnection().sendJSON("bot/inventory", "POST", object);
            }
        }, "InventoryValueThread").start();
    }

    /**
     * Add a listener to obtain Action and/or Status updates from Web API
     */
    public void addStatusListener() {
        System.out.println("Adding status action/status!");
        Script script = bot.getScriptExecutor().getCurrent();
        sendStatus("STARTING " + script.getName());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isConnected()) {
                    try {
                        Thread.sleep(5000);
                        getNewAction();
                        getNewStatus();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "StatusListenerThread").start();
    }

    public void addOnlineStatusThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isConnected()) {
                    try {
                        JsonObject obje = new JsonObject();
                        obje.add("online", new JsonPrimitive(isLoggedIn()));
                        getWebConnection().sendJSON("bot/online", "PUT", obje);
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "OnlineStatusThread").start();
    }

    public void getNewStatus() {
        JsonElement ele = getWebConnection().getJson("bot/activity");
        if (ele == JsonNull.INSTANCE) {
            return;
        }
        for (JsonElement el : ele.getAsJsonArray()) {
            JsonObject ob = el.getAsJsonObject();
            if (!ob.isJsonNull()) {
                JsonElement _status = ob.get("online");
                status = _status != null && !_status.isJsonNull() && _status.getAsString().equals("true") ? ACTIVE : Status.INACTIVE;
                System.out.println("RECEIVED Status: " + status);
            }
        }
    }

    public void getNewAction() {
        JsonElement ele = getWebConnection().getJson("bot/action");
        if (ele == JsonNull.INSTANCE) {
            return;
        }
        for (JsonElement el : ele.getAsJsonArray()) {
            JsonObject ob = el.getAsJsonObject();
            if (!ob.isJsonNull()) {
                JsonElement _action = ob.get("action");
                JsonElement _id = ob.get("id");
                if (_action != null && !_action.isJsonNull() && _id != null && !_id.isJsonNull()) {
                    String a = _action.getAsString();
                    int id = _id.getAsInt();
                    System.out.println("Received ACTION: " + a + " with ID: " + id);
                    WebAction action = getAction(a);
                    if (action != null) {
                        action.setId(id);
                        JsonElement params = ob.get("parameters");
                        if (params != null) {
                            action.setActionParameters(params.getAsJsonObject());
                        }
                        System.out.println("RECEIVED Action: " + action.getName() + ", ID: " + action.getId());
                        actionQueue.add(action);
                    }
                }
            }
        }
    }

    /**
     * Filters through list of added Web Actions to see if String matches
     *
     * @param s
     * @return
     */
    private WebAction getAction(String s) {
        for (WebAction a : actions) {
            String actionName = a.getName();
            if (actionName.equalsIgnoreCase(s)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Get an RS account from the DB to use
     * Also gets proxy information
     */
    public void getNewAccount() {
        if (getWebConnection().key != null) {
            new Thread(() -> {
                JsonElement ele = getWebConnection().getJson("bot");
                if (ele == JsonNull.INSTANCE) {
                    return;
                }
                try {
                    if (ele.isJsonArray()) {
                        for (JsonElement el : ele.getAsJsonArray()) {
                            JsonObject ob = el.getAsJsonObject();
                            if (!ob.isJsonNull()) {
                                JsonObject o = el.getAsJsonObject();
                                JsonElement _mail = o.get("login_name");
                                JsonElement _pass = o.get("password");
                                String mail = _mail != null && !_mail.isJsonNull() ? _mail.getAsString() : "";
                                String pass = _pass != null && !_pass.isJsonNull() ? _pass.getAsString() : "";
                                System.out.println("Account found: LoginMail: " + mail + ", Pass: " + pass);
                                setCurrentAccount(new Account(mail, pass));
                            }
                        }
                    } else {
                        JsonObject o = ele.getAsJsonObject();
                        JsonElement e = o.get("status");
                        System.out.println("Response for 'bot/': " + (e != null ? e.getAsString() : "No response found"));
                        log("Requesting 'bot/': " + (e != null ? e.getAsString() : "No response found"));
                    }
                } catch (IllegalStateException e) {
                    System.out.println("ERROR RESPONSE: " + ele.toString());
                    e.printStackTrace();
                }

            }, "AccountInfoThread").start();
        }
    }

    /**
     * Set account banned in DB
     */
    public void updateBanned() {
        getWebConnection().sendJSON("bot/ban", "PUT", null);
        System.out.println("Send banned message");
    }

    public enum Status {
        BANNED, ACTIVE, INACTIVE, IDADDED, STARTING
    }

    public Status getStatus() {
        return this.status;
    }

    /**
     * @return true if status of bot is active and should thus login / do script
     */
    public boolean isActive() {
        return status.equals(ACTIVE);
    }

    public void setCurrentAccount(Account account) {
        this.currentAccount = account;
    }

    public Account getCurrentAccount() {
        return this.currentAccount;
    }

    /**
     * Adds extra action to the web, this can be script specific
     *
     * @param webAction+
     */
    public void addAction(WebAction webAction) {
        actions.add(webAction);
    }

}
