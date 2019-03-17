package move;

import java.util.LinkedList;

import game.Dice;

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
 * @author Braddy Yeoh, 17357376
 *
 */
public class RollMoves {
	private Dice dice;
	private LinkedList<RollMoves> dependedRollMoves;
	private boolean isUsed;
	private LinkedList<Move> moves;
	
	public RollMoves() {
		this(-1, null);
	}
	
	public RollMoves(int sumDiceResult, LinkedList<RollMoves> dependedRollMoves) {
		this(new Dice(sumDiceResult), dependedRollMoves);
	}
	
	public RollMoves(Dice dice, LinkedList<RollMoves> dependedRollMoves) {
		this.dice = dice;
		this.dependedRollMoves = dependedRollMoves;
		this.isUsed = false;
		moves = new LinkedList<>();
	}
	
	// Copy Constructor.
	// https://www.artima.com/intv/bloch13.html
	public RollMoves(RollMoves otherRollMoves) {
		this(otherRollMoves.getDice(), otherRollMoves.getDependedRollMoves());
		
		for (Move aMove : otherRollMoves.getMoves()) {
			if (aMove instanceof PipToPip) {
				PipToPip move = new PipToPip((PipToPip) aMove);
				move.setRollMoves(this);
				moves.add(new PipToPip(move));
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
	
	public boolean isNormalRollMoves() {
		return dependedRollMoves == null;
	}
	
	public boolean isSumRollMoves() {
		return !isNormalRollMoves();
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
	
	public LinkedList<RollMoves> getDependedRollMoves() {
		return dependedRollMoves;
	}
	
	public void setDependentRollMoves(LinkedList<RollMoves> dependedRollMoves) {
		this.dependedRollMoves = dependedRollMoves;
	}
	
	// Used by calculateNormalMoves() in BoardMoves.class as optimization,
	// to relate with RollMoves' respective dice object.
	public RollMoves setDice(Dice dice) {
		this.dice = dice;
		return this;
	}
	
	public Dice getDice() {
		return dice;
	}
	
	public int getDiceResult() {
		return dice.getDiceResult();
	}
	
	public String printDependentRollMoves(String spaces) {
		String s = "";
		if (isSumRollMoves()) {
			String prefix = "DRM - ";
			s += spaces + "DependentRollMoves:\n";
			for (RollMoves aRollMoves : dependedRollMoves) {
				s += spaces + prefix + aRollMoves + "\n";
			}
		}
		return s;
	}
	
	public boolean equalsValueOf(RollMoves other) {
		return this.getDiceResult() == other.getDiceResult() && isUsed == other.isUsed;
	}
}
