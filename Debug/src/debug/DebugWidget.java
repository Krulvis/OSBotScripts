package debug;

import api.ATScript;
import api.ATState;
import api.util.ATPainter;
import api.util.gui.GUIWrapper;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.util.LinkedList;

/**
 * Created by s120619 on 29-4-2017.
 */
@ScriptManifest(name = "Debug Widgets", author = "Krulvis", version = 1.0d, info = "", logo = "")
public class DebugWidget extends ATScript {

    @Override
    public void onStart() {
        super.init(this);
    }

    @Override
    public int onLoop() {
        System.out.println(waitingForInput());
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
}
