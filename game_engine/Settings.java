package game_engine;

import constants.Quadrant;

/**
 * This class serves as a point where other class files share hard-coded data or relative data.
 * This class represents the user-specified data only, for default game data, refer to GameConstants.java.
 * This allows us to keep track of any user-specified data.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Settings {
	// by default, 1 starts at bottom right of screen, i.e. quadrant 4.
	public static Quadrant getMainQuadrant() {
		return Quadrant.BOTTOM_RIGHT;
	}
}
