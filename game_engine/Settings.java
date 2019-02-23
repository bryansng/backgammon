package game_engine;

import javafx.scene.paint.Color;
import constants.PlayerPerspectiveFrom;
import constants.Quadrant;

/**
 * This class serves as a point where other class files share hard-coded data or relative data.
 * This class represents the user-specified data only, for default game data, refer to GameConstants.java.
 * This allows us to keep track of any user-specified data.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Settings {
	// by default, 1 starts at bottom right of screen, i.e. quadrant 4.
	public static Quadrant getMainQuadrant() {
		return Quadrant.BOTTOM_RIGHT;
	}
	
	public static Color getColor(PlayerPerspectiveFrom pov) {
		Color color = null;
		switch (pov) {
			case BOTTOM:
				color = getBottomPerspectiveColor();
				break;
			case TOP:
				color = getTopPerspectiveColor();
				break;
			default:
		}
		return color;
	}
	
	private static Color getTopPerspectiveColor() {
		return Color.BLACK;
	}
	
	private static Color getBottomPerspectiveColor() {
		return Color.WHITE;
	}
}
