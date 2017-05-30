package staker.util;

import api.util.gui.GUIWrapper;
import org.osbot.rs07.api.def.ItemDefinition;
import staker.Staker;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

/**
 * Created by Krulvis on 29-May-17.
 */
public class GUI extends GUIWrapper<Staker> {

    public GUI(Staker s) {
        super(s);
        rootPanel.setPreferredSize(new Dimension(465, 520));
        construct(rootPanel);
        declineSlider.setMinimum(30);
        declineSlider.setMaximum(200);
        declineSlider.setMinorTickSpacing(20);
        declineSlider.setPaintLabels(true);
        declineSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                declineTimerValueField.setText("" + declineSlider.getValue());
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                script.autoChatMessage = messageField.getText();
                //script.showFriendsMM = showFriendsOnMMCheckBox.isSelected();
                //StakeSettings.walkBack = walkBackToStartCheckBox.isSelected();
                script.trayMessage = trayMessageCheckBox.isSelected();
                script.declineTime = declineSlider.getValue() * 1000;
                String ignoreItems = ignoreItemTextArea.getText();
                if (ignoreItems != null && ignoreItems.length() > 0) {
                    String[] items = ignoreItemTextArea.getText().replaceAll(" ", "").split(",");
                    for (String item : items) {
                        try {
                            int i = Integer.parseInt(item);
                            script.stake.ignoreList.add(i);
                            ItemDefinition def = ItemDefinition.forId(i);
                            if (def != null && i != def.getNotedId()) {
                                //Add noted id as well
                                script.stake.ignoreList.add(i + 1);
                            }
                        } catch (NumberFormatException ie) {
                            System.out.println("Error parsing item id: " + item);
                            script.log("Don't use anything except for numbers split with a ,");
                            return;
                        }
                    }
                }
                /*if (useWebAPICheckBox.isSelected() && botPanel.isVisible()) {
                    script.startWebController();
                }
                if (automateStakesCheckBox.isSelected()) {
                    StakeSettings.automatic = true;
                    StakeSettings.walkBack = true;
                    script.ruleSet = RuleSet.VINE_WHIP;
                }*/
                script.minAmount = getInt(minField);
                script.maxAmount = getInt(maxField);
                script.returnPercent = getInt(returnPercentField);
                script.equalOfferAtHighOdds = equalOfferCheckBox.isSelected();
                save();
                startScript();
            }
        });
        load();
        setVisible(true);
    }

    @Override
    public void loadSettings() {

    }

    @Override
    public void saveSettings() {

    }

    private void save() {
        String file = getSettingsFile();
        try {
            Properties p = new Properties();
            p.setProperty("message", messageField.getText());
            p.setProperty("show_friends", String.valueOf(showFriendsOnMMCheckBox.isSelected()));
            p.setProperty("walk_back", String.valueOf(walkBackToStartCheckBox.isSelected()));
            p.setProperty("tray", String.valueOf(trayMessageCheckBox.isSelected()));
            p.setProperty("decline_timer", String.valueOf(declineSlider.getValue()));
            p.setProperty("equal_offer", String.valueOf(equalOfferCheckBox.isSelected()));
            p.setProperty("min", minField.getText());
            p.setProperty("max", maxField.getText());
            p.setProperty("return", returnPercentField.getText());
            p.setProperty("ignore_list", ignoreItemTextArea.getText().replaceAll(" ", ""));
            p.setProperty("use_webapi", String.valueOf(useWebAPICheckBox.isSelected()));
            p.store(new FileWriter(new File(file)), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        String file = getSettingsFile();
        try {
            Properties p = new Properties();
            p.load(new FileReader(new File(file)));
            useWebAPICheckBox.setSelected(Boolean.parseBoolean(p.getProperty("use_webapi", "false")));
            showFriendsOnMMCheckBox.setSelected(Boolean.parseBoolean(p.getProperty("show_friends", "false")));
            walkBackToStartCheckBox.setSelected(Boolean.parseBoolean(p.getProperty("walk_back", "false")));
            trayMessageCheckBox.setSelected(Boolean.parseBoolean(p.getProperty("tray", "false")));
            equalOfferCheckBox.setSelected(Boolean.parseBoolean(p.getProperty("equal_offer", "false")));
            messageField.setText(p.getProperty("message", ""));
            minField.setText(p.getProperty("min", "1"));
            maxField.setText(p.getProperty("max", "20000"));
            returnPercentField.setText(p.getProperty("return", "55"));
            declineSlider.setValue(Integer.parseInt(p.getProperty("decline_timer", "60")));
            declineTimerValueField.setText(p.getProperty("decline_timer", "60"));
            ignoreItemTextArea.setText(p.getProperty("ignore_list", ""));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getInt(JTextField field) {
        int amount = 0;
        try {
            amount = Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            script.log("Please type full numbers only!");
            System.out.println("Input a number in the fields");
        }
        return amount;
    }

    public static void main(String... args) {
        new GUI(null);
    }

    private JPanel rootPanel;
    private JTextField messageField;
    private JSlider speedSlider;
    private JButton startButton;
    private JCheckBox showFriendsOnMMCheckBox;
    private JCheckBox walkBackToStartCheckBox;
    private JCheckBox trayMessageCheckBox;
    private JTextField minField;
    private JTextField maxField;
    private JTextField returnPercentField;
    private JSlider declineSlider;
    private JCheckBox equalOfferCheckBox;
    private JTextField declineTimerValueField;
    private JPanel automaticPanel;
    private JPanel ignoreItemPanel;
    private JTextArea ignoreItemTextArea;
    ;
    private JPanel botPanel;
    private JCheckBox useWebAPICheckBox;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridBagLayout());
        rootPanel.setPreferredSize(new Dimension(263, 150));
        rootPanel.setRequestFocusEnabled(true);
        final JLabel label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, 28));
        label1.setForeground(new Color(-4516587));
        label1.setText("Stake Accepter");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        rootPanel.add(label1, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        rootPanel.add(spacer1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Message:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        rootPanel.add(label2, gbc);
        messageField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        rootPanel.add(messageField, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Speed:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        rootPanel.add(label3, gbc);
        speedSlider = new JSlider();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        rootPanel.add(speedSlider, gbc);
        startButton = new JButton();
        startButton.setText("Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        rootPanel.add(startButton, gbc);
        showFriendsOnMMCheckBox = new JCheckBox();
        showFriendsOnMMCheckBox.setText("Show Friends On MM");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        rootPanel.add(showFriendsOnMMCheckBox, gbc);
        walkBackToStartCheckBox = new JCheckBox();
        walkBackToStartCheckBox.setText("Walk Back to Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        rootPanel.add(walkBackToStartCheckBox, gbc);
        trayMessageCheckBox = new JCheckBox();
        trayMessageCheckBox.setText("Tray Message");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        rootPanel.add(trayMessageCheckBox, gbc);
        automaticPanel = new JPanel();
        automaticPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(automaticPanel, gbc);
        automaticPanel.setBorder(BorderFactory.createTitledBorder("Automation"));
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        automaticPanel.add(spacer2, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Min Amount:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        automaticPanel.add(label4, gbc);
        minField = new JTextField();
        minField.setText("1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        automaticPanel.add(minField, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Max Amount:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        automaticPanel.add(label5, gbc);
        maxField = new JTextField();
        maxField.setText("2000000");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        automaticPanel.add(maxField, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Return %");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        automaticPanel.add(label6, gbc);
        returnPercentField = new JTextField();
        returnPercentField.setText("55");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        automaticPanel.add(returnPercentField, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Decline Timer:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        automaticPanel.add(label7, gbc);
        declineSlider = new JSlider();
        declineSlider.setPaintLabels(true);
        declineSlider.setPaintTicks(true);
        declineSlider.setPaintTrack(true);
        declineSlider.setSnapToTicks(true);
        declineSlider.setValue(100);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        automaticPanel.add(declineSlider, gbc);
        declineTimerValueField = new JTextField();
        declineTimerValueField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        automaticPanel.add(declineTimerValueField, gbc);
        equalOfferCheckBox = new JCheckBox();
        equalOfferCheckBox.setText("Equal Offer when Odds High");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        rootPanel.add(equalOfferCheckBox, gbc);
        ignoreItemPanel = new JPanel();
        ignoreItemPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(ignoreItemPanel, gbc);
        ignoreItemPanel.setBorder(BorderFactory.createTitledBorder("Ignore Items"));
        ignoreItemTextArea = new JTextArea();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 300;
        gbc.ipady = 100;
        ignoreItemPanel.add(ignoreItemTextArea, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ignoreItemPanel.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        ignoreItemPanel.add(spacer4, gbc);
        botPanel = new JPanel();
        botPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(botPanel, gbc);
        botPanel.setBorder(BorderFactory.createTitledBorder("Bot Control Panel"));
        useWebAPICheckBox = new JCheckBox();
        useWebAPICheckBox.setText("Use Web API");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        botPanel.add(useWebAPICheckBox, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
