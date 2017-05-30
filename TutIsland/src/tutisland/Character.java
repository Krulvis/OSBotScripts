package tutisland;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;

public class Character {

	private static Map<Integer, Integer> amounts = new HashMap<Integer, Integer>();

	public static void randomize(MethodProvider api) throws InterruptedException {
		fillMap();
		for (Entry<Integer, Integer> e : amounts.entrySet()) {
			RS2Widget arrow = api.getWidgets().get(269, e.getKey());
			int random_amount = MethodProvider.random(e.getValue());
			for (int i = 0; i < random_amount; i++) {
				if (arrow != null) {
					arrow.interact();
					MethodProvider.sleep(500);
				}
			}
		}
	}

	private static void fillMap() {
		amounts.put(113, 17); // heads
		amounts.put(114, 16); // jaws
		amounts.put(115, 14); // torsos
		amounts.put(116, 13); // arms
		amounts.put(117, 2); // hands
		amounts.put(118, 11); // legs
		amounts.put(119, 6); // feet
		amounts.put(121, 12); // colour hair
		amounts.put(127, 15); // colour torso
		amounts.put(129, 15); // colour legs
		amounts.put(130, 4); // colour feet
		amounts.put(131, 8); // colour skin
	}

}
