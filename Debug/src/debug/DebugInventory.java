package debug;

import api.ATScript;
import api.ATState;
import api.util.ATPainter;
import api.util.gui.GUIWrapper;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by Krulvis on 15-Jun-17.
 */
@ScriptManifest(logo = "", author = "Krulvis", name = "Debug Inventory", info = "", version = 1.0D)
public class DebugInventory extends ATScript {

    Item[] inv;

    @Override
    public int onLoop() {


        return 500;
    }

    @Override
    public void update() {

    }

    @Override
    public void onPaint(Graphics2D g) {
        int x = 10, y = 50, yy = 10;
        if (inv == null) {
            g.drawString("Inv is null;", x, y += yy);
        } else {
            for (int i = 0; i < inv.length; i++) {
                Item item = inv[i];
                g.drawString("[" + i + "]: " + (item == null ? "null" : item.getId()), x, y += yy);
            }
        }

    }

    @Override
    public void onMessage(Message m){
        System.out.println("[" + m.getTypeId() + "]: " + m.getMessage());
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
