package api.wrappers;

import api.ATMethodProvider;
import api.util.Random;
import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.ui.Option;
import org.osbot.rs07.input.mouse.MouseDestination;
import org.osbot.rs07.input.mouse.PointDestination;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;

/**
 * Created by Krulvis on 15-Mar-17.
 */
public class ATInteract extends ATMethodProvider {

	public ATInteract(ATMethodProvider parent) {
		init(parent);
	}

	public boolean interact(final MouseDestination target) {
		if (target != null) {
			if (!target.evaluate()) {
				if (mouse.move(target)) {
					waitFor(50, new Condition() {
						@Override
						public boolean evaluate() {
							return target.evaluate();
						}
					});
				}
			}
			if (target.evaluate()) {
				return mouse.click(false);
			}
		}
		return false;
	}

	public boolean interact(final MouseDestination target, String action, String noun, boolean forceRightMouse) {
		try {
			if (target == null || action == null) {
				System.out.println("target is null, returning false");
				return false;
			}
			int tries = 5;
			while (tries-- > 0 && !sufficient(action, noun, target)) {
				sleep(random(5, 20));
				//System.out.println("not sufficient \"" + action + "\" || Menu items length <= 2, tries-left: " + tries);
				if (mouse.move(target)) {
					sleep(random(5, 20));
				}
				if (menu.isOpen() && getOptionIndex(action, noun) == -1) {
					System.out.println("Wrong menu opened!");
					moveMouseRandomly();
				}
			}

			if (sufficient(action, noun, target) || menu.isOpen()) {
				int optionIndex = getOptionIndex(action, noun);
				//System.out.println("OptionIndex: " + optionIndex);
				if (optionIndex < 0) {
					if (menu.isOpen()) {
						System.out.println("Wrong menu opened!");
						moveMouseRandomly();
					}
					return false;
				}
				if (optionIndex == 0 && !forceRightMouse) {
					if (menu.isOpen() && clickMenu(action, noun)) {
						return waitFor(300, new Condition() {
							@Override
							public boolean evaluate() {
								return mouse.getCrossHairColor() != Mouse.CrossHairColor.YELLOW;
							}
						});
					}
					if (getOptionIndex(action, noun) == 0 && mouse.click(false)) {
						return waitFor(300, new Condition() {
							@Override
							public boolean evaluate() {
								return mouse.getCrossHairColor() != Mouse.CrossHairColor.YELLOW;
							}
						});
					}
				} else {
					//System.out.println("Optionindex > 0, open menu");
					if (!menu.isOpen() && !mouse.click(true)) {
						//System.out.println("Failed right clicking");
						tries = 4;
						while (tries-- > 0 && !menu.isOpen()) {
							mouse.click(true);
							waitFor(100, new Condition() {
								@Override
								public boolean evaluate() {
									return menu.isOpen();
								}
							});
						}
					}
					sleep(random(50, 250));
					if (menu.isOpen()) {
						if (clickMenu(action, noun)) {
							//boolean missed = false;
							return waitFor(300, new Condition() {
								@Override
								public boolean evaluate() {
									return mouse.getCrossHairColor() != Mouse.CrossHairColor.YELLOW;
								}
							});
						}
						//System.out.println("Can't interact with menu");
					}
				}
			} else if (!myPlayer().isMoving()) {
				moveMouseRandomly();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean clickMenu(String action, String noun) {
		int index = getOptionIndex(action, noun);
		return index > -1 && clickMenu(index);
	}

	public boolean clickMenu(int optionIndex) {
		System.out.println("Interacting with menu, index: " + optionIndex);
		if (!menu.isOpen()) {
			return false;
		}
		int menuX = menu.getX();
		int menuY = menu.getY();
		int menuW = menu.getWidth();
		int menuH = menu.getHeight();
		int menuRows = menu.getMenuCount();
		int offset = menuH - menuRows * 15 - 3;
		Point mouseP = mouse.getPosition();

		int width = random(5, 30);
		Rectangle r = new Rectangle(mouseP.x - width, menuY + offset + (15 * optionIndex) + 2, width * 2, 12);

		RectangleDestination test_ = new RectangleDestination(bot, r);
		mouse.move(test_, true);

		if (!menu.isOpen()) {
			System.out.println("-> menu closed for some reason");
			return false;
		}
		mouse.click(false);
		new ConditionalSleep(150) {
			@Override
			public boolean condition() throws InterruptedException {
				return mouse.getCrossHairColor() == Mouse.CrossHairColor.RED;
			}
		}.sleep();
		return mouse.getCrossHairColor() == Mouse.CrossHairColor.RED;
	}


	public int getOptionIndex(String action, String noun) {
		java.util.List<Option> options = menu.getMenu();
		for (int i = 0; i < options.size(); i++) {
			Option o = options.get(i);
			String a = o.action.replaceAll("\\<[^>]*>", "");
			String n = o.name.replaceAll("\\<[^>]*>", "");
			//System.out.println("A: " + a + ", N: " + n);
			if ((action == null || a.equals(action)) &&
					(noun == null || n.contains(noun.replace((char) -96, (char) 32)))) {
				return i;
			}
		}
		return -1;
	}

	public int getOptionIndex(String action) {
		java.util.List<Option> options = menu.getMenu();
		for (int i = 0; i < options.size(); i++) {
			Option o = options.get(i);
			String a = o.action.replaceAll("\\<[^>]*>", "");
			if ((action == null || a.equals(action))) {
				return i;
			}
		}
		return -1;
	}

	public boolean sufficient(String action, String noun, MouseDestination target) {
		final java.util.List<Option> options = menu.getMenu();
		final String upText = menu.getTooltip();
		if (options != null && options.size() > 0 && upText != null) {
			if (options.size() <= 2 && upText.contains("->") && (noun == null || upText.toLowerCase().contains(noun.toLowerCase().replace((char) -96, (char) 32)))) {
				return true;
			} else if (options.size() > 0 && getOptionIndex(action, noun) > -1) {
				return menu.isOpen() || target.evaluate();
			} else if (upText.contains("Cancel")) {
				return false;
			} else if (options.size() == 2 && upText.contains("Walk here")) {
				return false;
			}
		}
		return false;
	}

	public void moveMouseRandomly() {
		Point dest = new Point(Random.nextGaussian(0, 700, 100), Random.nextGaussian(0, 400, 100));
		mouse.move(new PointDestination(bot, dest));
	}
}
