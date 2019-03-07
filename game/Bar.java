package game;

import constants.GameConstants;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

/**
 * This class represents the bar game component in Backgammon.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Bar extends CheckersStorer {
	private Color checkerColorsToStore;
	
	public Bar(Color color) {
		super();
		checkerColorsToStore = color;
		
		double pointWidth = GameConstants.getPipSize().getWidth();
		double pointHeight = GameConstants.getPipSize().getHeight();
		
		setMinSize(pointWidth, pointHeight);
		setMaxSize(pointWidth, pointHeight);
		setAlignment(Pos.BOTTOM_CENTER);
		
		if (GameConstants.FORCE_CHECKERS_AT_BARS) {
			initCheckers(2, color);
		} else if (GameConstants.FORCE_LESS_CHECKERS_AT_BARS) {
			initCheckers(1, color);
		}
	}
	
	/**
	 * Returns the color that this bar in particular represents.
	 * @return color that this bar represents.
	 */
	public Color getColor() {
		return checkerColorsToStore;
	}
}
