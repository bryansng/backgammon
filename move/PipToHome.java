package move;

import java.util.LinkedList;

import game_engine.Settings;
import interfaces.ColorParser;
import interfaces.ColorPerspectiveParser;
import javafx.scene.paint.Color;

/**
 * This class represents a Pip to Home move.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class PipToHome extends SumMove implements ColorParser, ColorPerspectiveParser {
	private int fromPip, toHomePipNum;
	private Color toHome;
	
	public PipToHome(int fromPip, Color toHome, RollMoves rollMoves) {
		this(fromPip, toHome, rollMoves, null);
	}
	
	public PipToHome(int fromPip, Color toHome, RollMoves rollMoves, LinkedList<Move> intermediateMoves) {
		super(intermediateMoves, rollMoves, false);
		this.fromPip = fromPip;
		this.toHome = toHome;
		this.toHomePipNum = colorToPipBoundaryNum(toHome);
	}

	// Copy Constructor.
	public PipToHome(PipToHome move) {
		this(move.getFromPip(), move.getToHome(), move.getRollMoves(), move.getIntermediateMoves());
	}
	
	public int getFromPip() {
		return fromPip;
	}
	
	public Color getToHome() {
		return toHome;
	}
	
	// either -1 or 24.
	public int getToHomePipNum() {
		return toHomePipNum;
	}
	
	private int colorToPipBoundaryNum(Color fromBar) {
		return Settings.getPipBearOffBoundary(getPOV(fromBar));
	}
	
	// not used atm.
	public String toString() {
		String s = "fromPip: " + (fromPip+1) + ", toHome ";
		if (this.hasIntermediateMoves()) {
			s += "\n ~ with intermediate move:\n ~ " + this.getIntermediateMoves();
		}
		return s;
	}
	
	public int getFro() {
		return getFromPip();
	}
	
	public int getTo() {
		return getToHomePipNum();
	}
}
