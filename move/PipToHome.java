package move;

/**
 * This class represents a Pip to Home move.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class PipToHome extends SumMove implements Move {
	private RollMoves rollMoves;
	private int fromPip;
	
	public PipToHome(int fromPip, RollMoves rollMoves) {
		this(fromPip, rollMoves, null);
	}
	
	public PipToHome(int fromPip, RollMoves rollMoves, Move intermediateMove) {
		super(intermediateMove);
		this.rollMoves = rollMoves;
		this.fromPip = fromPip;
	}

	// Copy Constructor.
	public PipToHome(PipToHome move) {
		this(move.getFromPip(), move.getRollMoves(), move.getIntermediateMove());
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
	
	// not used atm.
	public String toString() {
		String s = "fromPip: " + (fromPip+1) + ", toHome ";
		if (this.hasIntermediateMove()) {
			s += "\n ~ with intermediate move:\n ~ " + this.getIntermediateMove();
		}
		return s;
	}
}
