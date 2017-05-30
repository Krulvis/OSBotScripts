package debug.interaction;

import api.ATScript;
import api.ATState;
import api.util.ATPainter;
import api.util.gui.GUIWrapper;
import api.wrappers.staking.data.RuleSet;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Option;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.Condition;

import java.awt.*;
import java.util.LinkedList;

import static api.wrappers.staking.data.Data.ARENA_AREA;

/**
 * Created by s120619 on 29-4-2017.
 */
@ScriptManifest(author = "Krulvis", version = 1.0D, logo = "", info = "", name = "Debug Interaction")
public class DebugInteraction extends ATScript {

    @Override
    public void onStart() {
        super.init(this);
    }

    Rectangle target = null;
    Player opponent = null;
    RectangleDestination dest = null;

    @Override
    public int onLoop() {
        if (ARENA_AREA.contains(myPosition())) {
            opponent = getPlayer("Cows Lance");
            Character<?> check = myPlayer().getInteracting();
            if (check == null || !check.getName().equalsIgnoreCase(opponent.getName()) || !check.isHitBarVisible()) {
                if (opponent.getPosition().isVisible(bot)) {
                    target = stake.getCenterPoint(opponent);
                    if (target != null && interact.interact(dest = new RectangleDestination(bot, target), "Fight", opponent.getName(), false)) {
                        waitFor(100, new Condition() {
                            @Override
                            public boolean evaluate() {
                                final Character check = myPlayer().getInteracting();
                                return check != null && check.getName().equalsIgnoreCase(opponent.getName());
                            }
                        });
                    }
                } else {
                    camera.toPosition(opponent.getPosition());
                }
            }
        }
        return 1000;

    }

    @Override
    public void onPaint(Graphics2D g2) {
        int x = 10;
        int y = 50;
        int yy = 10;
        if (target != null && opponent != null && dest != null) {
            g2.draw(target);
            g2.drawString("Sufficient: " + interact.sufficient("Fight", opponent.getName(), dest), x, y += yy);
            g2.drawString("Option index: " + interact.getOptionIndex("Fight", opponent.getName()), x, y += yy);
            g2.drawString("ooptinos: ", x, y += yy);
            for (Option o : menu.getMenu()) {
                g2.drawString(o.action.replaceAll("\\<[^>]*>", "") + ", " + o.name.replaceAll("\\<[^>]*>", ""), x, y += yy);
            }
        } else {
            g2.drawString("Target: " + (target != null), x, y += yy);
            g2.drawString("opponent: " + (opponent != null), x, y += yy);
            g2.drawString("Dest: " + (dest != null), x, y += yy);
        }
    }


    @Override
    public void update() {

    }

    @Override
    protected void initialize(LinkedList<ATState> statesToAdd) {

    }

    @Override
    protected Class<? extends ATPainter> getPainterClass() {
        return null;
    }

    @Override
    protected Class<? extends GUIWrapper> getGUI() {
        return null;
    }
}
