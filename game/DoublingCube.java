package game;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import interfaces.ColorPerspectiveParser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class represents the doubling cube object in Backgammon game.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class DoublingCube extends ImageView implements ColorPerspectiveParser, Touchable {
	private final int MAX_DICE_SIZE = 6;
	private Image[] cubeSides;
	private Image[] cubeHighlightedSides;
	private int currentSide;
	private boolean isMaxDoubling, isUsed;
	
	/**
	 * Constructors
	 * 		- Initialize the dices array with the possible cube images (i.e. 2,4,8,16,32,64).
	 */
	public DoublingCube() {
		super();
		initImages();
		//initListeners();
		reset();
	}
	
	/**
	 * Manages the listener of checkers storer.
	 */
	/*
	private void initListeners() {
		// Fires an event to MainController's checkers storer listener when this storer is mouse clicked.
		// Along with the event, the checkers storer object is passed in as the parameter to MainController.
		addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			this.fireEvent(new TouchableSelectedEvent(this));
			
			// consume event before it gets to MainController,
			// of which has other listeners relying on mouse clicks.
			event.consume();
		});
	}
	*/
	
	/**
	 * Initializes the dice images:
	 * 		- by getting the images from file,
	 * 		- adding them to the dices array.
	 * @param color
	 */
	private void initImages() {
		cubeSides = new Image[MAX_DICE_SIZE];
		cubeHighlightedSides = new Image[MAX_DICE_SIZE];
		for (int i = 0; i < cubeSides.length; i++) {
			int side = (int) Math.pow(2.0, i+1);		// formula for 2,4,8,16,32,64.
			try {
				InputStream input1 = getClass().getResourceAsStream("img/double_cube/" + side + ".png");
				InputStream input2 = getClass().getResourceAsStream("img/double_cube/" + side + "_highlighted.png");
				cubeSides[i] = new Image(input1);
				cubeHighlightedSides[i] = new Image(input2);
				input1.close();
				input2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Use the highlighted image.
	 */
	public void setHighlightImage() {
		setImage(cubeHighlightedSides[currentSide]);
	}
	
	/**
	 * Use the normal image (i.e. image that is not highlighted).
	 * Set the image of dice based on result.
	 * i.e. If result is 2, show image with doubling cube at 2.
	 */
	public void setNormalImage() {
		setImage(cubeSides[currentSide]);
	}
	
	/**
	 * Rotate the doubling cube image only when it is on the board.
	 */
	public void rotateOnBoard() {
		// rotation range of 15 to -15.
		Random rand = new Random();
		int rotation = rand.nextInt(30) - 15 + 1;
		setRotate(rotation);
	}
	
	public void doubleDoublingCube() {
		// allow double if less than max_dice_size-1.
		if (currentSide < MAX_DICE_SIZE-1) currentSide += 1;
		setNormalImage();
		
		if (currentSide == MAX_DICE_SIZE-1) isMaxDoubling = true;
	}
	
	// for declined doubling cube multiplier.
	public int getIntermediateGameMultiplier() {
		if (isUsed)
			return (int) Math.pow(2.0, currentSide);
		return 1;
	}
	
	// for game end multiplier.
	public int getEndGameMultiplier() {
		if (isUsed)
			return (int) Math.pow(2.0, currentSide+1);
		return 1;
	}
	
	public boolean isMaxDoubling() {
		return isMaxDoubling;
	}
	
	public boolean isUsed() {
		return isUsed;
	}
	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
		currentSide = 0;
		setNormalImage();
	}
	
	public void resetRotation() {
		setRotate(0.0);
	}
	
	public void reset() {
		currentSide = MAX_DICE_SIZE-1;
		isMaxDoubling = false;
		isUsed = false;
		setNormalImage();
		resetRotation();
	}
}
