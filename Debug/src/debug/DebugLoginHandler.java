package debug;

import api.ATScript;
import api.ATState;
import api.util.ATPainter;
import api.util.gui.GUIWrapper;
import org.osbot.rs07.script.ScriptManifest;

import java.util.LinkedList;

/**
 * Created by Krulvis on 30-May-17.
 */
@ScriptManifest(logo = "", author = "Krulvis", name = "Debug RandomHandler", info = "", version = 1.0D)
public class DebugLoginHandler extends ATScript {

	@Override
	public void onStart() {
		useWebAPI();
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
