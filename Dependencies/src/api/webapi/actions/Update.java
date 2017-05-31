package api.webapi.actions;

import api.ATMethodProvider;
import api.webapi.WebAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created by Krulvis on 16-Feb-17.
 */
public class Update extends WebAction {

    public Update(ATMethodProvider parent) {
        super(parent, "UPDATE");
    }

    public String updateScripts = "sh ./Update.sh";

    @Override
    public boolean perform() {

        System.out.println("Updating script!");
        try {
            if (updateScripts.contains("sh")) {
                System.setProperty("os.name", "Linux");
            }
            Process p = Runtime.getRuntime().exec(updateScripts);
            p.waitFor();
            System.setProperty("os.name", "Windows 7");
            String currentScript = bot.getScriptExecutor().getCurrent().getName();
            /*LocalScriptsLoader lsl = new LocalScriptsLoader();
            System.out.println("Reloading script classes...");
            lsl.loadClasses();
            for (Script q : lsl.getScripts()) {
                if (q.name().equalsIgnoreCase(currentScript)) {
                    System.out.println("Restarting script: " + currentScript);
                    TBot.getBot().getScriptHandler().startScript(q);
                    return -1;
                }
            }*/
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        //Never return true since client should be shut down on this action
        return false;
    }

    public static void main(String... args) {
        try {
            long start = System.currentTimeMillis();
            Process p = Runtime.getRuntime().exec("cmd /c start C:" + File.separator + "TopBot" + File.separator + "testUpdate.bat");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            int returnCode = p.waitFor();
            System.out.println("Python Script or OS Return Code: " + Integer.toString(returnCode));
            System.out.println("Done in: " + (System.currentTimeMillis() - start));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
