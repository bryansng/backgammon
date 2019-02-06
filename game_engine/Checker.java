package game_engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class represents the Checker object in Backgammon from the UI POV.
 * 
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
public class Checker extends ImageView {
	private Image img;
	private Image imgHighlighted;
	private String colour;
	
	/**
	 * Default Constructor
	 * 		- Initialize the img and img_highlighted instance variable of the checker.
	 * @param colour of checker.
	 */
	public Checker(String colour) {
		super();
		this.colour = colour;
		initImg();
	}
	
	/**
	 * - Get the image of the checker.
	 * - Initialize img and img_highlighted instance variable.
	 */
	private void initImg() {
		FileInputStream input1 = null;
		FileInputStream input2 = null;
		colour.toLowerCase();
		try {
			input1 = new FileInputStream("src/img/checkers/" + colour + "_checkers.png");
			input2 = new FileInputStream("src/img/checkers/" + colour + "_checkers_highlighted.png");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		img = new Image(input1);
		imgHighlighted = new Image(input2);
		
		setNormalImage();
	}
	
	/**
	 * Use the highlighted image.
	 */
	public void setHighlightImage() {
		setImage(imgHighlighted);
	}
	
	/**
	 * Use the normal image (i.e. image that is not highlighted).
	 */
	public void setNormalImage() {
		setImage(img);
	}
	
	/**
	 * Returns the colour of this checker.
	 * @return colour of the checker.
	 */
	public String getColour() {
		return colour;
	}
}
