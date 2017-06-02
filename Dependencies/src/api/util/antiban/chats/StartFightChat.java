package api.util.antiban.chats;

import api.util.Random;
import api.util.Timer;
import api.util.antiban.ChatHandler;
import org.osbot.rs07.api.Keyboard;

/**
 * Created by Tony on 2/06/2017.
 */
public class StartFightChat extends ChatHandler {

    public StartFightChat(Keyboard keyboard) {
        super(keyboard);
    }

    @Override
    protected Timer setTimer() {
        return new Timer(Random.nextGaussian(1000, 1500, 500));
    }

    @Override
    protected String[] setMessages() {
        return new String[]{"gl", "glgl", "gl mate", "good luck", "gg", "aye"};
    }

    protected double setProcessOdds(){
        return 0.2;
    }
}
