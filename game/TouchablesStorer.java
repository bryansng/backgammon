package game;

import events.TouchablesStorerSelectedEvent;
import javafx.scene.input.MouseEvent;

/**
 * This class should be extended by game components (bar, home, pips) that will store checkers.
 * This class has all the common functions that will be needed by game components to draw checkers.
 * This class extends Stack, of which extends VBox.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class TouchablesStorer extends Stack<Touchable> {
	public TouchablesStorer() {
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
			this.fireEvent(new TouchablesStorerSelectedEvent(this));
			
			// consume event before it gets to MainController,
			// of which has other listeners relying on mouse clicks.
			event.consume();
		});
	}
	
	public Checker getTopChecker() {
		Checker topChecker = null;
		if (top() instanceof Checker) {
			topChecker = (Checker) top();
		}
		return topChecker;
	}
	
	public void addThisCube(DoublingCube theCube) {
		push(theCube);
		drawCube();
	}
	
	public DoublingCube getTopCube() {
		DoublingCube topCube = null;
		if (top() instanceof DoublingCube) {
			topCube = (DoublingCube) top();
		}
		return topCube;
	}
	
	public void drawCube() {
		getChildren().clear();
		if (top() instanceof DoublingCube) {
			getChildren().add((DoublingCube) top());
		}
	}
	
	public DoublingCube popCube() {
		DoublingCube topCube = null;
		if (!isEmpty() && top() instanceof DoublingCube) {
			topCube = (DoublingCube) pop();
			drawCube();
		}
		return topCube;
	}
}
