package api.wrappers.staking.data;


import api.ATMethodProvider;
import org.osbot.rs07.api.ui.RS2Widget;

import java.awt.*;
import java.awt.event.MouseEvent;


/**
 * Created by Krulvis on 11-8-2015.
 */
public enum RuleSet {


    //BOXING("Boxing",        Rule.BOXING,        null,                   Settings.Style.ACCURATE,        new Rectangle(15, 30, 74, 34), false),
    WHIPPING("Aby Whip", Rule.WHIPPING, Settings.Weapon.ABYSSAL_WHIP, Settings.Style.CONTROLLED, new Rectangle(15, 48, 74, 30), false),
    DDS("DDS", Rule.DDSING, Settings.Weapon.VINE_WHIP, Settings.Style.CONTROLLED, new Rectangle(15, 81, 74, 30), false),
    VINE_WHIP("Vine Whip", Rule.WHIPPING, Settings.Weapon.VINE_WHIP, Settings.Style.CONTROLLED, new Rectangle(15, 113, 74, 30), false),
    //HASTA("Hasta",          Rule.WHIPPING,      Weapon.HASTA,           Settings.Style.DEFENSIVE,       new Rectangle(15, 150, 74, 34), false),
    //D_SCIM("Dragon Scim",   Rule.DDSING,        Weapon.DRAGON_SCIMITAR, Settings.Style.AGGRESSIVE,      new Rectangle(15, 152, 74, 34), false),
    ;
    Rectangle rect;
    String name;
    Rule[] rules;
    Settings.Weapon w;
    boolean inUse;
    Settings.Style style;

    RuleSet(String name, Rule[] rules, Settings.Weapon w, Settings.Style style, Rectangle rect, boolean inUse) {
        this.rect = rect;
        this.rules = rules;
        this.name = name;
        this.w = w;
        this.style = style;
    }

    public Rectangle getRect() {
        return rect;
    }

    public Rule[] getRules() {
        return rules;
    }

    public boolean isUsed() {
        return inUse;
    }

    public void setUsed(boolean inUse) {
        setUsed(this);
    }

    public String getName() {
        return name;
    }

    public Settings.Weapon getWeapon() {
        return w;
    }

    public Settings.Style getStyle() {
        return style;
    }

    public boolean allGood(ATMethodProvider api) {
        for (Rule r : Rule.values()) {
            if (r.shouldBeSet(rules) && !r.isSet(api)) {
                return false;
            } else if (r.shouldNotBeSet(rules) && r.isSet(api)) {
                return false;
            }
        }
        return true;
    }

    public static RuleSet getRuleSet() {
        for (RuleSet set : RuleSet.values()) {
            if (set.inUse) {
                return set;
            }
        }
        return null;
    }

    public static void setUsed(RuleSet ruleSet) {
        if (ruleSet != null) {
            for (RuleSet set : RuleSet.values()) {
                if (set.getName().equals(ruleSet.getName())) {
                    set.inUse = true;
                } else {
                    set.inUse = false;
                }
            }
        }
    }

    public static RuleSet checkClick(MouseEvent e) {
        if (e != null && e.getPoint() != null) {
            for (RuleSet set : RuleSet.values()) {
                if (set.getRect().contains(e.getPoint())) {
                    e.consume();
                    set.setUsed(true);
                    return set;
                }
            }
        }
        return null;
    }

    public boolean clickedSet(MouseEvent e) {
        if (getRect().contains(e.getPoint())) {
            e.consume();
            setUsed(true);
            return true;
        }
        return false;
    }

    public static void drawAllSetsButtons(Graphics g, Color one, Color tow) {
        for (RuleSet set : RuleSet.values()) {
            set.drawSetButton(g, one, tow);
        }
    }

    public void drawSetButton(Graphics g, Color one, Color tow) {
        Color oldColor = g.getColor();
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(one);
        if (inUse) {
            g2.setColor(tow);
        }
        //g2.draw(set.getRect());
        g2.fill(getRect());
        g2.setColor(Color.BLACK);
        Point center = new Point((int) getRect().getCenterX(), (int) getRect().getCenterY());
        g2.drawString(getName(), center.x - g2.getFontMetrics().stringWidth(getName()) / 2, center.y);
        g.setColor(oldColor);
    }

    public void drawRuleSetRules(Graphics2D g, ATMethodProvider api, RuleSet set) {
        Rule[] all = Rule.values();
        Rule[] rules = set.getRules();
        if (rules != null) {
            for (Rule r : all) {
                drawRule(g, api, r, r.shouldBeSet(rules));
            }
        }
    }

    public void drawRule(Graphics2D g, ATMethodProvider api, Rule r, boolean shouldBeSet) {
        RS2Widget wc = r.ordinal() > 10 ? api.widgets.get(Data.DUEL_INTERFACE_1, Data.EQUIPMENT_PARENT_1).getChildWidget(r.childId) : api.widgets.get(Data.DUEL_INTERFACE_1, r.childId);
        Color c = g.getColor();
        if (wc != null && wc.isVisible() && wc.getBounds() != null) {
            g.setColor(r.isSet(api) ? (shouldBeSet ? Color.GREEN : Color.RED) : (shouldBeSet ? Color.RED : Color.GREEN));
            g.draw(wc.getBounds());
        }
        g.setColor(c);
    }

}
