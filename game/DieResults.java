package game;

import java.util.LinkedList;

/**
 * This class represents a linked list of Dice.
 * Used to represent the dice results in each turn.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
@SuppressWarnings("serial")
public class DieResults extends LinkedList<Dice> {
	public DieResults() {
		super();
	}
	
	public String toString() {
		String s = "";
		int i = 1;
		for (Dice aDice : this) {
			s += aDice.getDiceResult();
			
			if (i != size()) s += ", ";
			i++;
		}
		return "[" + s + "]";
	}
}
