package api.web.nodes;

import api.ATMethodProvider;
import api.web.NPCChat;
import org.osbot.rs07.api.filter.ActionFilter;
import org.osbot.rs07.api.filter.PositionFilter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.utility.Condition;

/**
 * Created by Krulvis on 14-Mar-17.
 */
public class Door extends LocalTileAction {

    private static NPCChat SECURITY_STRONGHOLD_ANSWERS = null;
    private RS2Object door;

    public Door(ATMethodProvider mp, WebAction src, Position dir, Position finalTile, RS2Object door) {
        super(mp, src, dir, finalTile);
        SECURITY_STRONGHOLD_ANSWERS = new NPCChat(mp, true, "Don't give them the information and send an 'Abuse report.'", "Don't tell them anything and click the 'Report Abuse' button.", "No - Jagex does not block your password.", "Virus scan my computer then change my password and recoveries.", "Virus scan my computer then change my password.", "Only on the RuneScape website.",
                "Nowhere.", "Game Inbox on the RuneScape website.", "No", "No, it might steal my password.", "Don't give him my password.", "To help me recover my password if I forget it or it is stolen.", "Nobody", "Don't tell them anything and click the 'Report Abuse' button.", "Every couple of months", "Don't tell them anything and inform Jagex through the game website.", "Memorable",
                "Don't give them the information and send a 'Abuse Report'.", "The birthday of a famous person or event.", "Politely tell them no and then use the 'Report Abuse' button.", "No.", "Talk to any banker in RuneScape.");
        this.door = door;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public float getCost() {
        return super.getCost() + 56;
    }

    @Override
    public boolean execute() {
        final String name = door.getName();
        if (name.equals("Gate of War") || name.equals("Rickety door") || name.equals("Oozing barrier") || name.equals("Portal of Death")) {
            if (SECURITY_STRONGHOLD_ANSWERS.isActive()) {
                boolean success = SECURITY_STRONGHOLD_ANSWERS.execute();
                if (!success) {
                    return false;
                } else {
                    mp.sleep(600, 800);
                    return true;
                }
            }
        }
        int distance = mp.distance(door);
        if (distance >= 6)
            return false;
        double randomCamera = Math.random();
        if (randomCamera < 0.3) {
            mp.camera.toEntity(door);
        }

        // Add a sleep because of rubber banding with auto-closing doors
        mp.sleep(600, 800);

        if (door.hasAction("Open")) {
            if (door.interact("Open")) {
                mp.waitFor(1000 + (distance * 750), new Condition() {
                    @Override
                    public boolean evaluate() {
                        return checkOpen();
                    }
                });
            }
            return checkOpen();
        } else {
            return door.interact(door.getActions()[0]);
        }
    }

    @Override
    public String toString() {
        return "DOOR:" + super.toString();
    }

    public RS2Object getDoor() {
        return door;
    }

    public boolean checkOpen() {
        return mp.objects.closest(new PositionFilter<RS2Object>(door.getPosition()), new ActionFilter<RS2Object>("Open")) == null;
    }
}
