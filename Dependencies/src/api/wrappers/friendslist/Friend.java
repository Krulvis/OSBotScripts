package api.wrappers.friendslist;

import api.ATMethodProvider;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.utility.Condition;

import static api.wrappers.friendslist.ATFriendsList.LIST_CHILD;
import static api.wrappers.friendslist.ATFriendsList.WIDGET;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Friend {

    private String name;
    private int world;
    private ATMethodProvider api;

    public Friend(RS2Widget wc, ATMethodProvider api) {
        this.api = api;
        this.name = wc.getMessage();
        int world = -1;
        try {
            for (int i = 1; i <= 2; i++) {
                RS2Widget worldChild = api.widgets.get(wc.getRootId(), wc.getSecondLevelId() + i);
                if (worldChild != null) {
                    String worldText = worldChild.getMessage();
                    if (worldText.contains("World")) {
                        int index = worldText.indexOf("World ") + 6;
                        world = index > 0 ? Integer.parseInt(worldText.substring(index)) : -1;
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        this.world = world;
    }

    public int getWorld() {
        return world;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return world > -1;
    }

    public boolean sendMessage(String message) {
        if (!api.friends.isSendingMessage()) {
            RS2Widget wc = api.getWidgetChild(WIDGET, LIST_CHILD, new Filter<RS2Widget>() {
                @Override
                public boolean match(RS2Widget wc) {
                    return wc != null && wc.getMessage().contains(name);
                }
            });
            if (wc != null && wc.interact("Message")) {
                api.waitFor(new Condition() {
                    @Override
                    public boolean evaluate() {
                        return api.friends.isSendingMessage();
                    }
                }, 2000);
            }
        }
        if (api.friends.isSendingMessage()) {
            api.keyboard.typeString(message, true);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getName() + ", " + getWorld();
    }
}
