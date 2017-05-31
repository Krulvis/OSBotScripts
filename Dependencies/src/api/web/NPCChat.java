package api.web;

import api.ATMethodProvider;
import api.util.Random;
import org.osbot.rs07.api.ui.RS2Widget;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by Krulvis on 14-Mar-17.
 */
public class NPCChat {

    /**
     * Widget ID for NPC Chat with 2 options.
     */
    // public static final int TWO_OPTION_WIDGET_2_ID = 219;
    /**
     * Widget ID for NPC Chat with 2 options.
     */
    // public static final int TWO_OPTION_WIDGET_ID = 219;
    /**
     * Widget ID for NPC Chat with 2 options, type 2, I have no idea why there
     * are 2
     */
    // public static final int TWO_OPTION_WIDGET_ID_2 = 229;
    /**
     * Widget ID for NPC Chat with 3 options.
     */
    // public static final int THREE_OPTION_WIDGET_ID = 230;
    /**
     * Widget ID for NPC Chat with 4 options.
     */
    // public static final int FOUR_OPTION_WIDGET_ID = 232;
    /**
     * Widget ID for NPC Chat with 5 options.
     */
    // public static final int FIVE_OPTION_WIDGET_ID = 234;
    /**
     * Array of all three NPC Chat Widget IDs.
     */
    // public static final int FIVE_OPTION_WIDGET_ID = 234;
    // private static final int[] ALL_CHAT_WIDGETS = { TWO_OPTION_WIDGET_ID,
    // THREE_OPTION_WIDGET_ID, FOUR_OPTION_WIDGET_ID, FIVE_OPTION_WIDGET_ID,
    // TWO_OPTION_WIDGET_ID_2 };
    public static final int OPTION_WIDGET_ID = 219;
    public static final int OPTION_WIDGET_CHILD_ID = 0;
    // private static final int[] CHAT_WIDGET_OPTION_COUNT = { 2, 3, 4, 5, 2 };

    private final String[] options;

    private final boolean clickContinue;
    private boolean keyboard = false;
    private ATMethodProvider mp;

    /**
     * @param clickContinue
     *            Boolean value indicating whether the client should click,
     *            "Click to continue".
     * @param options
     *            Array of type String containing all possible options that the
     *            Bot filters through to execute if they match the NPC chat
     *            options.
     */
    public NPCChat(ATMethodProvider mp, boolean clickContinue, String... options) {
        this.mp = mp;
        this.options = options;
        this.clickContinue = clickContinue;
    }

    /**
     *
     * @param keyboard
     *            Boolean value indicating whether the client should use
     *            keyboard actions.
     * @param clickContinue
     *            Boolean value indicating whether the client should click,
     *            "Click to continue".
     * @param options
     *            Array of type String containing all possible options that the
     *            Bot filters through to execute if they match the NPC chat
     *            options.
     */
    public NPCChat(ATMethodProvider mp, boolean keyboard, boolean clickContinue, String... options) {
        this.mp = mp;
        this.keyboard = keyboard;
        this.options = options;
        this.clickContinue = clickContinue;
    }

    /**
     * Getter method for options that has been passed in the constructor.
     *
     * @return options.
     */
    public String[] getChatOptions() {
        return options;
    }

    /**
     * Getter method for clickContinue that has been passed in the constructor.
     *
     * @return clickContinue
     */
    public boolean doesClickContinue() {
        return clickContinue;
    }

