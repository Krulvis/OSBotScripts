package staker.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import org.osbot.rs07.utility.Condition;
import staker.Staker;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Second extends ATState<Staker> {

    public Second(Staker script) {
        super("Second", script);
    }

    private Timer tooLowTimer;

    @Override
    public int perform() throws InterruptedException {
        if (script.currentDuel != null) {

            final int otherExact = stake.otherOfferedAmount();
            boolean tooLow = otherExact < script.minAmount;
            boolean tooHigh = otherExact > script.maxAmount;

            script.currentDuel.setOtherExact(tooHigh ? script.maxAmount : otherExact);
            script.currentDuel.calculateMyOffer(script.returnPercent, script.equalOfferAtHighOdds);

            final int shouldOffer = script.currentDuel.getMyRoundedMultiplied();
            final int currentOffer = stake.myOfferedAmount();
            System.out.println("Other offer: " + otherExact + ", Should offer: " + shouldOffer + ", Curr Offer: " + currentOffer);
            if (shouldOffer > 0) {
                if (tooLow) {
                    if (tooLowTimer == null) {
                        tooLowTimer = new Timer(Random.nextGaussian(10000, 20000, 5000));
                    } else if (tooLowTimer.isFinished()) {
                        sleep(random(1000, 4000));
                        log("Declined stake since opponents offer was too low");
                        if (stake.declineSecond()) {
                            waitFor(2000, new Condition() {
                                @Override
                                public boolean evaluate() {
                                    return !stake.isSecondScreenOpen();
                                }
                            });
                        }
                    }
                } else {
                    tooLowTimer = null;
                }

                if (currentOffer != shouldOffer) {
                    if (shouldOffer < currentOffer) {
                        if (stake.remove(currentOffer - shouldOffer)) {
                            waitFor(3000, new Condition() {
                                @Override
                                public boolean evaluate() {
                                    return stake.myOfferedAmount() == shouldOffer;
                                }
                            });
                        }
                    } else if (shouldOffer > currentOffer) {
                        if (stake.offer(shouldOffer - currentOffer)) {
                            waitFor(2000, new Condition() {
                                @Override
                                public boolean evaluate() {
                                    return stake.myOfferedAmount() == shouldOffer;
                                }
                            });
                        }
                    }
                } else if (!stake.isSecondScreenAccepted() && stake.acceptSecondScreen()) {
                    waitFor(10000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return otherExact != stake.otherOfferedAmount() || stake.isThirdScreenOpen();
                        }
                    });
                }
            } else {
                tooLowTimer = null;
            }
        }
        return Random.smallSleep();
    }

    @Override
    public boolean validate() {
        return stake.isSecondScreenOpen();
    }
}
