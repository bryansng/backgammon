package game;

import constants.GameConstants;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class CheckersStorer extends TouchablesStorer {
	public CheckersStorer() {
		super();
	}
	
	/**
	 * Initialize num number of checkers with the checkerColor and pushes them to the stack.
	 * Then draw the checkers (i.e. add them to the point object that will be drawn on the stage).
	 * @param num number of checkers.
	 * @param checkerColor color of the checkers.
	 */
	public void initCheckers(int num, Color checkerColor) {
		removeCheckers();
		
		for (int i = 0; i < num; i++) {
			push(new Checker(checkerColor));
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
		double slack = GameConstants.getPipSize().getHeight() * 0.2;
		double diff = numCheckers * GameConstants.getCheckerSize().getHeight() - GameConstants.getPipSize().getHeight() + slack;
		
		if (top() instanceof Checker) {
			// If overlap, we basically add an y offset to the checkers so that they overlap each other.
			// Else, we simply add them to the point without any offsets.
			if (diff >= 0) {
				int i = 0;
				double yOffset = (diff / numCheckers);
				for (Touchable chk : this) {
					ImageView checker = (Checker) chk;
					checker.setTranslateY(yOffset*(numCheckers-i-1));
					checker.setViewOrder(i);	// lower order - higher z-index, i.e. order 1 overlaps order 2.
					getChildren().add(checker);
					i++;
				}
			} else {
				for (Touchable chk : this) {
					ImageView checker = (Checker) chk;
					checker.setTranslateY(0);
					getChildren().add(checker);
				}
			}
		}
	}
	
	/**
	 * Returns a boolean value indicating if the two checkers storer's top checkers are of the same color.
	 * If the other object is empty, return true as well.
	 * @param object, the other checker storer to be compared with.
	 * @return the boolean value.
	 */
	public boolean topCheckerColorEquals(CheckersStorer otherObject) {
		if (otherObject.isEmpty()) {
			return true;
		}
		return getTopChecker().getColor().equals(otherObject.getTopChecker().getColor());
	}
	
	/**
	 * Returns a boolean value indicating if the checkers storer's top checkers has the same color as given color.
	 * @param color given color to check.
	 * @return the boolean value.
	 */
	public boolean topCheckerColorEquals(Color color) {
		return getTopChecker().getColor().equals(color);
	}
	
	/**
	 * Removes all checkers in the storer (pop off stack).
	 */
	public void removeCheckers() {
		getChildren().clear();
		clear();
		drawCheckers();
	}
}
