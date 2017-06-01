package api.event.random;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.osbot.rs07.api.Client;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.constants.ResponseCode;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.listener.LoginResponseCodeListener;
import org.osbot.rs07.utility.Condition;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Krulvis on 29-May-17.
 */
public class LoginHandler extends ATState<RandomHandler> implements LoginResponseCodeListener {

    public LoginHandler(RandomHandler script) {
        super("Login Handler", script);
    }

    private Timer tooManyLogins;
    private int membershipDaysLeft = -1;
    public boolean checkedPid = false;

    @Override
    public void onResponseCode(int i) throws InterruptedException {
        if (ResponseCode.isDisabledError(i)) {
            System.out.println("Account disabled :(");
            webAPI.updateBanned();
            webAPI.setCurrentAccount(null);
        }
        if (ResponseCode.isConnectionError(i)) {
            tooManyLogins = new Timer(Random.nextGaussian(5000, 15000, 5000));
        }
    }

    @Override
    public int perform() throws InterruptedException {
        final RS2Widget clickNextWidget = widgets.get(378, 0);
        if (validateWidget(clickNextWidget)) {
            sendMemberShipInfo();
            System.out.println("Clicking 'Click Here'");
            final RS2Widget button = getWidgetChild(378, new Filter<RS2Widget>() {
                @Override
                public boolean match(RS2Widget widget) {
                    return widget.getMessage().contains("CLICK HERE");
                }
            });
            if (validateWidget(button) && button.interact()) {
                waitFor(3000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return !validateWidget(button);
                    }
                });
                return -1;
            }
        } else {
            Account account = script.getAccount();
            if (account != null && webAPI.isActive()) {
                checkedPid = false;
                int uiState = client.getLoginUIState();
                if (uiState == 0) {
                    System.out.println("Clicking 'Existing User'");
                    if (mouse.click(new RectangleDestination(bot, 395, 275, 130, 30))) {
                        sleep(Random.nextGaussian(200, 1000, 250));
                    }
                }
                uiState = client.getLoginUIState();
                if (uiState == 2 && !needsToWait()) {
                    System.out.println("Typing information");
                    if (hasUsernameTyped()) {
                        mouse.click(new RectangleDestination(bot, 315, 248, 35, 12));
                        backSpace(true);
                    }
                    keyboard.typeString(account.getUsername(), true);
                    sleep(Random.nextGaussian(250, 500, 200));
                    if (hasPasswordTyped()) {
                        backSpace(false);
                    }
                    if (keyboard.typeString(account.getPassword(), true)) {
                        sleep(Random.nextGaussian(2000, 3000, 1000));
                        waitFor(2000, new Condition() {
                            @Override
                            public boolean evaluate() {
                                return canClickContinue();
                            }
                        });
                    }
                } else if (uiState == 3) {
                    System.out.println("Invalid login, Try Again");
                    mouse.click(new RectangleDestination(bot, 320, 260, 120, 30));
                    sleep(Random.nextGaussian(500, 1000, 250));
                }
            } else if (account == null) {
                System.out.println("Account is null in loginHandler, getting new one from WebAPI");
                webAPI.getNewAccount();
                return Random.nextGaussian(5000, 15000, 5000);
            }
        }
        //System.out.println("StageValue: " + client.getLoginStageValue());
        //System.out.println("UI State: " + client.getLoginUIState());
        return Random.medSleep();
    }

    private boolean needsToWait() {
        return tooManyLogins != null && !tooManyLogins.isFinished();
    }

    @Override
    public boolean validate() {
        return client.getLoginState() == Client.LoginState.LOGGED_OUT || canClickContinue();
    }

    public boolean canClickContinue() {
        final RS2Widget clickNextWidget = widgets.get(378, 0);
        if (validateWidget(clickNextWidget)) {
            final RS2Widget button = getWidgetChild(378, new Filter<RS2Widget>() {
                @Override
                public boolean match(RS2Widget widget) {
                    return widget.getMessage().contains("CLICK HERE");
                }
            });
            return button != null && button.isVisible();
        }
        return false;
    }

    private boolean hasPasswordTyped() {
        Point passPoint = new Point(349, 272);
        Color passColor = colorPicker.colorAt(passPoint);
        return isLetter(passColor);
    }

    private boolean hasUsernameTyped() {
        Point p = new Point(316, 260);
        Point p2 = new Point(318, 261);
        Color c = colorPicker.colorAt(p);
        Color c2 = colorPicker.colorAt(p2);
        return isLetter(c, c2);
    }

    private boolean isLetter(Color... colors) {
        for (Color c : colors) {
            if (c.equals(Color.WHITE) || c.equals(Color.BLACK)) {
                return true;
            }
        }
        return false;
    }

    private void sendMemberShipInfo() {
        RS2Widget membership = getWidgetChild(378, new Filter<RS2Widget>() {
            @Override
            public boolean match(RS2Widget widget) {
                return widget != null && widget.getMessage().toLowerCase().contains("member");
            }
        });
        if (membership != null) {
            boolean isntMember = membership.getMessage().contains("You are not a member");
            if (isntMember) {
                membershipDaysLeft = 0;
                System.out.println("MemberdaysLeft: " + membershipDaysLeft);
            } else {
                String text = membership.getMessage();
                String days = text.substring(text.indexOf("You have ") + 9, text.indexOf("day"))
                        .replaceAll("<[^>]*>", "");
                membershipDaysLeft = Integer.parseInt(days.replaceAll(" ", ""));
                System.out.println("Membership Days Left: " + membershipDaysLeft);
            }
            if (webAPI.isConnected() && membershipDaysLeft > -1) {
                JsonObject obj = new JsonObject();
                obj.add("days_remaining", new JsonPrimitive(membershipDaysLeft));
                webAPI.getWebConnection().sendJSON("bot/membership", "PUT", obj);
            }
        }
    }

    public boolean isMember() {
        return membershipDaysLeft > 0;
    }

    public int getMembershipDaysLeft() {
        return membershipDaysLeft;
    }

    public void backSpace(boolean username) throws InterruptedException {
        Timer backSpaceTimer = new Timer(Random.nextGaussian(3000, 5000, 1000));
        keyboard.pressKey((char) KeyEvent.VK_BACK_SPACE);
        while (!backSpaceTimer.isFinished() || (username ? hasUsernameTyped() : hasPasswordTyped())) {
            sleep(Random.nextGaussian(100, 250, 50));
            keyboard.pressKey((char) KeyEvent.VK_BACK_SPACE);
        }
        keyboard.releaseKey((char) KeyEvent.VK_BACK_SPACE);
    }


}
