package api;

import api.event.random.RandomHandler;
import api.util.ATPainter;
import api.util.ImageUtils;
import api.util.Random;
import api.util.Timer;
import api.util.data.ATFood;
import api.web.Flag;
import api.webapi.WebAPI;
import api.wrappers.*;
import api.wrappers.friendslist.ATFriendsList;
import api.wrappers.grandexchange.GrandExchange;
import api.wrappers.grandexchange.Prices;
import api.wrappers.staking.ATStake;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.MagicSpell;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.api.util.GraphicUtilities;
import org.osbot.rs07.api.util.LocalPathFinder;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.input.mouse.EntityDestination;
import org.osbot.rs07.input.mouse.MouseDestination;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * Created by Krulvis on 29-May-17.
 */
public class ATMethodProvider extends Script {

    public ATPainter painter;
    public LocalPathFinder localPathFinder;
    public ATInteract interact;
    public ATWorldHopper worldHopper;
    public ATFriendsList friends;
    public ATCombat atCombat;
    public ATFood food;
    public ATChat chat;
    public ATStake stake;
    public ATShop shop;
    public ATEmotes emotes;
    public GrandExchange atGE;
    public Prices prices;
    public ImageUtils imageUtils;
    public WebAPI webAPI;

    protected void init(ATMethodProvider parent) {
        if (!(this instanceof ATScript) || (this instanceof RandomHandler)) {
            //System.out.println("Initializing new " + (this instanceof ATState ? "ATState" : "ATWrapper") + " - " + this.toString());
            //Standard OSBot wrappers
            this.client = parent.client;
            this.skills = parent.skills;
            this.inventory = parent.inventory;
            this.equipment = parent.equipment;
            this.bot = parent.bot;
            this.bank = parent.bank;
            this.camera = parent.camera;
            this.combat = parent.combat;
            this.dialogues = parent.dialogues;
            this.widgets = parent.widgets;
            this.objects = parent.objects;
            this.groundItems = parent.groundItems;
            this.players = parent.players;
            this.npcs = parent.npcs;
            this.configs = parent.configs;
            this.menu = parent.menu;
            this.mouse = parent.mouse;
            this.colorPicker = parent.colorPicker;
            this.keyboard = parent.keyboard;
            this.settings = parent.settings;
            this.map = parent.map;
            this.logger = parent.logger;
            this.tabs = parent.tabs;
            this.store = parent.store;
            this.walking = parent.walking;
            this.logoutTab = parent.logoutTab;
            this.worlds = parent.worlds;
            this.magic = parent.magic;
            this.grandExchange = parent.grandExchange; // this thing is shit

            //needs to be initialized in ATScript
            this.localPathFinder = parent.localPathFinder;
            //Customs
            this.painter = parent.painter;
            this.interact = parent.interact;
            this.worldHopper = parent.worldHopper;
            this.atCombat = parent.atCombat;
            this.food = parent.food;
            this.emotes = parent.emotes;
            this.stake = parent.stake;
            this.atGE = parent.atGE;
            this.friends = parent.friends;
            this.chat = parent.chat;
            this.imageUtils = parent.imageUtils;
            this.webAPI = parent.webAPI;
            this.shop = parent.shop;
            this.prices = parent.prices;
        } else {
            //Highest level, therefore initialize customs
            //Order matters, everything that is below something will be null there
            this.localPathFinder = new LocalPathFinder(getBot());
            this.interact = new ATInteract(this);
            this.imageUtils = new ImageUtils(this);
            this.prices = new Prices(this);
            this.food = new ATFood(this);
            this.atCombat = new ATCombat(this);
            this.emotes = new ATEmotes(this);
            this.stake = new ATStake(this);
            this.chat = new ATChat(this);
            this.webAPI = new WebAPI(this);
            this.shop = new ATShop(this);
            this.friends = new ATFriendsList(this);
            this.atGE = new GrandExchange(this);
            this.worldHopper = new ATWorldHopper(this);
        }
    }

    public String getForumUsername() {
        return client.getUsername();
    }

    public void sleep(long min, long max) {
        long mean = (min + max) / 2;
        try {
            sleep((long) Random.gaussian(mean, min));
        } catch (InterruptedException e) {
            //UGH
        }
    }

    public boolean waitFor(Condition c, int time) {
        return waitFor(time, c);
    }

    public boolean waitFor(int time, Condition c) {
        for (int i = 0; (i < time / 50) && !c.evaluate(); i++) {
            try {
                sleep(50);
            } catch (InterruptedException e) {
                //WHY
            }
        }
        return c.evaluate();
    }

    public int distance(Entity e) {
        return e != null ? distance(e.getPosition()) : Integer.MAX_VALUE;
    }

    public int distance(Position p) {
        return p.distance(myPosition());
    }

