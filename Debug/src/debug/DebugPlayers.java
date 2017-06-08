package debug;

import api.ATScript;
import api.ATState;
import api.util.ATPainter;
import api.util.gui.GUIWrapper;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by s120619 on 28-4-2017.
 */
@ScriptManifest(author = "krulvis", version = 1.0D, logo = "", info = "Debug players", name = "Debug Players")
public class DebugPlayers extends ATScript {

    @Override
    public void onStart() {
        super.init(this);
    }

    @Override
    public int onLoop() throws InterruptedException {


        String name = "Cows Lance";
        Player p = getPlayer(name);
        System.out.println((p == null ? "Did not " : "") + "Find " + name);

        return 1000;
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

    @Override
    public void onPaint(Graphics2D g2) {
        g2.drawString("Esc closing thing: " + isEscCloseInterface(), 10, 100);
    }
}
