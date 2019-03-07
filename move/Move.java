package move;

/**
 * This abstract class represents the Move (can be PipToPip, PipToHome, BarToPip).
 * 
 * Used to decouple relationship between classes.
 * i.e. (RollMoves with PipToPip, PipToHome, BarToPip).
 * Simplified to (RollMoves with Move), doesn't matter what Move, as long as it is a move.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public abstract class Move {
	private RollMoves rollMoves;
	private boolean isHit;
	
	public Move(RollMoves rollMoves, boolean isHit) {
		this.rollMoves = rollMoves;
		this.isHit = isHit;
	}
	
	public RollMoves getRollMoves() {
		return rollMoves;
	}
	
	public void setRollMoves(RollMoves rollMoves) {
		this.rollMoves = rollMoves;
	}
	
	public boolean isHit() {
		return isHit;
	}
	
	public void setHit(boolean isHit) {
		this.isHit = isHit;
	}
	
	public abstract int getFro();
	public abstract int getTo();
}
