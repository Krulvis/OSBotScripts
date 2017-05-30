package api.util.gui;

import api.ATScript;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Created by Krulvis on 29-May-17.
 */
public abstract class GUIWrapper<S extends ATScript> extends JFrame {

    public boolean stop = false;
    private JPanel panel;
    public S script;
    public String settingsFolder;

    public GUIWrapper(S script) {
        this.script = script;
        this.settingsFolder = System.getProperty("user.home") + File.separator + "OSBot" + File.separator + "data" + File.separator + script.getName() + File.separator;
        File folder = new File(this.settingsFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    protected void startScript() {
        script.isScriptRunning.set(true);
        saveSettings();
        setVisible(false);
        dispose();
    }

    protected void construct(JPanel mainPanel) {
        this.panel = mainPanel;
        try {
            SwingUtilities.invokeLater(gui);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (script != null && script.bot.getScriptExecutor().isRunning()) {
                        script.stop();
                    }
                    stop = true;
                    setVisible(false);
                    dispose();
                }
            });
        } catch (Exception e) {
            System.out.println("Error in GUI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Runnable gui = new Runnable() {
        public void run() {
            System.out.println("Starting gui in new runnable...");
            setTitle(script != null ? script.getName() : "Test Probably");
            setContentPane(panel);
            //setResizable(false);
            setSize(panel.getPreferredSize());
            pack();
            loadSettings();
            setLocationRelativeTo(null);
            setVisible(true);
        }
    };

    public String getSettingsFile() {
        return this.settingsFolder + "settingsFolder.properties";
    }

    public abstract void loadSettings();

    public abstract void saveSettings();
}
