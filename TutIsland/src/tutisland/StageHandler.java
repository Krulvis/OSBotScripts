package tutisland;

import java.awt.event.KeyEvent;
import java.util.List;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Spells.NormalSpells;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

public class StageHandler {

	protected MethodProvider api;

	public StageHandler(MethodProvider api) {
		this.api = api;
	}

	public void handleStage(int configValue) throws InterruptedException {
		if (isPendingContinuation()) {
			clickContinue();
			sleep(300);
		}
		if (configValue > 3 && !api.getSettings().areRoofsEnabled()) {
			if (Tab.SETTINGS.isOpen(api.getBot())) {
				if (!api.getWidgets().isVisible(60)) {
					RS2Widget advanced_options = api.getWidgets().get(261, 21);
					if (advanced_options != null && advanced_options.isVisible()) {
						advanced_options.interact();
						sleep(800);
					}
				} else {
					RS2Widget hide_roofs = api.getWidgets().get(60, 8);
					if (hide_roofs != null && hide_roofs.isVisible()) {
						hide_roofs.interact();
						sleep(800);
						api.getWidgets().closeOpenInterface();
					}
				}
			} else {
				api.getTabs().open(Tab.SETTINGS);
				sleep(500);
			}
		}
		switch (configValue) {
		case 0: // Make character, Talk to 'Runescape Guide' (w/ options = "I am
				// an experienced player.")
			RS2Widget acceptButton = api.getWidgets().get(269, 99);
			if (acceptButton != null && acceptButton.isVisible()) {
				//Character.randomize(parent);
				acceptButton.interact();
				sleep(800);
			} else {
				NPC runescapeGuide = api.getNpcs().closest("RuneScape Guide");
				if (runescapeGuide != null && runescapeGuide.isVisible()) {
					if (api.getDialogues().inDialogue() && isPendingContinuation()) {
						clickContinue();
						sleep(300);
					} else if (api.getDialogues().inDialogue() && api.getDialogues().isPendingOption()) {
						api.getDialogues().selectOption("I am an experienced player.");
						sleep(300);
					} else {
						if (!isPendingContinuation()) {
							runescapeGuide.interact("Talk-to");
							sleep(800);
						}
					}
				}
			}
		case 3: // Click settingsFolder
			if (!Tab.SETTINGS.isOpen(api.getBot())) {
				api.getTabs().open(Tab.SETTINGS);
				sleep(500);
			}
			break;
		case 7: // Talk to 'Runescape Guide'
			NPC runescapeGuide = api.getNpcs().closest("RuneScape Guide");
			if (runescapeGuide != null && runescapeGuide.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						runescapeGuide.interact("Talk-to");
						sleep(800);
					}
				}
			}
			break;
		case 10: // Open door (id: 9398)
			RS2Object door = api.getObjects().closest(9398);
			if (door != null) {
				door.interact("Open");
				sleep(500);
			}
			break;
		case 20: // Walk to (3102, 3099), Talk to 'Survival Expert'
			NPC survivalExpert = api.getNpcs().closest("Survival Expert");
			if (survivalExpert != null && survivalExpert.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						survivalExpert.interact("Talk-to");
						sleep(800);
					}
				}
			} else {
				api.getWalking().walk(new Position(3103, 3096, 0));
			}
			break;
		case 30: // Open inventory
			if (!Tab.INVENTORY.isOpen(api.getBot())) {
				api.getTabs().open(Tab.INVENTORY);
				sleep(500);
			}
			break;
		case 40: // Cut down tree
			RS2Object tree = api.getObjects().closest("Tree");
			if (tree != null) {
				if (!api.myPlayer().isAnimating()) {
					tree.interact("Chop down");
					sleep(800);
				}
			}
			break;
		case 50: // Light logs
			if (isTileFree()) {
				if (!api.myPlayer().isAnimating()) {
					if (api.getInventory().isItemSelected()) {
						api.getInventory().getItem("Logs").interact();
						sleep(500);
					} else {
						api.getInventory().getItem("Tinderbox").interact();
						sleep(500);
					}
				}
			}
			break;
		case 60: // Open stats
			if (!Tab.SKILLS.isOpen(api.getBot())) {
				api.getTabs().open(Tab.SKILLS);
				sleep(500);
			}
			break;
		case 70: // Talk to 'Survival Expert'
			survivalExpert = api.getNpcs().closest("Survival Expert");
			if (survivalExpert != null) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						survivalExpert.interact("Talk-to");
						sleep(800);
					}
				}
			}
			break;
		case 80: // Catch shrimp
			NPC fishingSpot = api.getNpcs().closest("Fishing spot");
			if (fishingSpot != null) {
				if (!api.myPlayer().isAnimating()) {
					if (api.getInventory().isItemSelected()) {
						api.getInventory().deselectItem();
					} else {
						fishingSpot.interact("Net");
						sleep(1200);
					}
				}
			}
			break;
		case 90: // Cook shrimp (Go back if burnt, make fire if not there)
			if (!Tab.INVENTORY.isOpen(api.getBot())) {
				api.getTabs().open(Tab.INVENTORY);
				sleep(500);
			} else {
				RS2Object fire = api.getObjects().closest("Fire");
				if (fire != null) {
					if (!api.getInventory().isItemSelected()) {
						api.getInventory().getItem("Raw shrimps").interact();
						sleep(500);
					} else {
						if (!api.myPlayer().isAnimating()) {
							fire.interact();
							sleep(1500);
						}
					}
				} else {
					if (isTileFree()) {
						if (!api.myPlayer().isAnimating()) {
							if (api.getInventory().isItemSelected()) {
								api.getInventory().getItem("Logs").interact();
								sleep(500);
							} else {
								api.getInventory().getItem("Tinderbox").interact();
								sleep(500);
							}
						}
					}
				}
			}
			break;
		case 110: // Catch shrimp, cook shrimp
			if (!api.getInventory().contains("Raw shrimps")) {
				fishingSpot = api.getNpcs().closest("Fishing spot");
				if (fishingSpot != null) {
					if (!api.myPlayer().isAnimating()) {
						if (api.getInventory().isItemSelected()) {
							api.getInventory().deselectItem();
						} else {
							fishingSpot.interact("Net");
							sleep(1200);
						}
					}
				}
			} else {
				if (!Tab.INVENTORY.isOpen(api.getBot())) {
					api.getTabs().open(Tab.INVENTORY);
					sleep(500);
				} else {
					RS2Object fire = api.getObjects().closest("Fire");
					if (fire != null) {
						if (!api.getInventory().isItemSelected()) {
							api.getInventory().getItem("Raw shrimps").interact();
							sleep(500);
						} else {
							if (!api.myPlayer().isAnimating()) {
								fire.interact();
								sleep(1500);
							}
						}
					} else {
						if (isTileFree()) {
							if (api.getInventory().contains("Logs")) {
								if (!api.myPlayer().isAnimating()) {
									if (api.getInventory().isItemSelected()) {
										api.getInventory().getItem("Logs").interact();
										sleep(500);
									} else {
										api.getInventory().getItem("Tinderbox").interact();
										sleep(500);
									}
								}
							} else {
								tree = api.getObjects().closest("Tree");
								if (tree != null) {
									if (!api.myPlayer().isAnimating()) {
										tree.interact("Chop down");
										sleep(800);
									}
								}
							}
						}
					}
				}
			}
			break;
		case 120: // Walk to (3090, 3092), Open Gate (id: 9708)
			RS2Object gate = api.getObjects().closest("Gate");
			if (gate != null && gate.isVisible()) {
				gate.interact("Open");
				sleep(1000);
			} else {
				api.getWalking().walk(new Position(3090, 3092, 0));
			}
			break;
		case 130: // Open door (id: 9709)
			door = api.getObjects().closest(9709);
			if (door != null && door.isVisible()) {
				door.interact("Open");
				sleep(1000);
			} else {
				api.getWalking().walk(new Position(3079, 3084, 0));
			}
			break;
		case 140: // Talk to 'Master chef'
			NPC masterChef = api.getNpcs().closest("Master Chef");
			if (masterChef != null) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						masterChef.interact("Talk-to");
						sleep(800);
					}
				}
			}
			break;
		case 150: // Use "Pot of flour" and "Bucket of water"
			if (api.getInventory().isItemSelected()) {
				api.getInventory().getItem("Pot of flour").interact();
				sleep(500);
			} else {
				api.getInventory().getItem("Bucket of water").interact();
				sleep(500);
			}
			break;
		case 160: // Use "Bread dough" on range (Go back if burnt)
			RS2Object range = api.getObjects().closest("Range");
			if (range != null) {
				if (!api.myPlayer().isAnimating()) {
					if (api.getInventory().isItemSelected()) {
						range.interact();
						sleep(800);
					} else {
						api.getInventory().getItem("Bread dough").interact();
						sleep(500);
					}
				}
			}
			break;
		case 170: // Open music
			if (!Tab.MUSIC.isOpen(api.getBot())) {
				api.getTabs().open(Tab.MUSIC);
				sleep(500);
			}
			break;
		case 180: // Open door (id: 9710)
			door = api.getObjects().closest(9710);
			if (door != null && door.isVisible()) {
				door.interact("Open");
				sleep(500);
			} else {
				api.getWalking().walk(new Position(3073, 3090, 0));
			}
			break;
		case 183: // Open emotes
			if (!Tab.EMOTES.isOpen(api.getBot())) {
				api.getTabs().open(Tab.EMOTES);
				sleep(500);
			}
			break;
		case 187: // Press any emote
			RS2Widget emote = api.getWidgets().get(216, 1, 0);
			if (emote != null && emote.isVisible()) {
				emote.interact();
				sleep(500);
			}
			break;
		case 190: // Open settingsFolder
			if (!Tab.SETTINGS.isOpen(api.getBot())) {
				api.getTabs().open(Tab.SETTINGS);
				sleep(500);
			}
			break;
		case 200: // Toggle run
			if (api.getSettings().isRunning()) {
				api.getSettings().setRunning(false);
				sleep(800);
			} else {
				api.getSettings().setRunning(true);
				sleep(800);
			}
			break;
		case 210: // Run to (3085, 3127, 0), open door (id: 9716)
			door = api.getObjects().closest(9716);
			if (door != null && door.isVisible()) {
				door.interact("Open");
				sleep(500);
			} else {
				api.getWalking().walk(new Position(3085, 3127, 0));
			}
			break;
		case 220: // Talk to "Quest Guide"
			NPC questGuide = api.getNpcs().closest("Quest Guide");
			if (questGuide != null) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						questGuide.interact("Talk-to");
						sleep(500);
					}
				}
			}
			break;
		case 230: // Open quests
			if (!Tab.QUEST.isOpen(api.getBot())) {
				api.getTabs().open(Tab.QUEST);
				sleep(500);
			}
			break;
		case 240: // Talk to "Quest Guide"
			questGuide = api.getNpcs().closest("Quest Guide");
			if (questGuide != null) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						questGuide.interact("Talk-to");
						sleep(500);
					}
				}
			}
			break;
		case 250: // Climb down ladder (id: 9726)
			RS2Object ladder = api.getObjects().closest(9726);
			if (ladder != null && ladder.isVisible()) {
				ladder.interact("Climb-down");
				sleep(1200);
			} else {
				api.getWalking().walk(ladder);
			}
			break;
		case 260: // Walk to (3082, 9508, 0), talk to "Mining Instructor"
			NPC miningInstructor = api.getNpcs().closest("Mining Instructor");
			if (miningInstructor != null && miningInstructor.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						miningInstructor.interact("Talk-to");
						sleep(500);
					}
				}
			} else {
				api.getWalking().walk(new Position(3081, 9505, 0));
			}
			break;
		case 270: // Prospect tin (id: 10080)
			RS2Object tin = api.getObjects().closest(10080);
			if (tin != null) {
				tin.interact("Prospect");
				new ConditionalSleep(5000) {
					@Override
					public boolean condition() throws InterruptedException {
						return api.getDialogues().inDialogue() && isPendingContinuation();
					}
				}.sleep();
			}
			break;
		case 280: // Prospect copper (id: 10079)
			RS2Object copper = api.getObjects().closest(10079);
			if (copper != null) {
				copper.interact("Prospect");
				new ConditionalSleep(5000) {
					@Override
					public boolean condition() throws InterruptedException {
						return api.getDialogues().inDialogue() && isPendingContinuation();
					}
				}.sleep();
			}
			break;
		case 290: // Talk to "Mining instructor"
			miningInstructor = api.getNpcs().closest("Mining Instructor");
			if (miningInstructor != null && miningInstructor.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						miningInstructor.interact("Talk-to");
						sleep(500);
					}
				}
			} else {
				api.getWalking().walk(new Position(3081, 9505, 0));
			}
			break;
		case 300: // Mine tin (id: 10080)
			tin = api.getObjects().closest(10080);
			if (tin != null) {
				if (!api.myPlayer().isAnimating()) {
					tin.interact("Mine");
					sleep(1200);
				}
			}
			break;
		case 310: // Mine copper (id: 10077)
			copper = api.getObjects().closest(10079);
			if (copper != null) {
				if (!api.myPlayer().isAnimating()) {
					copper.interact("Mine");
					sleep(1200);
				}
			}
			break;
		case 320: // Use tin on furnace (id: 10082)
			RS2Object furnace = api.getObjects().closest("Furnace");
			if (furnace != null) {
				if (api.getInventory().isItemSelected()) {
					furnace.interact();
					new ConditionalSleep(3000) {
						@Override
						public boolean condition() throws InterruptedException {
							return api.getInventory().contains("Bronze bar");
						}
					}.sleep();
					;
				} else {
					if (!Tab.INVENTORY.isOpen(api.getBot())) {
						api.getTabs().open(Tab.INVENTORY);
						sleep(500);
					} else {
						Item tin_ore = api.getInventory().getItem("Tin ore");
						if (tin_ore != null) {
							tin_ore.interact();
							sleep(500);
						}
					}
				}
			}
			break;
		case 330: // Click continue, Talk to "Mining Instructor"
			miningInstructor = api.getNpcs().closest("Mining Instructor");
			if (miningInstructor != null && miningInstructor.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						miningInstructor.interact("Talk-to");
						sleep(500);
					}
				}
			} else {
				api.getWalking().walk(new Position(3081, 9505, 0));
			}
			break;
		case 340: // "Smith" on anvil
			RS2Object anvil = api.getObjects().closest("Anvil");
			if (anvil != null) {
				anvil.interact("Smith");
				sleep(1000);
			}
			break;
		case 350: // "Smith 1 Bronze dagger"
			RS2Widget smith = api.getWidgets().get(312, 2);
			if (smith != null && smith.isVisible()) {
				smith.interact();
				sleep(800);
			}
			break;
		case 360: // Walk to (3090, 9504) Open Gate (id: 9717||9718)
			gate = api.getObjects().closest("Gate");
			if (gate != null && gate.isVisible()) {
				gate.interact("Open");
				sleep(1000);
			} else {
				api.getWalking().walk(new Position(3094, 9503, 0));
			}
			break;
		case 370: // Talk to "Combat Instructor"
			NPC combatInstructor = api.getNpcs().closest("Combat Instructor");
			if (combatInstructor != null && combatInstructor.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						api.getWalking().walk(combatInstructor.getPosition());
						combatInstructor.interact("Talk-to");
						sleep(500);
					}
				}
			} else {
				api.getWalking().walk(new Position(3106, 9509, 0));
			}
			break;
		case 390: // Open equipment
			if (!Tab.EQUIPMENT.isOpen(api.getBot())) {
				api.getTabs().open(Tab.EQUIPMENT);
				sleep(500);
			}
			break;
		case 400: // Click "View equipment stats"
			RS2Widget viewStats = api.getWidgets().get(387, 18);
			if (viewStats != null && viewStats.isVisible()) {
				viewStats.interact();
				sleep(800);
			}
		case 405: // Equip bronze dagger
			if (!api.getEquipment().isWearingItem(EquipmentSlot.WEAPON, "Bronze dagger")
					&& api.getInventory().contains("Bronze dagger")) {
				api.getEquipment().equip(EquipmentSlot.WEAPON, "Bronze dagger");
				sleep(800);
			}
			break;
		case 410: // Close interface, Talk-to "Combat Instructor"
			api.getWidgets().closeOpenInterface();
			sleep(500);
			combatInstructor = api.getNpcs().closest("Combat Instructor");
			if (combatInstructor != null && combatInstructor.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						api.getWalking().walk(combatInstructor.getPosition());
						combatInstructor.interact("Talk-to");
						sleep(500);
					}
				}
			}
			break;
		case 420: // (Open settingsFolder) Click "View equipment stats", remove bronze
					// dagger, equip bronze sword and wooden shield.
			if (!Tab.EQUIPMENT.isOpen(api.getBot())) {
				api.getTabs().open(Tab.EQUIPMENT);
				sleep(500);
			} else {
				if (api.getEquipment().isWearingItem(EquipmentSlot.WEAPON, "Bronze dagger")) {
					api.getEquipment().unequip(EquipmentSlot.WEAPON, "Bronze dagger");
					sleep(800);
				}
				if (!api.getEquipment().isWearingItem(EquipmentSlot.WEAPON, "Bronze dagger")
						&& api.getInventory().contains("Bronze sword")) {
					api.getEquipment().equip(EquipmentSlot.WEAPON, "Bronze sword");
					sleep(800);
				}
				if (!api.getEquipment().isWearingItem(EquipmentSlot.SHIELD, "Wooden shield")
						&& api.getInventory().contains("Wooden Shield")) {
					api.getEquipment().equip(EquipmentSlot.SHIELD, "Wooden shield");
					sleep(800);
				}
			}
			break;
		case 430: // Open attack styles
			if (!Tab.ATTACK.isOpen(api.getBot())) {
				api.getTabs().open(Tab.ATTACK);
				sleep(500);
			}
			break;
		case 440: // Open Gate (id: 9719||9720)
			gate = api.getObjects().closest(new int[] { 9719, 9720 });
			if (gate != null && gate.isVisible()) {
				gate.interact("Open");
				sleep(1000);
			} else {
				api.getWalking().walk(new Position(3111, 9518, 0));
			}
			break;
		case 450: // Attack "Giant rat"
			if (!api.myPlayer().isUnderAttack()) {
				NPC giantRat = api.getNpcs().closest("Giant rat");
				if (giantRat != null && !giantRat.isUnderAttack()) {
					giantRat.interact("Attack");
					sleep(1500);
				}
			}
			break;
		case 460: // Wait for "Giant rat" to die
			if (api.myPlayer().isUnderAttack()) {
				sleep(300);
			} else {
				NPC giantRat = api.getNpcs().closest("Giant rat");
				if (giantRat != null && !giantRat.isUnderAttack()) {
					giantRat.interact("Attack");
					sleep(1000);
				}
			}
			break;
		case 470: // Open Gate (id: 9719||9720), walk to (3107, 9510), Talk to
					// "Combat Instructor"
			if (!api.getMap().canReach(new Position(3107, 9510, 0))) {
				gate = api.getObjects().closest(new int[] { 9719, 9720 });
				if (gate != null && gate.isVisible()) {
					gate.interact("Open");
					sleep(1000);
				} else {
					api.getWalking().walk(new Position(3110, 9518, 0));
				}
			} else {
				combatInstructor = api.getNpcs().closest("Combat Instructor");
				if (combatInstructor != null && combatInstructor.isVisible()) {
					if (api.getDialogues().inDialogue() && isPendingContinuation()) {
						clickContinue();
						sleep(300);
					} else {
						if (!isPendingContinuation()) {
							api.getWalking().walk(combatInstructor.getPosition());
							combatInstructor.interact("Talk-to");
							sleep(500);
						}
					}
				} else {
					api.getWalking().walk(new Position(3107, 9510, 0));
				}
			}
			break;
		case 480: // Equip bronze arrow and shortbow, attack "Giant Rat")
			if (!api.getEquipment().isWearingItem(EquipmentSlot.WEAPON, "Shortbow")) {
				api.getEquipment().equip(EquipmentSlot.WEAPON, "Shortbow");
				sleep(800);
			} else {
				if (!api.getEquipment().isWearingItem(EquipmentSlot.ARROWS, "Bronze arrow")) {
					api.getEquipment().equip(EquipmentSlot.ARROWS, "Bronze arrow");
					sleep(800);
				} else {
					if (!api.myPlayer().isUnderAttack()) {
						NPC giantRat = api.getNpcs().closest("Giant rat");
						if (giantRat != null && !giantRat.isUnderAttack()) {
							giantRat.interact("Attack");
							sleep(1500);
						}
					}
				}
			}
			break;
		case 490: // Wait for "Giant rat" to die
			if (api.myPlayer().isUnderAttack()) {
				sleep(300);
			} else {
				NPC giantRat = api.getNpcs().closest("Giant rat");
				if (giantRat != null && !giantRat.isUnderAttack()) {
					giantRat.interact("Attack");
					sleep(1000);
				}
			}
			break;
		case 500: // Walk to (3111, 9525, 0), Climb ladder (id: 9727)
			ladder = api.getObjects().closest("Ladder");
			if (ladder != null && ladder.isVisible()) {
				ladder.interact("Climb-up");
				sleep(500);
			} else {
				api.getWalking().walk(new Position(3111, 9525, 0));
			}
			break;
		case 510: // Walk to (3122, 3120, 0), Talk to banker (w/ Options)
			RS2Object bankBooth = api.getObjects().closest("Bank booth");
			if (bankBooth != null && bankBooth.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else if (api.getDialogues().inDialogue() && api.getDialogues().isPendingOption()) {
					api.getDialogues().selectOption(1);
					sleep(300);
				} else {
					bankBooth.interact();
					sleep(1200);
				}
			} else {
				api.getWalking().walk(new Position(3122, 3120, 0));
			}
			break;
		case 520: // Close bank interface, use poll booth (id: 26815) - Skip 1,
					// 2, 3 times,
			if (api.getBank().isOpen()) {
				api.getBank().close();
				sleep(500);
			}
			RS2Object pollBooth = api.getObjects().closest("Poll booth");
			if (pollBooth != null) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					pollBooth.interact();
					sleep(800);
				}
			}
			break;
		case 525: // Close interface, Open door (id: 9721)
			if (api.getWidgets().isVisible(310)) {
				api.getWidgets().closeOpenInterface();
				sleep(500);
			}
			door = api.getObjects().closest(9721);
			if (door != null && door.isVisible()) {
				door.interact("Open");
				sleep(500);
			} else if (door != null) {
				api.getWalking().walk(new Position(3124, 3124, 0));
			}
			break;
		case 530: // Talk to "Financial Advisor"
			NPC financialAdvisor = api.getNpcs().closest("Financial Advisor");
			if (financialAdvisor != null && financialAdvisor.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						financialAdvisor.interact("Talk-to");
						sleep(500);
					}
				}
			}
			break;
		case 540: // Open door (id: 9722)
			door = api.getObjects().closest(9722);
			if (door != null && door.isVisible()) {
				door.interact("Open");
				sleep(500);
			}
			break;
		case 550: // Walk to (3130, 3109, 0), Talk to "Brother Brace"
			NPC brotherBrace = api.getNpcs().closest("Brother Brace");
			if (brotherBrace != null && brotherBrace.isVisible()) {
				if (!api.getMap().canReach(brotherBrace)) {
					door = api.getObjects().closest("Large door");
					if (door != null && door.isVisible()) {
						door.interact("Open");
						sleep(500);
					}
				} else {
					if (api.getDialogues().inDialogue() && isPendingContinuation()) {
						clickContinue();
						sleep(300);
					} else {
						if (!isPendingContinuation()) {
							brotherBrace.interact("Talk-to");
							sleep(500);
						}
					}
				}
			} else {
				if (api.getMap().canReach(new Position(3125, 3107, 0))) {
					api.getWalking().walk(new Position(3125, 3107, 0));
				} else {
					api.getWalking().walk(new Position(3130, 3107, 0));
					door = api.getObjects().closest("Large door");
					if (door != null && door.isVisible()) {
						door.interact("Open");
						sleep(500);
					}
				}
			}
			break;
		case 560: // Open prayer
			if (!Tab.PRAYER.isOpen(api.getBot())) {
				api.getTabs().open(Tab.PRAYER);
				sleep(500);
			}
			break;
		case 570: // Talk to "Brother Brace"
			brotherBrace = api.getNpcs().closest("Brother Brace");
			if (brotherBrace != null && brotherBrace.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						brotherBrace.interact("Talk-to");
						sleep(500);
					}
				}
			}
			break;
		case 580: // Open friends
			if (!Tab.FRIENDS.isOpen(api.getBot())) {
				api.getTabs().open(Tab.FRIENDS);
				sleep(500);
			}
			break;
		case 590: // Open ignore
			if (!Tab.IGNORES.isOpen(api.getBot())) {
				api.getTabs().open(Tab.IGNORES);
				sleep(500);
			}
			break;
		case 600: // Talk to "Brother Brace"
			brotherBrace = api.getNpcs().closest("Brother Brace");
			if (brotherBrace != null && brotherBrace.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						brotherBrace.interact("Talk-to");
						sleep(500);
					}
				}
			}
			break;
		case 610: // Open door (id: 9723)
			door = api.getObjects().closest(9723);
			if (door != null && door.isVisible()) {
				door.interact("Open");
				sleep(500);
			} else {
				api.getWalking().walk(new Position(3122, 3103, 0));
			}
			break;
		case 620: // Walk to (3141, 3087, 0), Talk to "Magic Instructor"
			NPC magicInstructor = api.getNpcs().closest("Magic Instructor");
			if (magicInstructor != null && magicInstructor.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						magicInstructor.interact("Talk-to");
						sleep(500);
					}
				}
			} else {
				api.getWalking().walk(new Position(3141, 3087, 0));
			}
			break;
		case 630: // Open magic
			if (!Tab.MAGIC.isOpen(api.getBot())) {
				api.getTabs().open(Tab.MAGIC);
				sleep(500);
			}
			break;
		case 640: // Talk to "Magic Instructor"
			magicInstructor = api.getNpcs().closest("Magic Instructor");
			if (magicInstructor != null && magicInstructor.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						magicInstructor.interact("Talk-to");
						sleep(500);
					}
				}
			}
			break;
		case 650: // Cast wind strike on chicken
			Area house = new Area(3140, 3084, 3142, 3091);
			if (house.contains(api.myPlayer())) {
				api.getWalking().walk(new Position(3140, 3091, 0));
				NPC chicken = api.getNpcs().closest("Chicken");
				if (chicken != null && chicken.isVisible()) {
					if (!api.myPlayer().isUnderAttack()) {
						api.getMagic().castSpellOnEntity(NormalSpells.WIND_STRIKE, chicken);
						sleep(1000);
					}
				}
			} else {
				api.getWalking().walk(house);
			}
			break;
		case 670: // Talk to "Magic Instructor" (w/ options = "Yes.")
			magicInstructor = api.getNpcs().closest("Magic Instructor");
			if (magicInstructor != null && magicInstructor.isVisible()) {
				if (api.getDialogues().inDialogue() && isPendingContinuation()) {
					clickContinue();
					sleep(300);
				} else if (api.getDialogues().inDialogue() && api.getDialogues().isPendingOption()) {
					api.getDialogues().selectOption("Yes.");
					sleep(300);
				} else {
					if (!isPendingContinuation()) {
						magicInstructor.interact("Talk-to");
						sleep(500);
					}
				}
			}
			break;
		default:
			//parent.getLogoutTab().logOut();
			api.log(configValue);
		}
	}

	private boolean isTileFree() {
		List<RS2Object> fires = api.getObjects().get(api.myPosition().getX(), api.myPosition().getY());
		for (RS2Object fire : fires) {
			if (fire.getName().equals("Fire")) {
				return false;
			}
		}
		return true;
	}

	private boolean isPendingContinuation() {
		RS2Widget widget = api.getWidgets()
				.getWidgetContainingText(new String[] { "Click here to continue", "Please wait..." });
		if (widget != null && widget.isVisible()) {
			return true;
		}
		widget = api.getWidgets().get(162, 33);
		if (widget != null && widget.isVisible()) {
			return true;
		}
		return false;
	}

	private void clickContinue() throws InterruptedException {
		RS2Widget widget = api.getWidgets().getWidgetContainingText("Click here to continue");
		if (widget != null && widget.isVisible()) {
			api.getKeyboard().typeKey((char) KeyEvent.VK_SPACE);
			sleep(200);
		}
		widget = api.getWidgets().get(162, 33);
		if (widget != null && widget.isVisible()) {
			widget.interact();
			sleep(500);
		}
	}

	private void sleep(long ms) throws InterruptedException {
		MethodProvider.sleep(ms);
	}

}
