package puropuro.states;

import api.ATState;
import api.util.Random;
import puropuro.PuroPuro;

/**
 * Created by Krulvis on 12-Mar-17.
 */
public class Starting extends ATState<PuroPuro> {

    public Starting(PuroPuro script) {
        super("Starting script...", script);
    }

    @Override
    public int perform() {

        return Random.smallSleep();
    }

    @Override
    public boolean validate() {
        return script.implings == null;
    }
}
