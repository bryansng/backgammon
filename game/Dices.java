package game;

import constants.DieInstance;
import constants.GameConstants;
import interfaces.ColorParser;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * This class represents a HBox of dices.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Dices extends HBox implements ColorParser {
	private Dice[] dices;
	private Color color;
	
	/**
	 * Default Constructor
	 * 		- Initialize the dices array with two dices.
	 * @param color of dices
	 */
	public Dices(Color color) {
		this(color, 2);
	}
	
	/**
	 * Overloaded Constructor
	 * 		- Initialize the dices array with any number of dices.
	 * 		- Set this node's alignment.
	 * @param color of dices.
	 * @param numberOfDices number of dices. 
	 */
	public Dices(Color color, int numberOfDices) {
		super();
		this.color = color;
		dices = new Dice[numberOfDices*2];	// times 2 to leave space for double die instances.
		setAlignment(Pos.CENTER);
		setSpacing(GameConstants.getDiceSize().getWidth() / 4.0);
		initDices();
		drawDices(DieInstance.DOUBLE);
	}
	
	/**
	 * Initialize the individual dices and assign them into the dices array.
	 */
	private void initDices() {
		for (int i = 0; i < dices.length; i++) {
			dices[i] = new Dice(color);
		}
	}
	
	/**
	 * Draw the die to the board, provided this HBox is drawn as well.
	 * @param instance instance where the dices are single, double or default.
	 */
	private void drawDices(DieInstance instance) {
		getChildren().clear();
		int numDices = getNumDices(instance);
		for (int i = 0; i < numDices; i++) {
			getChildren().add(dices[i]);
		}
	}
	
	/**
	 * Returns an array of integers, containing the result of each dice roll.
	 * @return result of each dice roll in terms of an array of integers.
	 */
	public DieResults getTotalRoll(DieInstance instance) {
		int numDices = getNumDices(instance);
		DieResults res = new DieResults();
		for (int i = 0; i < numDices; i++) {
			res.add(dices[i].draw(dices[i].roll()));
		}
		drawDices(instance);
		
		if (isDouble(res)) {
			res = addDoubleDie(res);
		}
		return res;
	}
	
	/**
	 * Checks if result of die roll is a double instance.
	 * @param res, result of die roll.
	 * @return boolean value indicating if so.
	 */
	private boolean isDouble(DieResults res) {
		boolean isDouble = true;
		// can't be double if only 1 dice.
		if (res.size() > 1) {
			Dice prev = res.getFirst();
			for (Dice curr : res) {
				if (!prev.equalsValueOf(curr)) {
					isDouble = false;
					break;
				}
				prev = curr;
			}
		} else {
			isDouble = false;
		}
		return isDouble;
	}
	
	/**
	 * Doubles the current cube objects in dices as well as the roll die result.
	 * @param res the roll die result.
	 * @return double the roll die result.
	 */
	private DieResults addDoubleDie(DieResults res) {
		int numberOfDices = getNumDices(DieInstance.DOUBLE);
		DieResults newRes = new DieResults();
		for (int i = 0; i < numberOfDices; i++) {
			newRes.add(dices[i].draw(res.getFirst().getDiceResult()));
		}
		drawDices(DieInstance.DOUBLE);
		return newRes;
	}
	
	/**
	 * Returns the number of dices based on the die instance.
	 * @param instance instance where the dices are single, double or default.
	 * @return number of dices.
	 */
	private int getNumDices(DieInstance instance) {
		int numDices = 0;
		switch (instance) {
			case SINGLE:
				numDices = 1;
				break;
			case DOUBLE:
				numDices = dices.length;
				break;
			case DEFAULT:
				numDices = dices.length/2;
				break;
		}
		return numDices;
	}
	
	// Used to hard-create double rolls, added in Board's calculateMoves() method.
	// Activated by FORCE_DOUBLE_INSTANCE constant in GameConstants.
	public DieResults getDoubleRoll(DieInstance instance) {
		int numberOfDices = getNumDices(instance);
		int randomRoll = dices[0].roll();
		DieResults res = new DieResults();
		for (int i = 0; i < numberOfDices; i++) {
			res.add(dices[i].draw(randomRoll));
		}
		drawDices(instance);
		return res;
	}
	
	// Used to hard-create double rolls of ones, added in Board's calculateMoves() method.
	// Activated by FORCE_DOUBLE_ONES constant in GameConstants.
	public DieResults getDoubleOnes(DieInstance instance) {
		int numberOfDices = getNumDices(instance);
		DieResults res = new DieResults();
		for (int i = 0; i < numberOfDices; i++) {
			res.add(dices[i].draw(1));
		}
		drawDices(instance);
		return res;
	}
	
	// Used to hard-create double rolls of ones, added in Board's calculateMoves() method.
	// Activated by FORCE_DOUBLE_TWOS constant in GameConstants.
	public DieResults getDoubleTwos(DieInstance instance) {
		int numberOfDices = getNumDices(instance);
		DieResults res = new DieResults();
		for (int i = 0; i < numberOfDices; i++) {
			res.add(dices[i].draw(2));
		}
		drawDices(instance);
		return res;
	}
	
	public void reset() {
		getChildren().clear();
	}
}
