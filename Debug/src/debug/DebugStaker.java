package debug;

import api.ATScript;
import api.ATState;
import api.util.ATPainter;
import api.util.gui.GUIWrapper;
import org.osbot.rs07.script.ScriptManifest;

import java.util.LinkedList;

/**
 * Created by s120619 on 29-4-2017.
 */
@ScriptManifest(author = "Krulvis", version = 1.0D, logo = "", info = "", name = "Debug Staker")
public class DebugStaker extends ATScript {


    int otherOfferedAmount = 0;

    @Override
    public void onStart() {
        super.init(this);
    }

    @Override
    public int onLoop() {
        if (stake.isSecondScreenOpen()) {
            otherOfferedAmount = stake.otherOfferedAmount();
        }
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
