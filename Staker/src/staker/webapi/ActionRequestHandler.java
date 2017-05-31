package staker.webapi;

/**
 * Created by Tony on 31/05/2017.
 */
public abstract class ActionRequestHandler implements ActionType {

    private String requestType;
    private String actionType;

    public ActionRequestHandler(String actionCommand){
        this.requestType = actionCommand;
        this.actionType = this.setActionType();
        this.handleActionRequest();
    }

    private void handleActionRequest(){
        if(this.requestType.equals(this.actionType))
            this.processAction();
    }

}
