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
    private LoginHandler loginHandler;

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
    public void onPaint(Graphics2D g2) {
        ATState random = this.currentState;
        if (random != null) {
            Color c = g2.getColor();
            Font f = g2.getFont();
            g2.setFont(ATPainter.BIG20);
            g2.setColor(Color.CYAN);
            g2.drawString("Solving Random: " + random.getName(), 200, 200);
            g2.setColor(c);
            g2.setFont(f);
        } else {
            g2.drawString("No current Random", 200, 200);
        }
    }

    public Account getAccount() {
        return webAPI.getCurrentAccount();
    }

    @Override
    protected void initialize(LinkedList<ATState> statesToAdd) {
        this.loginHandler = new LoginHandler(this);
        bot.addLoginListener(loginHandler);
        statesToAdd.add(loginHandler);
    }

    public LoginHandler getLoginHandler() {
        return this.loginHandler;
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
