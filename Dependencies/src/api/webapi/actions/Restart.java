package api.webapi.actions;

import api.ATMethodProvider;
import api.webapi.WebAPI;

import java.io.IOException;

/**
 * Created by Krulvis on 16-Feb-17.
 */
public class Restart extends WebAction {

    public Restart(ATMethodProvider parent) {
        super(parent, "RESTART");
    }

    public String startBot = "sh ./StartBots.sh";

    @Override
    public boolean perform() {
        try {
            //String opp = System.
            if (startBot.contains("sh")) {
                System.setProperty("os.name", "Linux");
            }
            Runtime.getRuntime().exec(startBot);
            System.setProperty("os.name", "Windows 7");
            System.exit(0);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
