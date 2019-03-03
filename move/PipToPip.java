package move;

import java.util.LinkedList;

/**
 * This class represents a Pip to Pip move.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class PipToPip extends SumMove implements Move {
	private RollMoves rollMoves;
	private int fromPip;
	private int toPip;
	private boolean isHit;
	
	public PipToPip(int fromPip, int toPip, RollMoves rollMoves, boolean isHit) {
		this(fromPip, toPip, rollMoves, isHit, null);
	}
	
	public PipToPip(int fromPip, int toPip, RollMoves rollMoves, boolean isHit, LinkedList<Move> intermediateMoves) {
		super(intermediateMoves);
		this.rollMoves = rollMoves;
		this.fromPip = fromPip;
		this.toPip = toPip;
		this.isHit = isHit;
	}

	// Copy Constructor.
	public PipToPip(PipToPip move) {
		this(move.getFromPip(), move.getToPip(), move.getRollMoves(), move.isHit(), move.getIntermediateMoves());
	}
	
	public RollMoves getRollMoves() {
		return rollMoves;
	}
	
	public void setRollMoves(RollMoves rollMoves) {
		this.rollMoves = rollMoves;
	}
	
	public int getFromPip() {
		return fromPip;
	}
	
	public int getToPip() {
		return toPip;
	}
	
	public boolean isHit() {
		return isHit;
	}
	
	// not used atm.
	public String toString() {
		String s = "fromPip: " + (fromPip+1) + ", toPip: " + (toPip+1);
		if (this.hasIntermediateMoves()) {
			s += "\n ~ with intermediate move:\n ~ " + this.getIntermediateMoves();
		}
		return s;
	}
}
