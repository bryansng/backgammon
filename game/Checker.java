package game;

import java.io.IOException;
import java.io.InputStream;
import interfaces.ColorParser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * This class represents the Checker object in Backgammon from the UI POV.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Checker extends ImageView implements ColorParser, Touchable {
	private Image img;
	private Image imgHighlighted;
	private Color color;
	
	/**
	 * Default Constructor
	 * 		- Initialize the img and img_highlighted instance variable of the checker.
	 * @param color of checker.
	 */
	public Checker(Color color) {
		this(color, false);
	}
	public Checker(Color color, boolean isIcon) {
		super();
		this.color = color;
		initImg(isIcon);
	}
	
	/**
	 * - Get the image of the checker.
	 * - Initialize img and img_highlighted instance variable.
	 */
	private void initImg(boolean isIcon) {
		String colorString = parseColor(color);
		if (isIcon) colorString += "_icon";
		InputStream input1 = getClass().getResourceAsStream("img/checkers/" + colorString + "_checkers.png");
		InputStream input2 = getClass().getResourceAsStream("img/checkers/" + colorString + "_checkers_highlighted.png");
		img = new Image(input1);
		imgHighlighted = new Image(input2);
		try {
			input1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			input2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 * Returns the color of this checker.
	 * @return color of the checker.
	 */
	public Color getColor() {
		return color;
	}
}
