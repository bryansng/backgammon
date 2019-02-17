package game_engine;

import events.CheckersStorerSelectedEvent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * This class should be extended by game components that will store checkers.
 * 
 * This class has all the common functions that will be needed by game components
 * to draw checkers.
 * 
 * This class extends Stack, of which extends VBox.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class CheckersStorer extends Stack<Checker> {
	public CheckersStorer() {
		super();
		initListeners();
	}
	
	/**
	 * Manages the listener of checkers storer.
	 */
	private void initListeners() {
		// Fires an event to MainController's checkers storer listener when this storer is mouse clicked.
		// Along with the event, the checkers storer object is passed in as the parameter to MainController.
		addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			this.fireEvent(new CheckersStorerSelectedEvent(this));
			
			// consume event before it gets to MainController, of which has other listeners relying on mouse clicks.
			event.consume();
		});
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
	 * Returns a boolean value indicating if the two checkers storer's top checkers are of the same color.
	 * If the other object is empty, return true as well.
	 * @param object, the other checker storer to be compared with.
	 * @return the boolean value.
	 */
	public boolean topCheckerColourEquals(CheckersStorer otherObject) {
		if (otherObject.isEmpty()) {
			return true;
		}
		return (top().getColor()).equals(otherObject.top().getColor());
	}
	
	/**
	 * Returns a boolean value indicating if the checkers storer's top checkers has the same color as given color.
	 * @param color given color to check.
	 * @return the boolean value.
	 */
	public boolean topCheckerColourEquals(Color color) {
		return (top().getColor()).equals(color);
	}
}
