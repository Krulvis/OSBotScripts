package api.wrappers;

import api.ATMethodProvider;
import api.util.Random;
import api.util.Timer;
import api.util.antiban.delays.ResumeAutochatDelay;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.utility.Condition;

/**
 * Created by Krulvis on 20-Apr-17.
 */
public class ATChat extends ATMethodProvider {

    private int AUTOCHAT = -1;
    private int PUBLIC = -1;
    private int TRADE = -1;
    private final int MAIN = 162;

    private Timer setMessage = null;

    public ATChat(ATMethodProvider parent) {
        init(parent);
    }

    public boolean resumeChat() {
        final RS2Widget wc = getPublicTab();
        if (validateWidget(wc)) {
            if (isAutochatEnabled()) {
                return true;
            } else if (wc.interact("Resume autochat")) {
                waitFor(new Condition() {
                    @Override
                    public boolean evaluate() {
                        return isAutochatEnabled();
                    }
                }, 2000);
            }
        }
        return wc != null && isAutochatEnabled();
    }

    public RS2Widget getMessageWidget() {
        if (AUTOCHAT == -1) {
            RS2Widget autochat = getWidgetChild(MAIN, new Filter<RS2Widget>() {
                @Override
                public boolean match(RS2Widget widgetChild) {
                    return widgetChild != null && widgetChild.getMessage().contains("Send autochat: ");
                }
            });
            if (autochat != null) {
                AUTOCHAT = autochat.getSecondLevelId();
            }
        }
        RS2Widget wc = null;
        if (AUTOCHAT > 0) {
            wc = widgets.get(MAIN, AUTOCHAT);
        }
        return (wc == null ? null : wc);
    }

    public String getMessage() {
        RS2Widget wc = getMessageWidget();
        if (wc != null) {
            String text = wc.getMessage();
            return text.replaceAll("\\<[^>]*>", "").replaceAll("Send autochat: ", "");
        } else {
            return "";
        }
    }

    public boolean isMessageSet() {
        RS2Widget wc = getMessageWidget();
        if (wc != null) {
            String mess = wc.getMessage();
            return mess != null && !mess.equals("") && getMessage() != null && !mess.contains("[off]");
        }
        return false;
    }

    public boolean setMessage(final String m, boolean slowTyping) {
        if (m == null || m.equalsIgnoreCase("")) {
            return true;
        }
        if (isMessageSet() && (getMessage().equalsIgnoreCase(m) || getMessage().equals("Enter amount:")) && isAutochatEnabled()) {
            //System.out.println("Message is already set n good.");
            return true;
        } else if (setMessage == null) {
            setMessage = new Timer(Random.nextGaussian(5000, 25000, 10000, 5000));
            System.out.println("Waiting: " + setMessage.getRemainingString() + ", before settingsFolder autochat message");
        } else if (setMessage.isFinished()) {
            System.out.println("Message set: " + isMessageSet());
            String mes = getMessage();
            System.out.println("Message same: " + (mes != null ? mes.equals(m) : "no"));
            System.out.println("Autoenabled: " + isAutochatEnabled());
            System.out.println("Current msg: " + mes + ", New msg: " + m);
            if (isMessageSet() && mes != null && mes.equalsIgnoreCase(m)) {
                if (resumeChat()) {
                    ResumeAutochatDelay.execute();
                    waitFor(2000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return isMessageSet() && getMessage().equalsIgnoreCase(m) && isAutochatEnabled();
                        }
                    });
                    return isMessageSet() && getMessage().equalsIgnoreCase(m) && isAutochatEnabled();
                }
            } else {
                //TODO TYPE AUTOCHAT MESSAGE
                RS2Widget wc = getMessageWidget();
                RS2Widget publicTab = getPublicTab();
                if (publicTab != null && (wc == null || !wc.isVisible())) {
                    boolean openAutoChat = publicTab.interact("Setup your autochat");
                    if (openAutoChat) {
                        waitFor(2000, new Condition() {
                            @Override
                            public boolean evaluate() {
                                return validateWidget(getMessageWidget());
                            }
                        });
                    }
                }
                if (validateWidget(getMessageWidget())) {
                    if (slowTyping) {
                        keyboard.typeString(m, true);
                        if (getMessage().equals(m)) {
                            setMessage = null;
                            return true;
                        }
                    } else {
                        keyboard.typeString(m, true);
                        setMessage = null;
                    }
                }
            }
        }
        return false;
    }

    public boolean isAutochatEnabled() {
        RS2Widget wc = getPublicTab();
        if (wc != null && validateWidget(wc)) {
            String[] interactions = wc.getInteractActions();
            if (interactions == null || interactions.length == 0) {
                return false;
            }
            for (String a : interactions) {
                if (a.contains("Pause")) {
                    return true;
                }
            }
        }
        return false;
    }

    public RS2Widget getTradeTab() {
        if (TRADE == -1) {
            RS2Widget wc = getWidgetChild(MAIN, new Filter<RS2Widget>() {
                @Override
                public boolean match(RS2Widget wc) {
                    String[] interactions = wc.getInteractActions();
                    if (interactions == null || interactions.length == 0) {
                        return false;
                    }
                    for (String a : interactions) {
                        if (a.contains("Trade")) {
                            return true;
                        }
                    }
                    return false;
                }
            });
            if (wc != null && wc.getSecondLevelId() > 0) {
                TRADE = wc.getSecondLevelId();
            }
        }
        return TRADE > -1 ? widgets.get(MAIN, TRADE) : null;
    }

    private RS2Widget getPublicTab() {
        if (PUBLIC == -1) {
            RS2Widget wc = getWidgetChild(MAIN, new Filter<RS2Widget>() {
                @Override
                public boolean match(RS2Widget wc) {
                    String[] interactions = wc.getInteractActions();
                    if (interactions == null || interactions.length == 0) {
                        return false;
                    }
                    for (String a : interactions) {
                        if (a.contains("Public")) {
                            return true;
                        }
                    }
                    return false;
                }
            });
            if (wc != null && wc.getSecondLevelId() > 0) {
                PUBLIC = wc.getSecondLevelId();
            }
        }
        return PUBLIC > -1 ? widgets.get(MAIN, PUBLIC) : null;
    }

}
