package api.webapi.actions;

import api.webapi.WebAPI;

import java.io.IOException;

/**
 * Created by Krulvis on 16-Feb-17.
 */
public class Restart extends Action {

    public Restart(WebAPI parent){
        super(parent);
    }

    public String startBot = "sh ./StartBots.sh";

    @Override
    public int loop() {
        try {
            //String opp = System.
            if(startBot.contains("sh")){
                System.setProperty("os.name", "Linux");
            }
            Runtime.getRuntime().exec(startBot);
            System.setProperty("os.name", "Windows 7");
            System.exit(0);
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
