package api.web.nodes;


import api.ATMethodProvider;
import api.web.LocalPuroAction;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Model;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.input.mouse.EntityDestination;
import org.osbot.rs07.utility.Condition;

/**
 * Created by Krulvis on 5-7-2016.
 */
public class WheatNode extends LocalPuroAction {

    private RS2Object go;

    public WheatNode(ATMethodProvider mp, LocalTileAction a, Position direction, Position finalTile, RS2Object go) {
        super(mp, a, direction, finalTile);
        this.go = go;
    }

    @Override
    public float getCost() {
        return 200;
    }

    @Override
    public boolean execute() {
        if (go == null) {
            return false;
        }
        if (mp.magic.isSpellSelected()) {
            mp.magic.deselectSpell();
        }
        if (mp.inventory.isItemSelected()) {
            mp.inventory.deselectItem();
        }
        if (go != null) {
            if (!go.isVisible()) {
                mp.camera.toEntity(go);
            }
            if (mp.interact(go, "Push-through")) {
                mp.waitFor(800 * mp.distance(go), new Condition() {
                    @Override
                    public boolean evaluate() {
                        return mp.isAnimating();
                    }
                });
                if (mp.isAnimating()) {
                    mp.waitFor(3000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return !mp.isAnimating() && mp.myPosition().equals(getDestinationTile());
                        }
                    });
                    mp.sleep(600, 750);
                }
            }
        }
        return true;
    }

    @Override
    public int getType() {
        return 2;
    }
}
