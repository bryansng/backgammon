package game_engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import events.PointSelectedEvent;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;

/**
 * This class represents the Point/Pipe object in Backgammon.
 * This class helps Board class to initialize the checkers for each point object.
 * This class also add the checkers objects to the point object, to be drawn to the stage.
 * 
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
public class Point extends Stack<Checker> {
	private Image img;
	private Image img_highlight; 
	private int pointNum;
	
	/**
	 * Default Constructor
	 * 		- Initialize the img and img_highlighted instance variable of the checker.
	 * 		- Set this point's transformation, alignment, size, etc.
	 * 		- Set that img to be the background of this point.
	 * 		- Initialize this point's listeners.
	 * 
	 * @param color of the point.
	 * @param rotation either 0 or 180. 0 = pointing upwards. 180 = pointing downwards. 
	 */
	public Point(String color, double rotation, int pointNum) {
		super();
		this.pointNum = pointNum;
		FileInputStream input1 = null;
		FileInputStream input2 = null;
		try {
			if (color.equals("BLACK")) {
				input1 = new FileInputStream("src/img/board/black_point.png");
				input2 = new FileInputStream("src/img/board/black_point_highlighted.png");
			}
			else if (color.equals("WHITE")) {
				input1 = new FileInputStream("src/img/board/white_point.png");
				input2 = new FileInputStream("src/img/board/white_point_highlighted.png");
			}
			img = new Image(input1);
			img_highlight = new Image(input2);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		setRotate(rotation);
		setAlignment(Pos.BOTTOM_CENTER);
		// don't simply set point max and pref size, this will effect how the point is drawn.
		setMinSize(img.getWidth(), img.getHeight());	// highlighted and non-highlighted should have the same width & height.
		setNormalImage();
		setListeners();
	}
	
	/**
	 * Manages the listener of point.
	 */
	private void setListeners() {
		// Fires an event to MainController's point listener when this point is mouse clicked.
		// Along with the event, the point number is passed in as the parameter to MainController.
		addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			this.fireEvent(new PointSelectedEvent(pointNum));
		});
	}
	
	/**
	 * Use the highlighted image.
	 */
	public void setHighlightImage() {
		setBackground(new Background(new BackgroundImage(img_highlight, null, null, null, null)));
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
	 * Handles how the checkers are positioned in the point object.
	 * (i.e. how it will be drawn eventually on the stage).
	 */
	public void drawCheckers() {
		// Clear the point object of any children.
		getChildren().clear();
		
		// If total height of checkers greater than point, we overlap the checkers.
		int numCheckers = size();
		double slack = Settings.getPointSize().getHeight() * 0.2;
		double diff = numCheckers * Settings.getCheckerSize().getHeight() - Settings.getPointSize().getHeight() + slack;
		
		// If overlap, we basically add an y offset to the checkers so that they overlap each other.
		// Else, we simply add them to the point without any offsets.
		if (diff >= 0) {
			int i = 0;
			double yOffset = (diff / numCheckers);
			for (Checker chk : this) {
				ImageView checker = chk;
				checker.setTranslateY(yOffset*(numCheckers-i-1));
				getChildren().add(checker);
				i++;
			}
		} else {
			for (Checker chk : this) {
				ImageView checker = chk;
				checker.setTranslateY(0);
				getChildren().add(checker);
			}
		}
	}
	
	/**
	 * Returns the pointNum instance variable (the number the point represents).
	 * @return the pointNum instance variable.
	 */
	public int getPointNumber() {
		return pointNum;
	}
}
