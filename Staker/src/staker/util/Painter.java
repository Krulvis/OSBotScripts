package staker.util;

import api.ATState;
import api.util.ATPainter;
import api.wrappers.staking.Duel;
import api.wrappers.staking.calculator.SPlayer;
import api.wrappers.staking.data.Rule;
import api.wrappers.staking.data.RuleSet;
import staker.Staker;
import staker.states.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Painter extends ATPainter<Staker> {

    public Painter(Staker script) {
        super(script);
    }

    @Override
    public void paint(Graphics2D g) {
        int y = this.y;
        int x = 275;
        ATState state = script.currentState;
        Duel duel = script.currentDuel;
        RuleSet ruleSet = script.ruleSet;
        if (state instanceof Starting) {
            drawString(g, "Connected with Web API: " + script.webAPI.isConnected(), 380, 325);
        } else if (state instanceof Waiting) {
            drawString(g, "Waiting", 410, 325);
        } else if (state instanceof Fight) {
            drawString(g, "Fighting", 410, 325);
        } else if (state instanceof EndFight) {
            drawString(g, state.getName(), 410, 325);
        } else if (duel != null) {
            if (shouldDisplayOdds(state) && duel.getOdds() != null) {
                g.setColor(BLACK_A);
                g.fillRect(265, 345, 110, 130);
                duel.drawOdds(g, script.ruleSet);
                ArrayList<Rule> list = Rule.listIncorrectRules(ruleSet, script);
                if (list == null || list.size() == 0) {
                    drawString(g, "All Rules are good!", x, y += yy, Color.GREEN);
                } else if (list.size() > 0) {
                    for (Rule rule : list) {
                        drawString(g, rule.name(), x, y += yy, Color.RED);
                    }
                }
                duel.drawStatsCompare(g);
            }
            if (shouldDisplayMoney(state)) {
                duel.drawMoney(g);
            }
        }
    }


    private boolean shouldDisplayOdds(ATState state) {
        return state instanceof First || state instanceof Second || state instanceof Third;
    }

    private boolean shouldDisplayMoney(ATState state) {
        return state instanceof Second || state instanceof Third;
    }
}
