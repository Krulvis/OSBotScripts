package puropuro;

import api.ATScript;
import api.ATState;
import api.util.ATPainter;
import api.util.gui.GUIWrapper;
import api.web.PuroPathGenerator;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.ScriptManifest;
import puropuro.states.Banking;
import puropuro.states.Hunting;
import puropuro.states.Starting;
import puropuro.util.BindSpell;
import puropuro.util.Impling;

import javax.swing.*;
import java.util.LinkedList;

/**
 * Created by Krulvis on 12-Mar-17.
 */
@ScriptManifest(version = 1.0d, info = "Catches implings", logo = "", author = "Krulvis", name = "PuroPuro")
public class PuroPuro extends ATScript {

    public final static int JAR = 11260, NET = 10010, MAGIC_NET = 11259, WATER = 555, EARTH = 557, NATURE = 561, DRAMEN = 772;
    public BindSpell bindSpell = BindSpell.NONE;
    public int caught = 0, restockAmount = 500;
    public boolean restockWhenOut = true, spawnCamp = false;
    public Position spawnTile = null;
    public PuroPathGenerator ppg;

    public Impling[] implings = null;

    @Override
    public void onStart() {
        setPrivateVersion();
        ppg = new PuroPathGenerator(this);
    }

    @Override
    public void update() {

    }

    @Override
    protected void initialize(LinkedList<ATState> statesToAdd) {
        statesToAdd.add(new Starting(this));
        statesToAdd.add(new Banking(this));
        statesToAdd.add(new Hunting(this));
    }

    @Override
    protected Class<? extends ATPainter> getPainterClass() {
        return Painter.class;
    }

    @Override
    protected Class<? extends GUIWrapper> getGUI() {
        return null;
    }
}
