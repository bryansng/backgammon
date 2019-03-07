package move;

import java.util.LinkedList;

/**
 * This class represents a Pip to Pip move.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 */
public class PipToPip extends SumMove {
	private int fromPip;
	private int toPip;
	
	public PipToPip(int fromPip, int toPip, RollMoves rollMoves, boolean isHit) {
		this(fromPip, toPip, rollMoves, isHit, null);
	}
	
	public PipToPip(int fromPip, int toPip, RollMoves rollMoves, boolean isHit, LinkedList<Move> intermediateMoves) {
		super(intermediateMoves, rollMoves, isHit);
		this.fromPip = fromPip;
		this.toPip = toPip;
	}

	// Copy Constructor.
	public PipToPip(PipToPip move) {
		this(move.getFromPip(), move.getToPip(), move.getRollMoves(), move.isHit(), move.getIntermediateMoves());
	}
	
	public int getFromPip() {
		return fromPip;
	}
	
	public int getToPip() {
		return toPip;
	}
	
	// not used atm.
	public String toString() {
		String s = "fromPip: " + (fromPip+1) + ", toPip: " + (toPip+1);
		if (this.hasIntermediateMoves()) {
			s += "\n ~ with intermediate move:\n ~ " + this.getIntermediateMoves();
		}
		return s;
	}
	
	public int getFro() {
		return getFromPip();
	}
	
	public int getTo() {
		return getToPip();
	}
}
