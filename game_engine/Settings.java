package game_engine;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;

/**
 * This class serves as a point where other class files share hard-coded data or relative data.
 * This allows us to keep track of any user-specified data.
 * This means that every other class except this one will share positional data relative to
 * data here.
 * @author Bryan Sng
 * @email sngby98@gmail.com
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
	private static FileInputStream getFile(String path) {
		FileInputStream input = null;
		try {
			input = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return input;
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
		Image img = new Image(getFile("src/img/board/white_point.png"));
		
		Dimension d = new Dimension();
		d.setSize(img.getWidth(), img.getHeight());
		return d;
	}
	
	/**
	 * Returns the size of the checker based on its image.
	 * @return size of checker.
	 */
	public static Dimension getCheckerSize() {
		Image img = new Image(getFile("src/img/checkers/white_checkers.png"));
		
		Dimension d = new Dimension();
		d.setSize(img.getWidth(), img.getHeight());
		return d;
	}
	
	/**
	 * Returns the size of the dice based on its image.
	 * @return size of dice.
	 */
	public static Dimension getDiceSize() {
		Image img = new Image(getFile("src/img/dices/black/1.png"));
		
		Dimension d = new Dimension();
		d.setSize(img.getWidth(), img.getHeight());
		return d;
	}
}
