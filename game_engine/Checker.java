package game_engine;

import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class represents the Checker object in Backgammon from the UI POV.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
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
		colour = colour.toLowerCase();
		InputStream input1 = getClass().getResourceAsStream("img/checkers/" + colour + "_checkers.png");
		InputStream input2 = getClass().getResourceAsStream("img/checkers/" + colour + "_checkers_highlighted.png");
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
