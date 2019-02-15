package game_engine;

import java.util.LinkedList;

public class PipMove {
	private int fromPip;
	private LinkedList<Integer> toPips;
	private int toHome;
	
	public PipMove(int fromPip) {
		this.fromPip = fromPip;
		toPips = new LinkedList<Integer>();
		toHome = -1;
	}
	
	public int getFromPip() {
		return fromPip;
	}
	
	public LinkedList<Integer> getToPips() {
		return toPips;
	}
	
	public int getToHome() {
		return toHome;
	}
	
	public String toString() {
		String s = "Able to move from " + (fromPip+1) + " to [";
		int i = 0;
		for (Integer toPip : toPips) {
			s += Integer.toString(toPip.intValue() + 1);
			
			if (i < toPips.size()-1) {
				s += ", ";
			}
			i++;
		}
		return s + "]";
		
		//return "Able to move from " + (fromPip+1) + " " + toPips.toString() + ".";
	}
}
