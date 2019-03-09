package game;

import java.util.LinkedList;

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