    /**
     * This method will check various chat conditions and execute based on the
     * priority of the chat interface situation.
     *
     *
     * If the NPC chat is open and "Click to continue" is ready to be clicked,
     * the client will click it. If, however, the "Click to continue" is not
     * present and instead you are faced with multiple chat options, the client
     * will perform through the list of options you passed in the constructor and
     * execute the first option that matches with the NPC option.
     *
     *
     * @see
     * @return True if any chat option including "Click to continue" is clicked.
     *         False if no "Click to continue" is available and none of the
     *         passed options matched the NPC chat's available options.
     */
    public boolean execute() {
        if (doesClickContinue() && canContinue(mp)) {
            if (!keyboard) {
                return clickContinue(mp);
            } else {
                mp.keyboard.typeKey((char) KeyEvent.VK_SPACE);
                mp.sleep(100, 350);
                return true;
            }
        }
        if (isChatOpen(mp)) {
            for (String s : options) {
                if (keyboard ? selectOptionExactKeyboard(mp, s) : selectOptionExact(mp, s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether NPC chat interface is currently active.
     *
     * @return True if NPC chat interface is open; False if otherwise.
     */
    public boolean isActive() {
        if (doesClickContinue()) {
            return isChatOpen(mp);
        }
        return isChatOptionsOpen(mp);
    }

    /**
     * Checks whether the user enabled keyboard solving.
     *
     * @return True if the npc chat is set to use the keyboard.
     */
    public boolean isUsingKeyboard() {
        return keyboard;
    }

    /**
     *  Sets whether or not to use the keyboard in npcchat class.
     * @param keyboard
     */
    public void setKeyboard(final boolean keyboard) {
        this.keyboard = keyboard;
    }

    /**
     * Returns whether or not any of the NPC Chat widgets are open.
     *
     * @return Boolean value indicating whether any of the NPC Chat widgets are
     *         open.
     */
    public static boolean isChatOptionsOpen(ATMethodProvider mp) {
        final RS2Widget chatWidget = mp.widgets.get(OPTION_WIDGET_ID, OPTION_WIDGET_CHILD_ID);
        return chatWidget != null && chatWidget.isVisible();
    }

    public static boolean isChatOpen(ATMethodProvider mp) {
        return canContinue(mp) || isChatOptionsOpen(mp);
    }

    /**
     * Obtains the number of chat options available.
     *
     * <p>
     * Note: Chat interface must already be open when calling this method. If
     * the chat is not open it will return 0 by default.
     *
     * @return The number of chat options available in the current chat
     *         interface.
     */
    public static int getOptionCount(ATMethodProvider mp) {
        if (isChatOptionsOpen(mp)) {
            final RS2Widget chatWidget = mp.widgets.get(OPTION_WIDGET_ID, OPTION_WIDGET_CHILD_ID);
            if (chatWidget == null || !chatWidget.isVisible()) {
                return 0;
            }
            return chatWidget.getChildWidgets().length - 3;
        }
        return 0;
    }

    /**
     * Clicks an option that contains the specified text.
     *
     * @param option
     *            The text of the option to click.
     * @return Boolean value indicating whether or not the specified option was
     *         clicked.
     */
    public static boolean selectOption(ATMethodProvider mp, String option) {
        RS2Widget npcChatWidget = getNPCChatWidget(mp);
        if (npcChatWidget != null) {
            for (RS2Widget inter : npcChatWidget.getChildWidgets()) {
                if (inter.getMessage().contains(option)) {
                    return mp.clickWidget(inter);
                }
            }
        }
        return false;
    }

    /**
     * Clicks an option that equals the specified text.
     *
     * @param option
     *            The text of the option to click.
     * @return Boolean value indicating whether or not the specified option was
     *         clicked.
     */
    public static boolean selectOptionExact(ATMethodProvider mp, String option) {
        RS2Widget npcChatWidget = getNPCChatWidget(mp);
        if (npcChatWidget != null) {
            for (RS2Widget inter : npcChatWidget.getChildWidgets()) {
                if (inter.getMessage().equalsIgnoreCase(option)) {
                    return mp.clickWidget(inter);
                }
            }
        }
        return false;
    }

    public static boolean selectOptionExactKeyboard(ATMethodProvider mp, String option) {
        RS2Widget npcChatWidget = getNPCChatWidget(mp);
        if (npcChatWidget != null) {
            int keyboardChoice = 1;
            for (RS2Widget inter : npcChatWidget.getChildWidgets()) {
                if (inter.getMessage().equalsIgnoreCase(option)) {
                    mp.keyboard.typeString("" + keyboardChoice, false);
                    mp.sleep(100, 350);
                    return true;
                }
                if (inter.getTextColor() == 0 && inter.getMessage() != null && inter.getMessage().length() > 0)
                    keyboardChoice += 1;
            }
        }
        return false;
    }

    /**
     * Returns an array of the options' text, null safe.
     *
     * @return An array of the options' text.
     */
    public static String[] getOptions(ATMethodProvider mp) {
        RS2Widget npcChatWidget = getNPCChatWidget(mp);
        if (npcChatWidget != null) {
            ArrayList<String> options = new ArrayList<String>();
            for (RS2Widget inter : npcChatWidget.getChildWidgets()) {
                if (!inter.getMessage().equals("") && inter.getTextColor() != 0x800000) { // Red-ish
                    // color
                    options.add(inter.getMessage());
                }
            }
            return options.toArray(new String[options.size()]);
        }
        return new String[0];
    }

    /**
     * Returns the interface of the current NPC Chat.
     *
     * <p>
     * Note: This is NOT null safe.
     *
     * @return The interface of the current NPC Chat.
     */
    public static RS2Widget getNPCChatWidget(ATMethodProvider mp) {
        final RS2Widget w = mp.widgets.get(OPTION_WIDGET_ID, OPTION_WIDGET_CHILD_ID);
        if (w != null && w.isVisible()) {
            return w;
        }
        return null;
    }

    /**
     * Checks whether the current chat window allows to click to continue.
     *
     * @return True if client can click to continue, false if it requires to
     *         select a specific chat option.
     */
    public static boolean canContinue(ATMethodProvider mp) {
        return mp.dialogues.isPendingContinuation();
    }

    /**
     * Clicks the "Click to continue" in the chat interface.
     *
     * <p>
     * Note: Chat interface must be open and "Click to continue" must be an
     * available option.
     *
     * @see {@link org.tbot.methods.Widgets.clickContinue()}
     * @return True if chat is open and "Click to continue" is clicked
     *         successfully. False if otherwise.
     */
    public static boolean clickContinue(ATMethodProvider mp) {
        boolean keyboard = Random.uniform() > 0.5;
        return keyboard ? mp.keyboard.typeKey((char)KeyEvent.VK_SPACE) : mp.dialogues.clickContinue();
    }

}