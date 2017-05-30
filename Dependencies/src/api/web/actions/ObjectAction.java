package api.web.actions;

import api.ATMethodProvider;
import org.osbot.rs07.api.filter.ActionFilter;
import org.osbot.rs07.api.filter.NameFilter;
import org.osbot.rs07.api.filter.PositionFilter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;

public class ObjectAction extends Action {
    private final Position location;
    private final String[] actions;
    private final String name;

    public ObjectAction(ATMethodProvider mp, final String name, final String... actions) {
        super(mp);
        this.location = null;
        this.name = name;
        this.actions = actions;
    }

    public ObjectAction(ATMethodProvider mp, final Position t, final String name, final String... actions) {
        super(mp);
        this.location = t;
        this.name = name;
        this.actions = actions;
    }

    @Override
    public boolean traverse() {
        final RS2Object obj = getRS2Object();
        if (obj == null) {
            return false;
        }
        if (obj.isVisible()) {
            /*
             * String actions = getActions(); String name = getName(); if (actions
			 * == null) { return obj.interact(name); }
			 */
            final String actionString = getCompleteActionString(obj);
            if (!actionString.contains("Use")) {
                if (inventory.isItemSelected()) {
                    inventory.deselectItem();
                    sleep(200, 300);
                }
                if (magic.isSpellSelected()) {
                    magic.deselectSpell();
                    sleep(200, 300);
                }
            }
            return obj.interact(actionString);
        }
        return walking.walk(obj.getPosition());
    }

    public String getCompleteActionString(RS2Object obj) {
        final String[] objActions = getActions();
        if (objActions != null && objActions.length == 0) {
            for (String action : objActions) {
                if (obj.hasAction(action)) {
                    return action;
                }
            }
        }
        return "";
    }

    public RS2Object getRS2Object() {
        final String objAction[] = getActions();
        final String objName = getName();
        final Position objLocation = getLocation();

        if (objLocation != null) {
            RS2Object objs = objects.closest(new PositionFilter<RS2Object>(objLocation), new NameFilter<RS2Object>(objName), new ActionFilter<RS2Object>(objAction));
            if (objs != null) {
                return objs;
            }
        } else {
            return objects.closest(new NameFilter<>(objName), new ActionFilter<>(objAction));
        }
        return null;
    }

    public Position getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    /**
     * Should include object name
     *
     * @return
     */
    public String[] getActions() {
        return actions;
    }

    @Override
    public double getCost() {
        return 5;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public String toString() {
        return "ObjectAction: {" + getActions() + " " + getName() + "@" + getLocation() + "}";
    }
}