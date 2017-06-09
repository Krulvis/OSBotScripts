package debug;

import api.ATScript;
import api.ATState;
import api.event.listener.inventory.InventoryListener;
import api.util.ATPainter;
import api.util.Random;
import api.util.gui.GUIWrapper;
import com.sun.media.sound.ModelDestination;
import org.osbot.rs07.api.def.ItemDefinition;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by Krulvis on 09-Jun-17.
 */
@ScriptManifest(logo = "", author = "Krulvis", name = "Debug Items", info = "", version = 1.01D)
public class DebugItems extends ATScript implements InventoryListener {

    @Override
    public void onStart() {
        init(this);
    }

    private Item current;

    @Override
    public void update() {

    }


    @Override
    public void onPaint(Graphics2D g) {
        int y = 100, x = 10, yy = 10;
        if (current != null) {
            ItemDefinition def = ItemDefinition.forId(current.getId());
            if (def != null) {
                g.drawString("ID: " + def.getId(), x, y += yy);
                g.drawString("NotedID: " + def.getNotedId(), x, y += yy);
                g.drawString("UNNotedID: " + def.getUnnotedId(), x, y += yy);
                g.drawString("IS Noted: " + def.isNoted(), x, y += yy);
                g.drawString("Name: " + def.isNoted(), x, y += yy);
            }
        }
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

    @Override
    public void itemAdded(Item item, int amount) {
        System.out.println("Item added: " + item.getName() + ": " + amount);
    }

    @Override
    public void itemRemoved(Item item, int amount) {

    }
}
