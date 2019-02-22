package move;

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
	
	public PipToPip(int fromPip, int toPip, RollMoves rollMoves) {
		this(fromPip, toPip, rollMoves, null);
	}
	
	public PipToPip(int fromPip, int toPip, RollMoves rollMoves, Move intermediateMove) {
		super(intermediateMove);
		this.rollMoves = rollMoves;
		this.fromPip = fromPip;
		this.toPip = toPip;
	}
	
	public RollMoves getRollMoves() {
		return rollMoves;
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
		if (this.hasIntermediateMove()) {
			s += "\n ~ with intermediate move:\n ~ " + this.getIntermediateMove();
		}
		return s;
	}
}
