package game_engine;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/**
 * This class represents a HBox of dices.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Dices extends HBox {
	private Dice[] dices;
	
	/**
	 * Default Constructor
	 * 		- Initialize the dices array with two dices.
	 * @param colour of dices
	 */
	public Dices(String colour) {
		this(colour, 2);
	}
	
	/**
	 * Overloaded Constructor
	 * 		- Initialize the dices array with any number of dices.
	 * 		- Set this node's alignment.
	 * @param colour of dices.
	 * @param numberOfDices number of dices. 
	 */
	public Dices(String colour, int numberOfDices) {
		super();
		dices = new Dice[numberOfDices];
		initDices(colour);
		setAlignment(Pos.CENTER);
	}
	
	/**
	 * Initialize the individual dices and assign them into the dices array.
	 * @param colour of dices.
	 */
	private void initDices(String colour) {
		for (int i = 0; i < dices.length; i++) {
			dices[i] = new Dice(colour);
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
	
	// upon calling, removes the double cube instances (i.e. set them to null).
	// and remove them from the nodes.
	public void removeDoubleCube() {
		
	}
	*/
}
