package game_engine;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * This class represents a HBox of dices.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
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
		dices = new Dice[numberOfDices];
		setAlignment(Pos.CENTER);
		setSpacing(Settings.getDiceSize().getWidth() / 4.0);
		initDices(color);
	}
	
	/**
	 * Initialize the individual dices and assign them into the dices array.
	 * @param color of dices.
	 */
	private void initDices(Color color) {
		for (int i = 0; i < dices.length; i++) {
			dices[i] = new Dice(color);
			getChildren().add(dices[i]);
		}
	}
	
	/**
	 * Return an array of integers, containing the result of each dice roll.
	 * @return result of each dice roll in terms of an array of integers.
	 */
	public int[] getTotalRoll() {
		int[] res = new int[dices.length];
		for (int i = 0; i < dices.length; i++) {
			res[i] = dices[i].roll();
		}
		return res;
	}
	
	/* IGNORE FIRST, NOT PART OF SPRINT SCOPE.
	 * This is considered for handling doubling cubes.
	// upon calling, this method should double current cube objects in dices.
	public void doubleCube() {
		
	}
	*/
	
	// upon calling, removes the double cube instances (i.e. set them to null).
	// and remove them from the nodes.
	/**
	 * 
	 */
	public void removeDoubleCube() {
		getChildren().clear();
		initDices(color);
	}
}
