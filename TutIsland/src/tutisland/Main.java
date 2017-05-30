package tutisland;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import api.util.accountcreation.HttpConnection;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(author = "Rudie", info = "Completes tutorial island", logo = "", name = "Tut", version = 0.1)
public class Main extends Script {

	private Area area = new Area(3191, 3250, 3240, 3190);
	private StageHandler stageHandler;
	private static String username,password;
	private int responseCode = 2;

	public static void main(String[] args){
		//		String[] account = getNewAccount().split(":");
		//		username = account[0];
		//		password = account[1];
		//		System.out.println(account);
		//		finishAcc(username);
		finishAcc("awo+000785@outlook.com");
	}

	private enum State {NEW_ACC, TUT_ISLAND, RELOG};

	@Override
	public void onStart(){
		stageHandler = new StageHandler(this);
	}

	public State getState(){
//		if(responseCode != 2)
//			return State.RELOG;
//		else if(!client.isLoggedIn())
//			return State.NEW_ACC;
		return State.TUT_ISLAND;
	}


	@Override
	public void onResponseCode(int arg0) throws InterruptedException {
		responseCode = arg0;
	}

	@Override
	public int onLoop() throws InterruptedException {

		if (MethodProvider.random(0, 60) == 0) {
			turnCamera();
		}
		if(client.isLoggedIn()){
			responseCode = 2;
		}
		switch(getState()){
		case RELOG:
			log("Trying to log in again: " + responseCode);
			if(!client.isLoggedIn()){
				LoginEvent loginEvent = new LoginEvent(username,password);
				getBot().addLoginListener(loginEvent);
				execute(loginEvent);
			}
			break;
		case NEW_ACC:
			sleep(5000);
			String lastuser = username;
			if(lastuser != null)
				finishAcc(lastuser);

			String[] account = getAccount().split(":");
			username = account[0];
			password = account[1];
			LoginEvent loginEvent = new LoginEvent(username,password);
			getBot().addLoginListener(loginEvent);
			execute(loginEvent);

			break;
		case TUT_ISLAND:
			stageHandler.handleStage(getConfigs().get(281));
			break;
		}


		return 300;
	}

	public static void finishAcc(String user){

		HashMap<String, String> params = new HashMap<String, String>() {{
			put("user",user);
		}};

		HttpConnection.executePost("finishTutAccount",params);
	}

	private static String getAccount() {
		return HttpConnection.sendGet("getTutAccount");
	}


	public void turnCamera() {
		int camAlt = camera.getZ();
		char LR = KeyEvent.VK_LEFT;
		char UD;
		if (camAlt > -1600) {
			UD = KeyEvent.VK_UP;
		} else if (camAlt < -2215 || MethodProvider.random(0, 2) == 0) {
			UD = KeyEvent.VK_DOWN;
		} else {
			UD = KeyEvent.VK_UP;
		}
		if (MethodProvider.random(0, 2) == 0) {
			LR = KeyEvent.VK_RIGHT;
		}
		keyboard.pressKey(LR);
		try {
			Thread.sleep(MethodProvider.random(50, 400));
		} catch (Exception ignored) {
		}
		keyboard.pressKey(UD);
		try {
			Thread.sleep(MethodProvider.random(300, 700));
		} catch (Exception ignored) {
		}
		keyboard.releaseKey(UD);
		try {
			Thread.sleep(MethodProvider.random(100, 400));
		} catch (Exception ignored) {
		}
		keyboard.releaseKey(LR);
	}





}
