package move;

import java.util.LinkedList;
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
 * @author Braddy Yeoh, 17357376
 *
 */
public class BarToPip extends SumMove implements ColorParser, ColorPerspectiveParser {
	private Color fromBar;
	private int toPip, fromBarPipNum;
	
	public BarToPip(Color fromBar, int toPip, RollMoves rollMoves, boolean isHit) {
		this(fromBar, toPip, rollMoves, isHit, null);
	}
	
	public BarToPip(Color fromBar, int toPip, RollMoves rollMoves, boolean isHit, LinkedList<Move> intermediateMoves) {
		super(intermediateMoves, rollMoves, isHit);
		this.fromBar = fromBar;
		this.fromBarPipNum = colorToPipBoundaryNum(fromBar);
		this.toPip = toPip;
	}

	// Copy Constructor.
	public BarToPip(BarToPip move) {
		this(move.getFromBar(), move.getToPip(), move.getRollMoves(), move.isHit(), move.getIntermediateMoves());
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
	
	private int colorToPipBoundaryNum(Color fromBar) {
		return Settings.getPipBearOnBoundary(getPOV(fromBar));
	}
	
	// not used atm.
	public String toString() {
		String s = "fromBar: " + parseColor(fromBar) + ", toPip: " + (toPip+1);
		if (this.hasIntermediateMoves()) {
			s += "\n ~ with intermediate move:\n ~ " + this.getIntermediateMoves();
		}
		return s;
	}
	
	public int getFro() {
		return getFromBarPipNum();
	}
	
	public int getTo() {
		return getToPip();
	}
}
