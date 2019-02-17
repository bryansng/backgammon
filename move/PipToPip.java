package move;

public class PipToPip extends SumMove implements Move {
	private RollMoves rollMoves;
	private int fromPip;
	private int toPip;
	
	public PipToPip(int fromPip, int toPip, RollMoves rollMoves) {
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
	
	public String toString() {
		return "fromPip: " + (fromPip+1) + ", toPip: " + (toPip+1);
	}
}
