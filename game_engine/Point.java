package game_engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;

/**
 * This class represents the Point/Pipe object in Backgammon.
 * This class helps Board class to initialize the checkers for each point object.
 * This class also add the checkers objects to the point object, to be drawn to the stage.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Point extends CheckersStorer {
	private Image img;
	private Image imgHighlighted; 
	private int pointNum;
	
	/**
	 * Default Constructor
	 * 		- Initialize the img and imgHighlighteded instance variable of the checker.
	 * 		- Set this point's transformation, alignment, size, etc.
	 * 		- Set that img to be the background of this point.
	 * 		- Initialize this point's listeners.
	 * 
	 * @param color of the point.
	 * @param rotation either 0 or 180. 0 = pointing upwards. 180 = pointing downwards. 
	 */
	public Point(String colour, double rotation, int pointNum) {
		super();
		this.pointNum = pointNum;
		FileInputStream input1 = null;
		FileInputStream input2 = null;
		colour.toLowerCase();
		try {
			input1 = new FileInputStream("src/img/board/" + colour + "_point.png");
			input2 = new FileInputStream("src/img/board/" + colour + "_point_highlighted.png");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		img = new Image(input1);
		imgHighlighted = new Image(input2);
		
		setRotate(rotation);
		setAlignment(Pos.BOTTOM_CENTER);
		// don't simply set point max and pref size, this will effect how the point is drawn.
		setMinSize(img.getWidth(), img.getHeight());	// highlighted and non-highlighted should have the same width & height.
		setNormalImage();
	}
	
	/**
	 * Use the highlighted image.
	 */
	public void setHighlightImage() {
		setBackground(new Background(new BackgroundImage(imgHighlighted, null, null, null, null)));
	}

	/**
	 * Use the normal image (i.e. image that is not highlighted).
	 */
	public void setNormalImage() {
		setBackground(new Background(new BackgroundImage(img, null, null, null, null)));
	}
	
	/**
	 * Initialize num number of checkers with the checkerColour and pushes them to the stack.
	 * Then draw the checkers (i.e. add them to the point object that will be drawn on the stage).
	 * @param num number of checkers.
	 * @param checkerColour colour of the checkers.
	 */
	public void initCheckers(int num, String checkerColour) {
		for (int i = 0; i < num; i++) {
			push(new Checker(checkerColour));
		}
		drawCheckers();
	}
	
	/**
	 * Returns the pointNum instance variable (the number the point represents).
	 * @return the pointNum instance variable.
	 */
	public int getPointNumber() {
		return pointNum;
	}
}