package game_engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class represents the Dice object in Backgammon from the UI POV.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Dice extends ImageView {
	private final int MAX_DICE_SIZE = 6;
	private Image[] dices;
	
	/**
	 * Default Constructor
	 * 		- Initialize the dices array with the possible dice images (i.e. 1-6).
	 * @param colour
	 */
	public Dice(String colour) {
		super();
		dices = new Image[MAX_DICE_SIZE];
		initImages(colour);
	}
	
	/**
	 * Initializes the dice images:
	 * 		- by getting the images from file,
	 * 		- adding them to the dices array.
	 * @param colour
	 */
	private void initImages(String colour) {
		FileInputStream input = null;
		colour.toLowerCase();
		try {
			for (int i = 0; i < dices.length; i++) {
				input = new FileInputStream("src/img/dices/" + colour + "/" + (i+1) + ".png");
				dices[i] = new Image(input);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the image of dice based on result.
	 * i.e. If result is 1, show image with dice at 1.
	 * @param result of rolling the dice.
	 */
	private void draw(int result) {
		setImage(dices[result-1]);
	}
	
	/**
	 * Get the roll dice result (i.e. number from 1 to 6),
	 * draw the dice with the result,
	 * return the result.
	 * @return the result of rolling the dice.
	 */
	public int roll() {
		Random rand = new Random();
		int res = rand.nextInt(MAX_DICE_SIZE) + 1;
		draw(res);
		return res;
	}
}
