package api;

import api.event.random.RandomHandler;
import api.util.ATPainter;
import api.util.Random;
import api.util.Timer;
import api.util.Updater;
import api.util.gui.GUIWrapper;
import api.webapi.WebAPI;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Krulvis on 29-May-17.
 */
public abstract class ATScript extends ATMethodProvider {

	public AtomicBoolean isScriptRunning = new AtomicBoolean(false);
	private boolean isScriptInitialized = false;
	public LinkedList<ATState> states = new LinkedList<>();
	public ATState currentState;
	private Updater updater;
	public Timer timer;
	private boolean isPrivateVersion = false;
	private GUIWrapper<? extends ATScript> guiWrapper;
	private RandomHandler randomHandler;

	@Override
	public int onLoop() throws InterruptedException {
		if (isScriptRunning.get()) {
			return loop();
		} else if (!isScriptInitialized) {
			updater = new Updater(this);
			timer = new Timer();
			if (isPrivateVersion) {
				updater.checkAllowed();
			}
			try {
				Class<? extends ATPainter> painterClass = getPainterClass();
				this.painter = painterClass == null ? null : painterClass.getConstructor(this.getClass()).newInstance(this);
				Class<? extends GUIWrapper> guiClass = getGUI();
				this.guiWrapper = guiClass == null ? null : guiClass.getConstructor(this.getClass()).newInstance(this);

			} catch (Exception e) {
				e.printStackTrace();
			}

			super.init(this);
			isScriptInitialized = true;
			this.initialize(states);
			if (this.guiWrapper == null) {
				isScriptRunning.set(true);
			}
		}
		return 100;
	}

	/**
	 * Main perform method. By default its performing states
	 * logic, if you @Override it, no state'mp logic will be performed
	 * or you need to add own state checker and performer.
	 *
	 * @return int - time in ms to sleep.
	 * @throws InterruptedException
	 */
	public int loop() throws InterruptedException {
		try {
			int sleep;
			if (randomHandler != null && (sleep = randomHandler.onLoop()) > 0) {
				return sleep;
			} else if (states.isEmpty()) {
				return 100;
			} else {
				update();
				currentState = getState();
				if (currentState != null) {
					return currentState.perform();
				}
			}
		} catch (InterruptedException e) {
			System.err.println(" --------- SCRIPT INTERRUPTED --------- ");
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println(" --------- ERROR ON SCRIPT --------- ");
			e.printStackTrace();
		}
		return random(50, 150);
	}

	public ATState<? extends ATScript> getState() {
		for (ATState<?> state : states) {
			if (state.validate()) {
				return state;
			}
		}
		return null;
	}

	/**
	 * Use this to update Item gatherings & Profit gains
	 * <p>
	 * Add updater null check and updater.shouldUpdate() inside update()
	 */
	public abstract void update();

	/**
	 * Initializes this context.
	 *
	 * @param statesToAdd - states we want to add.
	 */
	protected abstract void initialize(LinkedList<ATState> statesToAdd);

	/**
	 * Sets up context'mp painter.
	 * Do not set it visible. This method is only called once.
	 * Return null if no painter added
	 *
	 * @return ATPainter the painter we would like to use.
	 */
	protected abstract Class<? extends ATPainter> getPainterClass();

	/**
	 * Set up GUIWrapper, script will only start after started is set to true in GUIWrapper
	 *
	 * @return
	 */
	protected abstract Class<? extends GUIWrapper> getGUI();

	/**
	 * Basic painter of every ATScript,
	 * checks for active randomhandler, painter class and current state of script
	 *
	 * @param g2
	 */
	@Override
	public void onPaint(Graphics2D g2) {
		if (randomHandler != null && randomHandler.isActive()) {
			randomHandler.onPaint(g2);
		} else {
			if (currentState != null) {
				currentState.onPaint(g2);
			}
			if (painter != null) {
				painter.paint(g2);
			}
		}
	}

	public void setPrivateVersion() {
		isPrivateVersion = true;
	}


	@Override
	public void onExit() {
		webAPI.disconnect();
	}

	public void useWebAPI() {
		webAPI = new WebAPI(this);
		webAPI.connect();
		randomHandler = new RandomHandler(this);
	}

}
