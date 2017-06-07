package api.event.listener;

import api.ATMethodProvider;
import api.ATScript;

/**
 * Created by Krulvis on 07-Jun-17.
 */
public abstract class EventHandler extends ATMethodProvider {

    public ATScript script;

    public EventHandler(ATScript script) {
        init(script);
        this.script = script;
    }

    public abstract void handle();
}
