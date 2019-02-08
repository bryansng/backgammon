package game_engine;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.InputStream;

import javafx.scene.image.Image;

/**
 * This class serves as a point where other class files share hard-coded data or relative data.
 * This allows us to keep track of any user-specified data.
 * This means that every other class except this one will share positional data relative to
 * data here.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Settings {
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
		return Settings.class.getResourceAsStream(path);
	}
	
	/**
	 * Top and bottom - part for user score and faces. Height currently hard-coded.
	 * @return height of top/bottom.
	 */
	public static double getTopBottomHeight() {
		return 50.0;
	}
	
	/**
	 * Currently hard-coded, we could create a image from illustrator, but this could work in the meantime.
	 * @return size of the board.
	 */
	public static Dimension getHalfBoardSize() {
		Dimension d = new Dimension();
		d.setSize(389, 639);
		return d;
	}
	
	/**
	 * Returns the size of the point based on its image.
	 * @return size of point.
	 */
	public static Dimension getPointSize() {
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
	
	/**
	 * Returns the total number of checkers allowed in the game by both players.
	 * @return the total number of checkers.
	 */
	public static int getPointMaxSize() {
		int totalNumberOfCheckers = 30;
		return totalNumberOfCheckers;
	}
	
	/**
	 * Returns the colour of the board in terms of CSS.
	 * @return colour of the board.
	 */
	public static String getBoardColour() {
		String colour = "-fx-background-color: forestgreen;";
		return colour;
	}
	
	
	/**
	 * Returns the colour of the game in terms of CSS.
	 * @return colour of the game.
	 */
	public static String getGameColour() {
		String colour = "-fx-background-color: saddlebrown;";
		return colour;
	}
	
	/**
	 * Returns the height of the point labels.
	 * @return height of point label.
	 */
	public static double getPointNumberLabelHeight() {
		return 15.0;
	}
	
	/**
	 * Returns the margin of the homes within home panel.
	 * @return margin of the homes.
	 */
	public static double getHomeMargin() {
		double margin = getPointNumberLabelHeight();
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
	 * Returns the distance between two player labels.
	 * @return spacing of the player label.
	 */
	public static double getPlayerLabelSpacing() {
		double spacing = 50.0;
		return spacing;
	}
	
	/**
	 * Returns the number of points in the game.
	 * @return the number of points
	 */
	public static int getNumberOfPoints() {
		int numberOfPoints = 24;
		return numberOfPoints;
	}
}
