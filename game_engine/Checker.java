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
	private Image img_highlight; 
	private boolean isHighlight;
	
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
	private void initImg(String color) {
		FileInputStream input1 = null;
		FileInputStream input2 = null;
		try {
			if (color.equals("BLACK")) {
				input1 = new FileInputStream("src/img/checkers/black_checkers.png");
				input2 = new FileInputStream("src/img/checkers/black_checkers_highlighted.png");
			}
			else if (color.equals("WHITE")) {
				input1 = new FileInputStream("src/img/checkers/white_checkers.png");
				input2 = new FileInputStream("src/img/checkers/white_checkers_highlighted.png");
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		img = new Image(input1);
		img_highlight = new Image(input2);
		isHighlight = false;
	}

	/**
	 * Returns the an ImageView of the img instance variable.
	 * @return ImageView of the checker image.
	 */
	public ImageView getChecker() {
		if (!isHighlight) {
			return new ImageView(img);
		} else {
			return new ImageView(img_highlight);
		}
	}
	
	// change point object background with the highlighted one.
	public void setHighlightImage() {
		isHighlight = true;
	}
	
	// change point object background with the non-highlighted one.
	public void setNormalImage() {
		isHighlight = false;
	}
}
