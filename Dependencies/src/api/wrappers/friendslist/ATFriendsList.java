package api.wrappers.friendslist;

import api.ATMethodProvider;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.utility.Condition;

import java.util.ArrayList;

/**
 * Created by Krulvis on 30-May-17.
 */
public class ATFriendsList extends ATMethodProvider {

    public final static int WIDGET = 429;
    public final static int LIST_CHILD = 3;

    public final static int FRIEND_COLOR = 16777215;
    public final static int WORLD_COLOR = 16711680;

    public ArrayList<Friend> friendsList;

    public ATFriendsList(ATMethodProvider parent) {
        init(parent);
        friendsList = new ArrayList<>();
    }


    public boolean addFriend(final String name) {
        if (isAdded(name)) {
            return true;
        }
        if (!isAddingFriend()) {
            if (tabs.getOpen() != Tab.FRIENDS) {
                if (tabs.open(Tab.FRIENDS)) {
                    waitFor(2000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return tabs.getOpen() == Tab.FRIENDS;
                        }
                    });
                }
            }
            if (tabs.getOpen() == Tab.FRIENDS) {
                RS2Widget button = getAddButton();
                if (button != null && button.interact()) {
                    waitFor(2000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return isAddingFriend();
                        }
                    });
                }
            }
        }
        if (isAddingFriend()) {
            keyboard.typeString(name, true);
            waitFor(3000, new Condition() {
                @Override
                public boolean evaluate() {
                    return isAdded(name);
                }
            });
            return isAdded(name);
        }
        return false;
    }

    private RS2Widget getAddButton() {
        return getWidgetChild(429, new Filter<RS2Widget>() {
            @Override
            public boolean match(RS2Widget wc) {
                return wc != null && wc.getMessage().contains("Add Friend");
            }
        });
    }

    private boolean isAddingFriend() {
        final RS2Widget ba = getWidgetChild(162, new Filter<RS2Widget>() {
            @Override
            public boolean match(RS2Widget wc) {
                return wc != null && wc.getMessage() != null
                        && (wc.getMessage().contains("Enter name of friend to add to list"));
            }
        });
        if (ba != null) {
            RS2Widget w = widgets.get(ba.getRootId(), ba.getSecondLevelId() + 1);
            return w != null && w.getMessage().endsWith("*");
        }
        return false;
    }

    public boolean isSendingMessage() {
        final RS2Widget ba = getWidgetChild(162, new Filter<RS2Widget>() {
            @Override
            public boolean match(RS2Widget wc) {
                return wc != null && wc.getMessage() != null
                        && (wc.getMessage().contains("Enter message to send to "));
            }
        });
        if (ba != null) {
            RS2Widget w = widgets.get(ba.getRootId(), ba.getSecondLevelId() + 1);
            return w != null && w.getMessage().endsWith("*");
        }
        return false;
    }

    public ArrayList<Friend> reloadFriends() {
        friendsList = new ArrayList<>();
        RS2Widget wc = widgets.get(WIDGET, LIST_CHILD);
        if (wc != null) {
            RS2Widget[] friends = wc.getChildWidgets();
            for (RS2Widget friend : friends) {
                if (friend != null && friend.getTextColor() == FRIEND_COLOR && friend.getMessage() != null) {
                    //System.out.println("Found friend: " + friend.getText());
                    friendsList.add(new Friend(friend, this));
                }
            }
        }
        return friendsList;
    }

    public Friend getFriend(String playerName) {
        playerName = playerName.replaceAll("_", " ").replaceAll("-", " ");
        RS2Widget wc = widgets.get(WIDGET, LIST_CHILD);
        if (playerName == null) {
            return null;
        }
        if (wc != null) {
            RS2Widget[] friends = wc.getChildWidgets();
            for (RS2Widget friend : friends) {
                if (friend != null && friend.getTextColor() == FRIEND_COLOR && friend.getMessage() != null) {
                    String name = friend.getMessage().replaceAll("-", " ").replaceAll("_", " ");
                    if (playerName.equalsIgnoreCase(name)) {
                        return new Friend(friend, this);
                    }
                }
            }
        }
        return null;
    }

    public boolean isAdded(String playerName) {
        return getFriend(playerName) != null;
    }


}
