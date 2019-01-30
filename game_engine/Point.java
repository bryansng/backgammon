package game_engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.VBox;

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
	private VBox point;
	
	/**
	 * Default Constructor
	 * 		- Get the image of the point.
	 * 		- Initialize point object.
	 * 		- Set point object's transformation, alignment, size, etc.
	 * 		- Set that image to be the background of the point object.
	 * @param color - color of the point.
	 * @param rotation either 0 or 180. 0 = pointing upwards. 180 = pointing downwards. 
	 */
	public Point(String color, double rotation) {
		super();
		Image img = null;
		FileInputStream input = null;
		try {
			if (color.equals("BLACK")) {
				input = new FileInputStream("src/img/board/black_point.png");
			}
			else if (color.equals("WHITE")) {
				input = new FileInputStream("src/img/board/white_point.png");
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		img = new Image(input);
		point = new VBox();
		point.setRotate(rotation);
		point.setAlignment(Pos.BOTTOM_CENTER);
		// don't simply set point max and pref size, this will effect how the point is drawn.
		point.setMinSize(img.getWidth(), img.getHeight());
		point.setBackground(new Background(new BackgroundImage(img, null, null, null, null)));
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
				ImageView checker = chk.getChecker();
				// You can reverse their order by negating yOffset and add to rear instead.
				checker.setTranslateY(yOffset*i);
				point.getChildren().add(0, checker);	// add to the front instead of rear.
				i++;
			}
		} else {
			for (Checker chk : this) {
				point.getChildren().add(chk.getChecker());
			}
		}
	}
	
	/**
	 * Returns the point instance variable.
	 * @return the point.
	 */
	public VBox getPoint() {
		return point;
	}
}
