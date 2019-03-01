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
	private boolean isSumMove, isUsed;
	private LinkedList<Move> moves;
	
	public RollMoves(int rollResult, boolean isSumMove) {
		this.rollResult = rollResult;
		this.isSumMove = isSumMove;
		this.isUsed = false;
		moves = new LinkedList<>();
	}
	
	// Copy Constructor.
	// https://www.artima.com/intv/bloch13.html
	public RollMoves(RollMoves otherRollMoves) {
		this(otherRollMoves.getRollResult(), otherRollMoves.isSumMove());
		
		for (Move aMove : otherRollMoves.getMoves()) {
			if (aMove instanceof PipToPip) {
				PipToPip move = new PipToPip((PipToPip) aMove);
				move.setRollMoves(this);
				moves.add(move);
			} else if (aMove instanceof PipToHome) {
				PipToHome move = new PipToHome((PipToHome) aMove);
				move.setRollMoves(this);
				moves.add(new PipToHome(move));
			} else if (aMove instanceof BarToPip) {
				BarToPip move = new BarToPip((BarToPip) aMove);
				move.setRollMoves(this);
				moves.add(new BarToPip(move));
			}
		}
	}
	
	public boolean isNormalMove() {
		return !isSumMove;
	}
	
	public boolean isSumMove() {
		return isSumMove;
	}
	
	public boolean isUsed() {
		return isUsed;
	}
	
	public void setUsed() {
		isUsed = true;
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
