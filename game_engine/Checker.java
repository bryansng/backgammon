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
	
	/**
	 * Default Constructor
	 * 		- Initialize the img and img_highlighted instance variable of the checker.
	 * @param colour of checker.
	 */
	public Checker(String colour) {
		super();
		initImg(colour);
	}
	
	/**
	 * - Get the image of the checker.
	 * - Initialize img and img_highlighted instance variable.
	 * @param color of checker.
	 */
	private void initImg(String colour) {
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
		setListeners();
	}
	
	// IGNORE THIS FOR NOW, I JUST MIGHT NEED IT IN THE FUTURE.
	// catch the event, and not let the event dispatch chain reach the checkers.
	private void setListeners() {
		/*
		addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			event.consume();
		});
		
		// stop the capturing phase, let it just reach until point, so that gettarget returns only the point, not the checkers when clicked.
		addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			event.consume();
		});
		*/
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
}