    public boolean isAnimating() {
        return myPlayer().isAnimating();
    }

    public int currentHealth() {
        return myPlayer().getHealthPercent();
    }

    public boolean isMoving() {
        final Position dest = map.getDestination();
        return dest != null && dest.distance(myPosition()) > 0;
    }

    public boolean isLoggedIn() {
        return client.isLoggedIn();
    }

    public static int getDegree(int distance) {
        int degree = distance * -5 + 95;
        return degree < 40 ? 40 : degree > 95 ? 95 : degree;
    }


    public boolean clickWidget(RS2Widget widget) {
        Rectangle rect = widget.getBounds();
        return mouse.click(new RectangleDestination(bot, rect));
    }

    public boolean deselectItem() {
        if (inventory.isItemSelected() && inventory.deselectItem()) {
            waitFor(Random.nextGaussian(1000, 2000, 500), new Condition() {
                @Override
                public boolean evaluate() {
                    return !inventory.isItemSelected();
                }
            });
        }
        return !inventory.isItemSelected();
    }

    public boolean deselectSpell() {
        if (magic.isSpellSelected() && magic.deselectSpell()) {
            waitFor(Random.nextGaussian(1000, 2000, 500), new Condition() {
                @Override
                public boolean evaluate() {
                    return !magic.isSpellSelected();
                }
            });
        }
        return !magic.isSpellSelected();
    }

    public boolean deselectEverything() {
        return deselectItem() && deselectSpell();
    }

    public boolean spaceBar() {
        return keyboard.typeString(" ", false);
    }

