package game_engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class represents the Checker object in Backgammon.
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
public class Checker {
	private Image img;
	
	/**
	 * Default Constructor
	 * 		- Initialize the img instance variable of the checker.
	 * @param colour of checker.
	 */
	public Checker(String colour) {
		initImg(colour);
	}
	
	/**
	 * - Get the image of the checker.
	 * - Initialize img instance variable.
	 * @param color of checker.
	 */
	public void initImg(String color) {
		FileInputStream input = null;
		try {
			if (color.equals("BLACK")) {
				input = new FileInputStream("src/img/checkers/black_checkers.png");
			}
			else if (color.equals("WHITE")) {
				input = new FileInputStream("src/img/checkers/white_checkers.png");
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		img = new Image(input);
	}

	/**
	 * Returns the an ImageView of the img instance variable.
	 * @return ImageView of the checker image.
	 */
	public ImageView getChecker() {
		return new ImageView(img);
	}
}
