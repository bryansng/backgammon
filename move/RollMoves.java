package move;

import java.util.LinkedList;

public class RollMoves {
	private int rollResult;
	private boolean isSumMove;
	private LinkedList<Move> moves;
	
	public RollMoves(int rollResult, boolean isSumMove) {
		this.rollResult = rollResult;
		this.isSumMove = isSumMove;
		moves = new LinkedList<>();
	}
	
	public boolean isNormalMove() {
		return !isSumMove;
	}
	
	public boolean isSumMove() {
		return isSumMove;
	}
	
	public LinkedList<Move> getMoves() {
		return moves;
	}
	
	public int getRollResult() {
		return rollResult;
	}
	
	public String toString() {
		String s = "Roll of " + rollResult + "\n";
		for (int i = 0; i < moves.size(); i++) {
			s += " - " + moves.get(i) + "\n";
		}
		return s;
	}
}
