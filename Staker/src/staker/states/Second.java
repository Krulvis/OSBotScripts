package staker.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import org.osbot.rs07.utility.Condition;
import staker.Staker;
import staker.util.antiban.delays.*;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Second extends ATState<Staker> {

    public Second(Staker script) {
        super("Second", script);
    }

    public Timer tooLowTimer;
    boolean tooLow = false;
    int shouldOffer = 0;
    int currentOffer = 0;
    int otherExact;

    @Override
    public int perform() throws InterruptedException {
        if (script.currentDuel != null) {
            calculateOffers();
            System.out.println("Other offer: " + otherExact + ", Should offer: " + shouldOffer + ", Curr Offer: " + currentOffer);
            if (script.currentDuel.shouldDecline()) {
                log("Opponent took to long to offer 2nd duel");
                DeclineSecondDuelScreenDelay.execute();
                script.currentDuel.setCancelReason("took_too_long_2nd");
                if (stake.declineSecond()) {
                    waitFor(2000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return !stake.isSecondScreenOpen();
                        }
                    });
                }
            } else if (otherExact > 0) {
                if (tooLow) {
                    if (tooLowTimer == null) {
                        tooLowTimer = new Timer(Random.nextGaussian(5000, 10000, 5000));
                    } else if (tooLowTimer.isFinished()) {
                        DeclineSecondDuelScreenDelay.execute();
                        log("Declined stake since opponents offer was too low");
                        script.currentDuel.setCancelReason("offer_too_low_2nd");
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
                    if (currentOffer != shouldOffer && (shouldOffer < currentOffer || inventory.contains(995))) {
                        if (shouldOffer < currentOffer) {
                            RemoveBetDelay.execute();
                            if (!calculateOffers() && stake.remove(currentOffer - shouldOffer)) {
                                waitFor(3000, new Condition() {
                                    @Override
                                    public boolean evaluate() {
                                        return calculateOffers();
                                    }
                                });
                            }
                        } else if (shouldOffer > currentOffer) {
                            AddBetDelay.execute();
                            if (!calculateOffers() && stake.offer(shouldOffer - currentOffer)) {
                                waitFor(2000, new Condition() {
                                    @Override
                                    public boolean evaluate() {
                                        return stake.myOfferedAmount() == shouldOffer;
                                    }
                                });
                            }
                        }
                    } else if (!stake.isSecondScreenAccepted()) {
                        AcceptSecondDuelScreenDelay.execute();
                        if (stake.acceptSecondScreen()) {
                            waitFor(10000, new Condition() {
                                @Override
                                public boolean evaluate() {
                                    return otherExact != stake.otherOfferedAmount() || stake.isThirdScreenOpen();
                                }
                            });
                        }
                    }
                }
            } else {
                tooLowTimer = null;
            }
        } else if (stake.declineSecond()) {
            waitFor(2000, new Condition() {
                @Override
                public boolean evaluate() {
                    return !stake.isSecondScreenOpen();
                }
            });
        }
        return Random.medSleep();
    }

    public boolean calculateOffers() {
        otherExact = stake.otherOfferedAmount();
        tooLow = otherExact < script.minAmount;

        script.currentDuel.setOtherExact(otherExact);
        script.currentDuel.setOtherItems(stake.otherOfferedItems());
        script.currentDuel.calculateMyOffer(script.equalOfferAtHighOdds, script.maxAmount);

        shouldOffer = script.currentDuel.getMyRoundedMultiplied();
        currentOffer = stake.myOfferedAmount();

        return shouldOffer == currentOffer;
    }

    @Override
    public boolean validate() {
        return stake.isSecondScreenOpen();
    }
}
