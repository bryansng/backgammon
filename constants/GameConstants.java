package constants;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.InputStream;
import javafx.scene.image.Image;

/**
 * This class serves as a point where other class files share hard-coded data or relative data.
 * This means that every other class except this one will share positional data relative to data here.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class GameConstants {
	public static final boolean DEBUG_MODE = true;
	
	// force ways to init pips and checkers.
	public static final boolean FORCE_ONE_CHECKER = false;
	
	// force ways to init pips and checkers, for debug pipToHome moves.
	public static final boolean FORCE_CHECKERS_IN_HOME_BOARD = false;
	public static final boolean FORCE_CHECKERS_IN_HOME_BOARD_AT_PIP_5 = false;
	public static final boolean FORCE_LESS_CHECKERS_IN_HOME_BOARD = false;
	public static final boolean FORCE_LESSER_CHECKERS_IN_HOME_BOARD = false;
	public static final boolean FORCE_ONE_CHECKER_OUTSIDE_HOME_BOARD = false;
	
	// first roll after /start will not appear as doubles, but internally is.
	public static final boolean FORCE_DOUBLE_INSTANCE = false;
	
	public static final int MAX_CHECKERS_IN_GAME = 30;
	public static final int MAX_CHECKERS_PER_CHECKERS_STORER = MAX_CHECKERS_IN_GAME / 2;
	public static final int NUMBER_OF_PIPS = 24; 
	
	/**
	 * Screen sizes - Used to set scene width and height.
	 * @return size of screen.
	 */
	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	/**
	 * Takes the path of the file and returns its file input stream.
	 * @param path the path of the file.
	 * @return the file input stream.
	 */
	private static InputStream getFile(String path) {
		return GameConstants.class.getResourceAsStream("/game/" + path);
	}
	
	/**
	 * Currently hard-coded, we could create a image from illustrator, but this could work in the meantime.
	 * @return size of the board.
	 */
	public static Dimension getHalfBoardSize() {
		Dimension d = new Dimension();
		d.setSize(381, 589);
		return d;
	}
	
	/**
	 * Returns the size of the point based on its image.
	 * @return size of point.
	 */
	public static Dimension getPipSize() {
		Image img = new Image(getFile("img/board/white_point.png"));
		
		Dimension d = new Dimension();
		d.setSize(img.getWidth(), img.getHeight());
		return d;
	}
	
	/**
	 * Returns the size of the checker based on its image.
	 * @return size of checker.
	 */
	public static Dimension getCheckerSize() {
		Image img = new Image(getFile("img/checkers/white_checkers.png"));
		
		Dimension d = new Dimension();
		d.setSize(img.getWidth(), img.getHeight());
		return d;
	}
	
	/**
	 * Returns the size of the dice based on its image.
	 * @return size of dice.
	 */
	public static Dimension getDiceSize() {
		Image img = new Image(getFile("img/dices/black/1.png"));
		
		Dimension d = new Dimension();
		d.setSize(img.getWidth(), img.getHeight());
		return d;
	}
	
	public static final String GAME_COLOR = "saddlebrown";
	public static final String BOARD_COLOR = "forestgreen";
	
	/**
	 * Returns the colour of the board in terms of CSS.
	 * @return colour of the board.
	 */
	public static String getBoardColor() {
		String colour = "-fx-background-color: forestgreen;";
		return colour;
	}
	
	
	/**
	 * Returns the colour of the game in terms of CSS.
	 * @return colour of the game.
	 */
	public static String getGameColor() {
		String colour = "-fx-background-color: saddlebrown;";
		return colour;
	}
	
	/**
	 * Returns the height of the point labels.
	 * @return height of point label.
	 */
	public static double getPipNumberLabelHeight() {
		return 15.0;
	}
	
	public static double getMiddlePartWidth() {
		double width = getHomeMargin() * 4 + getBarMargin() * 2 + getHalfBoardSize().getWidth() * 2; 
		return width;
	}
	
	/**
	 * Returns the margin of the homes within home panel.
	 * @return margin of the homes.
	 */
	public static double getHomeMargin() {
		double margin = getPipNumberLabelHeight();
		return margin;
	}
	
	/**
	 * Returns the margin of the bars within bars.
	 * @return margin of the bars.
	 */
	public static double getBarMargin() {
		double margin = 5.0;
		return margin;
	}
	
	/**
	 * Top and bottom - part for player score and faces. Height currently hard-coded.
	 * @return height of player panel.
	 */
	public static double getPlayerPanelHeight() {
		// V gap between terminal and roll die button taken into consideration.
		// there's only 1 vgap, but two player panel, so we divide by 2.
		return 40.0 + getUIVGap()/2.0;
	}
	
	/**
	 * Returns the distance between two player labels.
	 * @return spacing of the player label.
	 */
	public static double getPlayerLabelSpacing() {
		double spacing = 50.0;
		return spacing;
	}
	
	
	/**
	 * Returns the vertical gap distance between command panel, info panel and roll die button.
	 * @return the vertical gap distance.
	 */
	public static double getUIVGap() {
		double vGap = 5.0;
		return vGap;
	}
	
	/**
	 * Returns the height for command panel and roll die button.
	 * @return the height.
	 */
	public static double getUIHeight() {
		return getPlayerPanelHeight() - getUIVGap();
	}
}
