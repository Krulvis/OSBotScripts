package generalstoreseller;

import api.ATState;
import api.util.ATPainter;
import generalstoreseller.states.Buying;

import java.awt.*;

/**
 * Created by Krulvis on 16-Feb-17.
 */
public class Painter extends ATPainter<GeneralStoreSeller> {

    public Painter(GeneralStoreSeller script) {
        super(script, 7);
    }

    @Override
    public void paint(Graphics2D g) {
        int y = this.y;
        ATState s = script.currentState;
        drawString(g, "State: " + (s != null ? s.getName() : "Checking prices"), x, y += yy);

        if (script.sellables != null && script.sellables.isChecking()) {
            drawString(g, "Checking online prices...", x, y += yy);
        }
        if (script.buyingState != null && s instanceof Buying) {
            drawString(g, "Resupplying: " + script.buyingState.resupply, x, y += yy);
        }
        drawPerHour(g, "Turnover: ", script.turnOver, script.timer.getPerHour(script.turnOver), x, y += yy);
        if (script.costs > 0) {
            drawPerHour(g, "Costs: ", script.costs, script.timer.getPerHour(script.costs), x, y += yy);
            if (script.turnOver > script.costs) {
                drawPerHour(g, "Profit: ", script.turnOver - script.costs, script.timer.getPerHour(script.turnOver - script.costs), x, y += yy);
            }
        }
    }
}
