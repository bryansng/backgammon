package move;

/**
 * This class represents a Bar to Pip move.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class BarToPip extends SumMove implements Move {
	private RollMoves rollMoves;
	private int toPip;
	
	public BarToPip(int toPip, RollMoves rollMoves) {
		this(toPip, rollMoves, null);
	}
	
	public BarToPip(int toPip, RollMoves rollMoves, Move intermediateMove) {
		super(intermediateMove);
		this.rollMoves = rollMoves;
		this.toPip = toPip;
	}
	
	public RollMoves getRollMoves() {
		return rollMoves;
	}
	
	public int getToPip() {
		return toPip;
	}
	
	// not used atm.
	public String toString() {
		String s = "fromBar, toPip: " + (toPip+1);
		if (this.hasIntermediateMove()) {
			s += "\n ~ with intermediate move:\n ~ " + this.getIntermediateMove();
		}
		return s;
	}
}
