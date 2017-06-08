package staker.states;

import api.ATState;
import api.util.Random;
import api.util.Timer;
import api.wrappers.staking.data.RuleSet;
import api.wrappers.staking.data.Settings;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.utility.Condition;
import staker.Staker;

import java.awt.*;

import static api.util.Random.nextGaussian;
import static api.wrappers.staking.data.Data.ARENA_AREA;

public class Fight extends ATState<Staker> {

    public Fight(Staker script) {
        super("Fight", script);
    }

    private Player opponent;
    public boolean canAttackPlayer = false;

    @Override
    public int perform() throws InterruptedException {
        script.endFight.challengeAgain = new Timer(30000);
        if (script.currentDuel != null) {
            script.currentDuel.getStartFightChat().sayMessage(keyboard);
            opponent = getPlayer(script.currentDuel.getPlayerName());
            if (opponent != null) {
                if (!opponent.isHitBarVisible()) {
                    script.currentDuel.resetFightTimer();
                }
                if (!script.currentDuel.isFinished() && ((opponent.isHitBarVisible() && opponent.getHealthPercent() == 0) || currentHealth() == 0)) {
                    System.out.println("Done fighting, " + (currentHealth() == 0 ? "I am" : "enemy is") + " dead");
                    script.currentDuel.setWon(opponent.getHealthPercent() == 0);
                    script.currentDuel.setFinished(true);
                    script.currentDuel.stopFightTimer();
                    if (!script.currentDuel.hasSendResults()) {
                        script.currentDuel.sendResults(webAPI);
                    }
                    openInventory();
                    script.resetValues();
                } else if (!script.currentDuel.isFinished()) {
                    fight(script.ruleSet);
                }
            }
        }
        return Random.smallSleep();
    }

    public boolean fight(final RuleSet mode) {
        if (mode == RuleSet.DDS && atCombat.getSpecialAttack() >= 25) {
            if (!Settings.Weapon.DRAGON_DAGGER.isWearing(this)) {
                openInventory();
                if (Settings.Weapon.DRAGON_DAGGER.equip(this)) {
                    waitFor(1000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return Settings.Weapon.DRAGON_DAGGER.isWearing(script);
                        }
                    });
                }
            } else if (tabs.getOpen() != Tab.ATTACK) {
                if (tabs.open(Tab.ATTACK)) {
                    waitFor(1000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return tabs.getOpen() == Tab.ATTACK;
                        }
                    });
                }
            }
            if (Settings.Weapon.DRAGON_DAGGER.isWearing(this) && tabs.getOpen() == Tab.ATTACK) {
                if (!combat.isSpecialActivated()) {
                    combat.toggleSpecialAttack(true);
                    waitFor(1000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return combat.isSpecialActivated();
                        }
                    });
                }
                if (combat.isSpecialActivated()) {
                    return attackOpponent();
                }
            }
        } else {
            if (!mode.getWeapon().isWearing(this)) {
                if (mode.getWeapon().equip(this)) {
                    waitFor(2000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return mode.getWeapon().isWearing(script);
                        }
                    });
                }
            }
            //System.out.println("Accking opponent");
            return attackOpponent();
        }
        return false;
    }

    public boolean attackOpponent() {
        if (!canAttackPlayer) {
            if (checkAttackThread == null || !checkAttackThread.isAlive()) {
                checkAttackThread = CheckAttackThread();
                checkAttackThread.start();
            }
        } else {
            if (camera.getPitchAngle() < 93) {
                camera.movePitch(random(94, 99));
            }
            Character<?> check = myPlayer().getInteracting();
            if (check == null || !check.getName().equalsIgnoreCase(opponent.getName()) || !check.isHitBarVisible()) {
                if (opponent.getPosition().isVisible(bot)) {
                    Rectangle target = stake.getCenterPoint(opponent);
                    //System.out.println("Clicking target");
                    if (target != null && interact.interact(new RectangleDestination(bot, target), "Fight", opponent.getName(), false)) {
                        waitFor(100, new Condition() {
                            @Override
                            public boolean evaluate() {
                                final Character check = myPlayer().getInteracting();
                                return check != null && check.getName().equalsIgnoreCase(opponent.getName());
                            }
                        });
                    }
                } else {
                    camera.toPosition(opponent.getPosition());
                }
            }
            final Character opp = myPlayer().getInteracting();
            return opp != null && opp.getName().equalsIgnoreCase(opponent.getName());
        }
        return false;
    }

    public Thread checkAttackThread = null;

    public final Thread CheckAttackThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Timer attackTimer = new Timer(2000);
                while (!attackTimer.isFinished()) {
                    try {
                        if (opponent != null) {
                            String text = opponent.getHeadMessage();
                            if (text.equals("1") || text.equals("2") || text.toUpperCase().contains("FIGHT")) {
                                break;
                            }
                        }
                        Thread.sleep(nextGaussian(50, 75, 5));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                canAttackPlayer = true;
            }
        }, "CanAttackPlayer Thread");
    }

    @Override
    public boolean validate() {
        return ARENA_AREA.contains(myPosition());
    }
}
