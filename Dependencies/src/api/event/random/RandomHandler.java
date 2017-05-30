package api.event.random;

import api.ATMethodProvider;
import api.ATScript;
import api.ATState;
import api.util.ATPainter;
import api.util.gui.GUIWrapper;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by Krulvis on 30-May-17.
 */
public class RandomHandler extends ATScript {

	private boolean active = false;

	public RandomHandler(ATMethodProvider parent) {
		init(parent);
		initialize(states);
	}

	@Override
	public int onLoop() throws InterruptedException {
		int sleep = -1;
		currentState = getState();
		if (currentState != null) {
			active = true;
			sleep = currentState.perform();
		} else {
			active = false;
		}
		return sleep;
	}


	@Override
	public void update() {

	}

	@Override
	public void onPaint(Graphics2D g) {
		ATState random = currentState;
		if (random != null) {
			Color c = g.getColor();
			Font f = g.getFont();
			g.setFont(ATPainter.BIG20);
			g.setColor(Color.CYAN);
			g.drawString("Solving Random: " + random.getName(), 200, 200);
			g.setColor(c);
			g.setFont(f);
		}
	}

	public Account getAccount() {
		return webAPI.getCurrentAccount();
	}

	@Override
	protected void initialize(LinkedList<ATState> statesToAdd) {
		LoginHandler loginHandler = new LoginHandler(this);
		bot.addLoginListener(loginHandler);
		statesToAdd.add(loginHandler);
	}

	@Override
	protected Class<? extends ATPainter> getPainterClass() {
		return null;
	}

	@Override
	protected Class<? extends GUIWrapper> getGUI() {
		return null;
	}

	public boolean isActive() {
		return active;
	}
}
