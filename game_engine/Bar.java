package game_engine;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;

/**
 * This class represents the bar game component in Backgammon.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Bar extends CheckersStorer {
	private Color checkerColoursToStore;
	
	public Bar(Color color) {
		super();
		checkerColoursToStore = color;
		
		double pointWidth = Settings.getPipSize().getWidth();
		double pointHeight = Settings.getPipSize().getHeight();
		
		setMinSize(pointWidth, pointHeight);
		setMaxSize(pointWidth, pointHeight);
		setAlignment(Pos.BOTTOM_CENTER);
	}
	
	/**
	 * Returns the color that this bar in particular represents.
	 * @return color that this bar represents.
	 */
	public Color getColour() {
		return checkerColoursToStore;
	}
}
