package move;

import java.util.LinkedList;

/**
 * This class represents a single roll dice result with a number of moves derived from that result.
 * 
 * EXPLANATION of Roll die related moves data structure:
 * 1st Principle: An individual dice result allows a player to move checkers from one pip to the other.
 * So a single dice result will result in a player being able to move a number of moves.
 * 
 * Single dice result - designated by instance variable rollResult.
 * A number of moves - designated by instance variable moves.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
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
	
	// not used atm.
	public String toString() {
		String s = "Roll of " + rollResult + "\n";
		for (int i = 0; i < moves.size(); i++) {
			s += " - " + moves.get(i) + "\n";
		}
		return s;
	}
}