    public void backSpace() {
        Timer backSpaceTimer = new Timer(Random.nextGaussian(3000, 5000, 1000));
        keyboard.pressKey((char) KeyEvent.VK_BACK_SPACE);
        while (!backSpaceTimer.isFinished()) {
            try {
                sleep(Random.nextGaussian(100, 250, 50));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            keyboard.pressKey((char) KeyEvent.VK_BACK_SPACE);
        }
        keyboard.releaseKey((char) KeyEvent.VK_BACK_SPACE);
    }

    public boolean isOutOfAny(final int... ids) {
        for (final int i : ids) {
            if (!bank.contains(i)) {
                sleep(500, 750);
                if (bank.isOpen() && !bank.contains(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean waitingForInput() {
        final RS2Widget ba = getWidgetChild(162, new Filter<RS2Widget>() {
            @Override
            public boolean match(RS2Widget wc) {
                if (wc != null && wc.getMessage() != null) {
                    String txt = wc.getMessage();
                    return (txt.contains("Enter message to") || txt.contains("Enter amount:") || txt.contains("Set a price for each item:")
                            || txt.contains("Enter name:") || txt.contains("How many do you wish to buy")
                            || txt.contains("Enter the player name"));
                }
                return false;
            }
        });
        if (ba != null && ba.isVisible()) {
            RS2Widget w = widgets.get(ba.getRootId(), ba.getSecondLevelId() + 1);
            return validateWidget(w) && w.getMessage().endsWith("*");
        }
        return false;
    }

    public boolean handleFillInX(int count) {
        return handleFillInX(getAmountString(count));
    }

    public boolean handleFillInX(String s) {
        keyboard.typeString("" + s, true);
        return true;
    }

    public String getAmountString(int amount) {
        String s = "" + amount;
        if (amount % 1000000 == 0) {
            s = (amount / 1000000) + "m";
        } else if (amount % 1000 == 0) {
            s = (amount / 1000) + "k";
        }
        return s;
    }

    private static final int[] NON_CHAT_WIDGETS = new int[]{12, 15, 90, 137, 149, 182, 216, 218, 239, 261, 271, 274, 320, 387, 429, 432, 548, 549, 589, 593};
    private static final Rectangle CHAT_RECT = new Rectangle(8, 345, 505, 131);

    public boolean canContinue() {
        final java.util.List<RS2Widget> wdgts = widgets.getAll();
        SEARCH:
        for (RS2Widget w : wdgts) {
            if (w == null) {
                continue SEARCH;
            }
            for (int nonChat : NON_CHAT_WIDGETS) {
                if (nonChat == w.getRootId()) {
                    continue SEARCH;
                }
            }
            final RS2Widget continueWidget = widgets.singleFilter(w.getRootId(), new Filter<RS2Widget>() {

                @Override
                public boolean match(final RS2Widget v) {
                    final String text = v.getMessage();
                    if (!text.equalsIgnoreCase("Click here to continue") && !text.equalsIgnoreCase("Please wait...")) {
                        return false;
                    }
                    if (!v.isVisible()) {
                        return false;
                    }
                    return CHAT_RECT.contains(v.getBounds());
                }

            });
            return continueWidget != null;
        }
        return false;
    }

    public boolean validateWidget(int parent) {
        return validateWidget(parent, 0);
    }

    public boolean validateWidget(int parent, int child) {
        return validateWidget(widgets.get(parent, child));
    }

    public boolean validateWidget(RS2Widget widget) {
        return widget != null && widget.isVisible();
    }

    public boolean scrollTo(final RS2Widget target, final RS2Widget inventory) {
        int minY = inventory.getAbsY();
        int maxY = minY + inventory.getHeight();

        int currMinY = target.getAbsY();
        int currMaxY = currMinY + target.getHeight();

        if (currMaxY > maxY || currMinY < minY) {
            Timer t = new Timer(500);
            while (!t.isFinished() && (currMaxY > maxY || currMinY < minY)) {
                if (!inventory.getBounds().contains(mouse.getPosition())) {
                    mouse.move(new RectangleDestination(bot, inventory.getBounds()));
                }
                if (currMinY < minY) {
                    mouse.scrollUp();
                } else {
                    mouse.scrollDown();
                }
                sleep(30, 50);
                currMinY = target.getScrollY();
                currMaxY = currMinY + target.getHeight();
            }
        }

        return (currMaxY <= maxY && currMinY >= minY);
    }

    public RS2Widget getWidgetChild(int parent, Filter<RS2Widget> filter) {
        return widgets.singleFilter(parent, filter);
    }

    public RS2Widget getWidgetWithText(final int parent, final String text) {
        return getWidgetChild(parent, new Filter<RS2Widget>() {
            @Override
            public boolean match(RS2Widget widget) {
                return widget != null && widget.getMessage().toLowerCase().contains(text.toLowerCase());
            }
        });
    }

    public RS2Widget getWidgetWithText(final RS2Widget parent, final String text) {
        return getWidgetChild(parent, new Filter<RS2Widget>() {
            @Override
            public boolean match(RS2Widget widget) {
                return widget != null && widget.getMessage().toLowerCase().contains(text.toLowerCase());
            }
        });
    }

    public RS2Widget getWidgetWithAction(final RS2Widget parent, final String action) {
        return getWidgetChild(parent, new Filter<RS2Widget>() {
            @Override
            public boolean match(RS2Widget widget) {
                return widget != null && widget.getInteractActions() != null && Arrays.asList(widget.getInteractActions()).contains(action);
            }
        });
    }

    public RS2Widget getWidgetChild(int parent, int second, Filter<RS2Widget> filter) {
        return getWidgetChild(widgets.get(parent, second), filter);
    }

    public RS2Widget getWidgetChild(RS2Widget w, Filter<RS2Widget> filter) {
        if (w == null || w.getChildWidgets() == null) {
            return null;
        }
        for (RS2Widget child : w.getChildWidgets()) {
            if (child != null && filter.match(child)) {
                return child;
            }
        }
        return null;
    }

    public boolean isOutOfAll(final int... ids) {
        boolean outOfAll = true;
        for (int id : ids) {
            if (!isOutOfAny(id)) {
                outOfAll = false;
            }
        }
        return outOfAll;
    }

    public boolean canCast(MagicSpell spell) {
        try {
            return magic.canCast(spell);
        } catch (InterruptedException e) {
            System.out.println("Failed checking if spell can be cast or some shit");
        }
        return false;
    }

    public boolean hasLevelForSpell(MagicSpell spell) {
        return skills.getDynamic(Skill.MAGIC) >= spell.getRequiredLevel();
    }

    public boolean hasSpellSelected(MagicSpell spell) {
        if (!magic.isSpellSelected()) {
            return false;
        }
        final String selected = magic.getSelectedSpellName();
        final String comparable = spell.toString();
        return selected.toLowerCase().equals(comparable.replaceAll("_", " ").toLowerCase());
    }

    //@TODO FIX DIS BS
    public boolean interact(final Item item, String action) {
        if (item != null) {
            return item.interact(action);
        }
        return false;
    }

    public boolean interact(final Entity e, String action) {
        if (e != null) {
            return interact(new EntityDestination(bot, e), e.getPosition(), action, e.getName());
        } else {
            return false;
        }
    }

    public boolean interact(final Position p, String action) {
        if (p != null) {
            return interact(new RectangleDestination(bot, p.getPolygon(bot).getBounds()), p, action, null);
        } else {
            return false;
        }
    }

    public boolean interact(final MouseDestination e, final Position p, final String action, final String noun) {
        final long startTime = System.currentTimeMillis();
        if (e == null) {
            return false;
        } else {
            Event interaction = new Event() {
                @Override
                public int execute() throws InterruptedException {
                    if (System.currentTimeMillis() - startTime > 1000) {
                        this.setFailed();
                    }
                    if (!e.isVisible()) {
                        final Position pos = p;
                        if (pos.distance(myPosition()) >= 8) {
                            if (walking.walk(closestEmptyTile(pos))) {
                                new ConditionalSleep(1000 + 500 * pos.distance(myPosition())) {
                                    @Override
                                    public boolean condition() throws InterruptedException {
                                        return pos.distance(myPosition()) >= 8;
                                    }
                                }.sleep();
                            }
                        }
                        camera.toPosition(p);
                        new ConditionalSleep(1500) {
                            @Override
                            public boolean condition() throws InterruptedException {
                                return e.isVisible();
                            }
                        }.sleep();
                    }
                    if (e.isVisible()) {
                        System.out.println("Started click");
                        if (interact.interact(e, action, noun, false)) {
                            System.out.println("Succesful click");
                            this.setFinished();
                        } else {
                            System.out.println("Failed clcik");
                        }
                    }
                    return gRandom(50, 25);
                }
            };
            return bot.getEventExecutor().execute(interaction).hasFinished();
        }
    }

    public boolean walkPath(Position p) {
        return walkPath(p, true);
    }

    public boolean walkPath(Position p, boolean checkRun) {
        int distance = 0;
        if (p == null || (distance = distance(p)) == 0) {
            return false;
        } else {
            checkRun();
            if (p.isVisible(bot) && Random.gaussian(10, 10) > 5) {
                return interact(p, "Walk here");
            } else if (p.isOnMiniMap(bot)) {
                Rectangle bounds = getMiniMapRect(p);
                return interact.interact(new RectangleDestination(bot, bounds));
            } else {
                return walking.webWalk(p);
            }
        }
    }

    public Rectangle getMiniMapRect(Position p) {
        if (p == null || !p.isOnMiniMap(bot)) {
            return null;
        }
        short[] coords = GraphicUtilities.getMinimapScreenCoordinate(bot, p.getX(), p.getY());
        return new Rectangle(coords[0] - 1, coords[1] - 1, 2, 2);
    }

    public boolean checkRun() {
        if (!settings.isRunning()) {
            if (settings.setRunning(true)) {
                waitFor(1000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return settings.isRunning();
                    }
                });
            }
        }
        return settings.isRunning();
    }

    public Position closestEmptyTile(Position t) {
        Position currBest = t;

        if (t == null) {
            return null;
        }
        int plane = t.getZ();
        int colValue = Flag.BLOCKED | Flag.BLOCKED2 | Flag.BLOCKED4 | Flag.WATER;
        for (int x = -1; x <= 1; x++) {
            Position check = new Position(t.getX() + x, t.getY(), plane);
            if (currBest == null || !canReach(currBest) || check.distance(myPosition()) < currBest.distance(myPosition())) {
                if (canReach(check)) {
                    currBest = check;
                }
            }
        }
        for (int y = -1; y <= 1; y++) {
            Position check = new Position(t.getX(), t.getY() + y, plane);
            if (currBest == null || !canReach(currBest) || check.distance(myPosition()) < currBest.distance(myPosition())) {
                if (canReach(check)) {
                    currBest = check;
                }
            }
        }
        return currBest;
    }

    public boolean canReach(final Position pos) {
        if (pos != null) {
            final int collisionValue = Flag.getFlag(this, pos);
            if ((collisionValue & (Flag.BLOCKED | Flag.BLOCKED2 | Flag.BLOCKED4 | Flag.WATER)) != 0) {
                return false;
            }
            LinkedList<Position> localPath = localPathFinder.findPath(pos);
            return localPath != null && !localPath.isEmpty();
        }
        return false;
    }

    public int getTotalLevel() {
        int lvls = 0;
        for (int i = 0; i < 23; i++) {
            Skill skill = Skill.values()[i];
            lvls += skills.getStatic(skill);
        }
        return lvls;
    }

    public Player getPlayer(final String name) {
        for (Player p : players.getAll()) {
            if (p != null) {
                char[] chars = p.getName().toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if ((byte) chars[i] == -96) {
                        chars[i] = (char) 32;
                    }
                }
                String n = String.copyValueOf(chars);
                if (n.equalsIgnoreCase(name)) {
                    return p;
                }
            }
        }
        return null;
    }

    public boolean isEscCloseInterface() {
        int config = configs.get(1224);
        return (config & 0b10000000000000000000000000000000) == 0b10000000000000000000000000000000;
    }

    public boolean openInventory() {
        return tabs.getOpen() == Tab.INVENTORY || (isEscCloseInterface() ? interact.interact(new RectangleDestination(bot, new Rectangle(629, 170, 25, 27))) : keyboard.typeKey((char) KeyEvent.VK_ESCAPE));
    }

    public void trayMessage(String title, String message) {
        imageUtils.trayMessage(title, message, TrayIcon.MessageType.NONE);
    }

    /**
     * Never needed here (ONLY USED IN ATScript)
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public int onLoop() throws InterruptedException {
        return 0;
    }

}
