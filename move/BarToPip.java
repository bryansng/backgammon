package move;

import game_engine.Settings;
import interfaces.ColorParser;
import interfaces.ColorPerspectiveParser;
import javafx.scene.paint.Color;

/**
 * This class represents a Bar to Pip move.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class BarToPip extends SumMove implements Move, ColorParser, ColorPerspectiveParser {
	private RollMoves rollMoves;
	private Color fromBar;
	private int toPip, fromBarPipNum;
	private boolean isHit;
	
	public BarToPip(Color fromBar, int toPip, RollMoves rollMoves, boolean isHit) {
		this(fromBar, toPip, rollMoves, isHit, null);
	}
	
	public BarToPip(Color fromBar, int toPip, RollMoves rollMoves, boolean isHit, Move intermediateMove) {
		super(intermediateMove);
		this.fromBar = fromBar;
		this.fromBarPipNum = colorToPipBoundaryNum(fromBar);
		this.toPip = toPip;
		this.rollMoves = rollMoves;
	}

	// Copy Constructor.
	public BarToPip(BarToPip move) {
		this(move.getFromBar(), move.getToPip(), move.getRollMoves(), move.isHit(), move.getIntermediateMove());
	}
	
	public RollMoves getRollMoves() {
		return rollMoves;
	}
	
	public void setRollMoves(RollMoves rollMoves) {
		this.rollMoves = rollMoves;
	}
	
	public int getToPip() {
		return toPip;
	}
	
	public Color getFromBar() {
		return fromBar;
	}
	
	// either -1 or 24.
	public int getFromBarPipNum() {
		return fromBarPipNum;
	}
	
	public boolean isHit() {
		return isHit;
	}
	
	private int colorToPipBoundaryNum(Color fromBar) {
		return Settings.getPipBearOnBoundary(getPOV(fromBar));
	}
	
	// not used atm.
	public String toString() {
		String s = "fromBar: " + parseColor(fromBar) + ", toPip: " + (toPip+1);
		if (this.hasIntermediateMove()) {
			s += "\n ~ with intermediate move:\n ~ " + this.getIntermediateMove();
		}
		return s;
	}
}
