package api.wrappers;

import api.ATMethodProvider;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.mouse.RectangleDestination;

import java.awt.*;

/**
 * Created by Krulvis on 04-Apr-17.
 */
public class ATEmotes extends ATMethodProvider {

    public ATEmotes(ATMethodProvider parent) {
        init(parent);
    }

    public enum Emote {
        YES,
        NO,
        BOW,
        ANGRY,
        THINK,
        WAVE,
        SHRUG,
        CHEER,
        BECKON,
        LAUGH,
        JUMP_FOR_JOY,
        YAWN,
        DANCE,
        JIG,
        SPIN,
        HEADBANG,
        CRY,
        BLOW_KISS,
        PANIC,
        RASPBERRY,
        CLAP,
        SALUTE,
        GOBLIN_BOW,
        GOBLIN_SALUTE,
        GLASS_BOX,
        CLIMB_ROPE,
        LEAN,
        GLASS_WALL,
        IDEA,
        STAMP,
        FLAP,
        SLAP_HEAD,
        ZOMBIE_WALK,
        ZOMBIE_DANCE,
        SCARED,
        RABBIT_HOP,
        SKILL_CAPE;

        @Override
        public String toString() {
            String s = super.toString().replace('_', ' ');
            return s.charAt(0) + s.substring(1, s.length()).toLowerCase();
        }

    }

    private static final Rectangle BOUNDS = new Rectangle(547, 205, 190, 261);

    public static Rectangle getBounds() {
        return BOUNDS;
    }

    public boolean doEmote(Emote emote) {
        if (!isOpen()) {
            if (!openTab()) {
                return false;
            }
        }
        RS2Widget wc = widgets.get(216, 1);
        if (!validateWidget(wc)) {
            return false;
        }
        RS2Widget[] children = wc.getChildWidgets();
        for (RS2Widget c : children) {
            if (!hasAction(emote.toString(), c.getInteractActions())) {
                continue;
            }
            if (scrollTo(c, wc)) {
                return c.interact();
            }
            return false;

        }
        return false;
    }

    public boolean isOpen() {
        return tabs.getOpen() == Tab.EMOTES;
    }

    private boolean hasAction(final String string, String[] actions) {
        if (actions != null && actions.length > 0 && string != null) {
            for (String q : actions) {
                if (string.equalsIgnoreCase(q)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean openTab() {
        if (tabs.getOpen() != Tab.EMOTES) {
            return tabs.open(Tab.EMOTES);
        }
        return true;
    }
}
