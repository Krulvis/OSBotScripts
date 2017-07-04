package api.util.antiban.chats;

import api.util.Random;
import api.util.Timer;
import api.util.antiban.ChatHandler;

/**
 * Created by Tony on 2/06/2017.
 */
public class StartFightChat extends ChatHandler {

    @Override
    protected Timer setTimer() {
        return new Timer(Random.nextGaussian(1000, 1500, 500));
    }

    @Override
    protected String[] setMessages() {

        return new String[]
                {"gl", "glgl", "gl mate", "good luck", "gg", "aye",
                        "hope I win", "there we go", "gl bro", "stank and tank",
                        "lets win", "cmon streak", "hit hard", "gl hf",
                        "have fun", "lets hit", "hit hard plz", "gl dude",
                        "good luck", "lets go", "yolo", "gl bank",
                        "losing bank today", "cmone rng gods"};
    }

    protected double setProcessOdds() {
        return 0.1;
    }
}
