package staker.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import org.osbot.rs07.utility.Condition;
import staker.Staker;
import staker.util.antiban.delays.AcceptChallengeDelay;
import staker.util.antiban.delays.AcceptSecondDuelScreenDelay;

/**
 * Created by Krulvis on 29-May-17.
 */
public class Second extends ATState<Staker> {

    public Second(Staker script) {
        super("Second", script);
    }

    public Timer tooLowTimer;

    @Override
    public int perform() throws InterruptedException {
        if (script.currentDuel != null) {
            final int otherExact = stake.otherOfferedAmount();
            boolean tooLow = otherExact < script.minAmount;

            script.currentDuel.setOtherExact(otherExact);
            script.currentDuel.setOtherItems(stake.otherOfferedItems());
            script.currentDuel.calculateMyOffer(script.equalOfferAtHighOdds, script.maxAmount);

            final int shouldOffer = script.currentDuel.getMyRoundedMultiplied();
            final int currentOffer = stake.myOfferedAmount();
            System.out.println("Other offer: " + otherExact + ", Should offer: " + shouldOffer + ", Curr Offer: " + currentOffer);
            if (script.currentDuel.shouldDecline()) {
                log("Opponent took to long to offer 2nd duel");
                //TODO Make speciel timer for declining
                AcceptChallengeDelay.execute();
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
                        //TODO ADD DELAY FOR TOO LOW OFFER
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
                    if (currentOffer != shouldOffer) {
                        if (shouldOffer < currentOffer) {
                            //TODO DELAY FOR REMOVING BET
                            if (stake.remove(currentOffer - shouldOffer)) {
                                waitFor(3000, new Condition() {
                                    @Override
                                    public boolean evaluate() {
                                        return stake.myOfferedAmount() == shouldOffer;
                                    }
                                });
                            }
                        } else if (shouldOffer > currentOffer && inventory.contains(995)) {
                            //TODO DELAY FOR ADDING BET
                            if (stake.offer(shouldOffer - currentOffer)) {
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
        }
        return Random.medSleep();
    }

    @Override
    public boolean validate() {
        return stake.isSecondScreenOpen();
    }
}
