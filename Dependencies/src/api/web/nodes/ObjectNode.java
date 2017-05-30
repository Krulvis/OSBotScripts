package api.web.nodes;

import api.ATMethodProvider;
import api.web.actions.ObjectAction;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;

/**
 * Created by Krulvis on 14-Mar-17.
 */
public class ObjectNode extends LocalTileAction{
    private RS2Object object;
    private ObjectAction oa;

    public ObjectNode(ATMethodProvider mp, WebAction src, Position dir, Position finalTile, RS2Object object) {
        super(mp, src, dir, finalTile);
        this.object = object;
    }

    public ObjectNode(ATMethodProvider mp, LocalTileAction src, Position dir, Position finalTile, ObjectAction action) {
        super(mp, src, dir, finalTile);
        this.oa = action;
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public float getCost() {
        return super.getCost() + 56;
    }

    @Override
    public boolean execute() {
        if (oa != null) {
            return oa.traverse();
        }
        int distance = mp.distance(object);
        if (distance >= 6)
            return false;
        mp.camera.toEntity(object);
        if (object.hasAction("Open")) {
            return object.interact("Open");
        }
        String[] actions = object.getActions();
        for(String s : actions){
            if(s != null && !s.equals("Examine")){
                return object.interact(s);
            }
        }
        return object.interact(object.getActions()[0]);
    }

    @Override
    public String toString() {
        return "DOOR:" + super.toString();
    }
}
