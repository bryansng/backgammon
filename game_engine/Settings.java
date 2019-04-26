package game_engine;

import javafx.scene.paint.Color;
import constants.GameConstants;
import constants.PlayerPerspectiveFrom;
import constants.Quadrant;
import exceptions.PlayerNoPerspectiveException;

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
	public static final boolean ENABLE_AUTO_PLAY = false;
	public static final boolean ENABLE_AUTO_ROLL = false;
	public static final boolean ENABLE_NEXT_PAUSE = true;
	public static boolean DARK_THEME = true;
	public static final int DEFAULT_TOTAL_GAMES = 11;
	public static int TOTAL_GAMES_IN_A_MATCH = DEFAULT_TOTAL_GAMES;
	
	public static void useLightTheme() {
		DARK_THEME = false;
	}
	public static void useDarkTheme() {
		DARK_THEME = true;
	}
	
	public static void setTotalGames(int totalGames) {
		TOTAL_GAMES_IN_A_MATCH = totalGames;
	}
	
	// by default, 1 starts at bottom right of screen, i.e. quadrant 4.
	public static Quadrant getMainQuadrant() {
		return Quadrant.BOTTOM_RIGHT;
	}
	
	public static Quadrant getWhiteHomeQuadrant() {
		return getMainQuadrant();
	}
	
	public static Quadrant getBlackHomeQuadrant() {
		Quadrant quadrant = null;
		switch (getMainQuadrant()) {
			case BOTTOM_RIGHT:
				quadrant = Quadrant.TOP_RIGHT;
				break;
			case BOTTOM_LEFT:
				quadrant = Quadrant.TOP_LEFT;
				break;
			case TOP_RIGHT:
				quadrant = Quadrant.BOTTOM_RIGHT;
				break;
			case TOP_LEFT:
				quadrant = Quadrant.BOTTOM_LEFT;
				break;
			default:
		}
		return quadrant;
	}
	
	public static Color getTopPerspectiveColor() {
		return Color.BLACK;
	}
	
	public static Color getBottomPerspectiveColor() {
		return Color.WHITE;
	}
	
	public static int getTopBearOffBoundary() {
		return GameConstants.NUMBER_OF_PIPS;
	}
	
	public static int getBottomBearOffBoundary() {
		return -1;
	}
	
	private static int getTopBearOnBoundary() {
		return getBottomBearOffBoundary();
	}
	
	private static int getBottomBearOnBoundary() {
		return getTopBearOffBoundary();
	}
	
	public static int getPipBearOnBoundary(PlayerPerspectiveFrom pov) {
		int bound = 0;
		switch (pov) {
			case BOTTOM:
				bound = getBottomBearOnBoundary();
				break;
			case TOP:
				bound = getTopBearOnBoundary();
				break;
			default:
				throw new PlayerNoPerspectiveException();
		}
		return bound;
	}
	
	public static int getPipBearOffBoundary(PlayerPerspectiveFrom pov) {
		int bound = 0;
		switch (pov) {
			case BOTTOM:
				bound = getBottomBearOffBoundary();
				break;
			case TOP:
				bound = getTopBearOffBoundary();
				break;
			default:
				throw new PlayerNoPerspectiveException();
		}
		return bound;
	}
	
	public static String getDefaultPlayerName(PlayerPerspectiveFrom pov) {
		String name = null;
		switch (pov) {
			case BOTTOM:
				name = "Cup";
				break;
			case TOP:
				name = "Tea";
				break;
			default:
				throw new PlayerNoPerspectiveException();
		}
		return name;
	}
}
