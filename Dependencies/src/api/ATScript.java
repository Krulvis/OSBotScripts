package api;

import api.event.listener.EventHandler;
import api.event.listener.inventory.InventoryHandler;
import api.event.listener.inventory.InventoryListener;
import api.event.random.RandomHandler;
import api.util.ATPainter;
import api.util.Random;
import api.util.Timer;
import api.util.Updater;
import api.util.cache.Cache;
import api.util.gui.GUIWrapper;
import api.webapi.actions.Relog;
import api.webapi.actions.Restart;
import api.webapi.actions.Update;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
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
    private boolean isPrivateVersion = false, useWebAPI = false;
    private GUIWrapper<? extends ATScript> guiWrapper;
    private RandomHandler randomHandler;
    private ArrayList<EventHandler> eventHandlers = new ArrayList<>();

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
            super.init(this);
            try {
                Class<? extends ATPainter> painterClass = getPainterClass();
                this.painter = painterClass == null ? null : painterClass.getConstructor(this.getClass()).newInstance(this);
                Class<? extends GUIWrapper> guiClass = getGUI();
                this.guiWrapper = guiClass == null ? null : guiClass.getConstructor(this.getClass()).newInstance(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.currentThread().setName("ScriptLoopThread");
            this.initialize(states);
            if (this.guiWrapper == null) {
                isScriptRunning.set(true);
            }
            if (this instanceof InventoryListener) {
                System.out.println("Script has InventoryListener");
                eventHandlers.add(new InventoryHandler(this));
            }
            startEventHandlers();
            if (this.useWebAPI) {
                randomHandler = new RandomHandler(this);
                webAPI.addAction(webAPI.relog = new Relog(this));
                webAPI.addAction(webAPI.restart = new Restart(this));
                webAPI.addAction(webAPI.update = new Update(this));
                //Done last
                webAPI.connect();
            }
            isScriptInitialized = true;
        }
        return Random.medSleep();
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
                return 500;
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
        return Random.smallSleep();
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
                painter.onRepaint(g2);
            }
        }
        ATPainter.drawString(g2, "V: " + getVersion(), 10, 50);
    }

    public void setPrivateVersion() {
        isPrivateVersion = true;
    }

    public RandomHandler getRandomHandler() {
        return this.randomHandler;
    }

    @Override
    public void onExit() {
        webAPI.disconnect();
    }

    public void useWebAPI() {
        this.useWebAPI = true;
    }

    public void startEventHandlers() {
        if (eventHandlers.size() > 0) {// Only start when there are actual handlers added
            System.out.println("Starting Event Handler Thread!");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (bot.getScriptExecutor().getCurrent() != null) {
                        if (isLoggedIn()) {
                            for (EventHandler eventHandler : eventHandlers) {
                                eventHandler.handle();
                            }
                        }
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "Event Handler Thread").start();
        }
    }

    public String getSettingsFolder() {
        return System.getProperty("user.home") + File.separator + "OSBot" + File.separator + "data" + File.separator + getName() + File.separator;
    }

}
