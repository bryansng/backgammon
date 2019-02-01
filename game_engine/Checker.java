package game_engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * This class represents the Checker object in Backgammon.
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
public class Checker extends ImageView {
	private Image img;
	private Image img_highlight;
	
	/**
	 * Default Constructor
	 * 		- Initialize the img and img_highlight instance variable of the checker.
	 * @param colour of checker.
	 */
	public Checker(String colour) {
		super();
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
		setNormalImage();
		setListeners();
	}
	
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
	
	// change point object background with the highlighted one.
	public void setHighlightImage() {
		setImage(img_highlight);
	}
	
	// change point object background with the non-highlighted one.
	public void setNormalImage() {
		setImage(img);
	}
}
