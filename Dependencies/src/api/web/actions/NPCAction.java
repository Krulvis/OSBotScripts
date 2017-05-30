package api.web.actions;


import api.ATMethodProvider;
import api.web.NPCChat;
import org.osbot.rs07.api.filter.NameFilter;
import org.osbot.rs07.api.model.NPC;

public class NPCAction extends Action {
    private final String action;
    private final String name;
    private final NPCChat chat;

    public NPCAction(ATMethodProvider mp, String name, String action) {
        this(mp, name, action, null);
    }

    public NPCAction(ATMethodProvider mp, String name, String action, NPCChat chat) {
        super(mp);
        this.name = name;
        this.action = action;
        this.chat = chat;
    }

    @Override
    public boolean traverse() {
        NPC npc = getNPC();
        if (npc == null) {
            return false;
        }
        NPCChat chat1 = getChat();
        if (chat1 != null) {
            if (chat1.isActive()) {
                return chat1.execute();
            }
        }
        String action1 = getCompleteActionString();
        if (npc.isOnScreen()) {
            return npc.interact(action1);
        }
        return walking.walk(npc.getPosition());
    }

    public String getCompleteActionString() {
        final String npcAction = getAction();
        final String npcName = getName();
        if (npcAction == null) {
            return npcName;
        }
        return npcAction + " " + npcName;
    }

    public String getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public NPCChat getChat() {
        return chat;
    }

    public NPC getNPC() {
        final String name = getName();
        return npcs.closest(new NameFilter<NPC>(name));
    }

    @Override
    public double getCost() {
        return 10;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public String toString() {
        return "NPCAction: {" + getAction() + " " + getName() + "}";
    }
}