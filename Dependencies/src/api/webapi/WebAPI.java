package api.webapi;

import api.ATMethodProvider;
import api.event.random.Account;
import api.util.Base64Coder;
import api.webapi.actions.Action;
import api.webapi.actions.Relog;
import api.webapi.actions.Restart;
import api.webapi.actions.Update;
import api.wrappers.staking.calculator.SPlayer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.osbot.rs07.script.Script;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Krulvis on 30-May-17.
 */
public class WebAPI extends ATMethodProvider {

	public Account currentAccount;
	public WebConnection webConnection;
	public Action currentAction;
	public Status status = Status.STARTING;
	private ArrayList<Action> actions;
	public Relog relog;
	public Update update;
	public Restart restart;
	public static String URL = "http://beta.api.rsbots.org/";

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
		actions.add(relog = new Relog(this));
		actions.add(restart = new Restart(this));
		actions.add(update = new Update(this));
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
		}
	}

	public boolean handleWebAPI() {
		if (getWebConnection() == null || !getWebConnection().isConnected) {
			return true;
		} else if (status == Status.INACTIVE && client.isLoggedIn()) {
			getWebConnection().sendJSON("bot/logout", "POST", null);
			return logoutTab.logOut();
		} else if (currentAction != null) {
			int response = currentAction.loop();
			if (response == -1) {
				currentAction = null;
			} else {
				return false;
			}
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
		JsonElement output = getWebConnection().sendJSON("bot/status", "POST", object);
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
		object.add("rsn", new JsonPrimitive(myPlayer().getName()));
		JsonElement response = getWebConnection().sendJSON("bot/info", "PUT", object);
		System.out.println("Response: " + response);
	}

	/**
	 * Sends a screenshot of the inventory in Base64 string (JSON) to DB
	 */
	public void sendInventoryScreenshot() {
		try {
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
						getAction();
						getActivity();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, "StatusListenerThread").start();
	}

	public void getActivity() {
		JsonElement ele = getWebConnection().getJson("bot/activity");
		if (ele == JsonNull.INSTANCE) {
			return;
		}
		for (JsonElement el : ele.getAsJsonArray()) {
			JsonObject ob = el.getAsJsonObject();
			if (!ob.isJsonNull()) {
				JsonElement _status = ob.get("online");
				status = _status != null && !_status.isJsonNull() && _status.getAsString().equals("true") ? Status.ACTIVE : Status.INACTIVE;
				System.out.println("RECEIVED Status: " + status);
			}
		}
	}

	public void getAction() {
		JsonElement ele = getWebConnection().getJson("bot/action");
		if (ele == JsonNull.INSTANCE) {
			return;
		}
		for (JsonElement el : ele.getAsJsonArray()) {
			JsonObject ob = el.getAsJsonObject();
			if (!ob.isJsonNull()) {
				JsonElement _action = ob.get("action");
				if (_action != null && !_action.isJsonNull()) {
					String a = _action.getAsString();
					System.out.println("Received ACTION: " + a);
					currentAction = getAction(a);
					if (currentAction != null) {
						System.out.println("RECEIVED Action: " + currentAction.getClass().getName());
					}
				}
			}
		}
	}

	/**
	 * Get an RS account from the DB to use
	 * Also gets proxy information
	 */
	public void getNewAccount() {
		if (getWebConnection().key != null) {
			new Thread(() -> {
				JsonElement ele = getWebConnection().getJson("bot/info");
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
						System.out.println("Response for 'bot/info': " + (e != null ? e.getAsString() : "No response found"));
						log("Requesting 'bot/info': " + (e != null ? e.getAsString() : "No response found"));
					}
				} catch (IllegalStateException e) {
					System.out.println("ERROR RESPONSE: " + ele.toString());
					e.printStackTrace();
				}

			}, "AccountInfoThread").start();
		}
	}

	private Action getAction(String s) {
		for (Action a : actions) {
			String classname = a.getClass().getName().substring(a.getClass().getName().lastIndexOf(".") + 1);
			boolean thisone = classname.equalsIgnoreCase(s);
			System.out.println("Class: " + classname + ", equals: " + s + ": " + thisone);
			if (thisone) {
				return a;
			}
		}
		return null;
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

	public void setCurrentAccount(Account account) {
		this.currentAccount = account;
	}

	public Account getCurrentAccount() {
		return this.currentAccount;
	}

}
