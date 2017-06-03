package api.util.antiban;

import api.util.Timer;
import org.osbot.rs07.api.Keyboard;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tony on 2/06/2017.
 */
abstract public class ChatHandler {

    private boolean processed = false;
    private boolean willprocess;
    private double processOdds;
    private String[] messages;
    private Timer timer;

    public ChatHandler() {
        this.messages = this.setMessages();
        this.processOdds = this.setProcessOdds();
        this.calculateWillProcess();
    }

    public void sayMessage(Keyboard keyboard) {
        if (!isProcessed() && this.willprocess) {
            if (this.timer == null) {
                this.timer = this.setTimer();
            }
            if (this.timer != null && this.timer.isFinished()) {
                this.setProcessed();
                keyboard.typeString(this.chooseRandomMessage(), true);
            }
        }
    }

    private String chooseRandomMessage() {
        int randInt = ThreadLocalRandom.current().nextInt(0, this.messages.length);
        return this.messages[randInt];
    }

    public boolean isProcessed() {
        return this.processed;
    }

    public boolean willProcess() {
        return this.willprocess;
    }

    private void setProcessed() {
        this.processed = true;
    }

    public void reset() {
        this.processed = false;
        this.calculateWillProcess();
    }

    public void overrideprocessOdds(double odds) {
        this.processOdds = odds;
        this.calculateWillProcess();
    }

    protected void calculateWillProcess() {
        double odds = this.processOdds * 100;
        int randInt = ThreadLocalRandom.current().nextInt(0, 100);
        this.willprocess = randInt < odds;
    }

    abstract protected double setProcessOdds();

    abstract protected String[] setMessages();

    abstract protected Timer setTimer();

}
