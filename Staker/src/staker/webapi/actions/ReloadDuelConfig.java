package staker.webapi.actions;

import staker.webapi.ActionRequestHandler;

/**
 * Created by Tony on 31/05/2017.
 */
public class ReloadDuelConfig extends ActionRequestHandler {

    public ReloadDuelConfig(String actionCommand) {
        super(actionCommand);
    }

    @Override
    public String setActionType() {
        return "RELOAD_DUELCONFIG";
    }

    @Override
    public void processAction() {
        //TODO
        // DO SOMETHING WHEN RELOAD DUELCONFIG IS TRIGGERED
    }
}
