package game;

import constants.GameConstants;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

/**
 * This class represents the homes where the checkers will bear-off to.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Home extends CheckersStorer {
	private Color checkerColorsToStore;

	public Home(Color color) {
		super();
		checkerColorsToStore = color;
		
		double pointHeight = GameConstants.getPipSize().getHeight();
		double pointWidth = GameConstants.getPipSize().getWidth();
		setMinSize(pointWidth, pointHeight);
		setMaxSize(pointWidth, pointHeight);
		setAlignment(Pos.BOTTOM_CENTER);
		unhighlight();
	}
	
	/**
	 * Returns the color that this home in particular represents.
	 * @return color that this home represents.
	 */
	public Color getColor() {
		return checkerColorsToStore;
	}
	
	/**
	 * Returns a boolean value indicating if the home is filled with checkers.
	 * @return the boolean value.
	 */
	public boolean isFilled() {
		return size() == GameConstants.MAX_CHECKERS_PER_CHECKERS_STORER;
	}
	
	public void highlight() {
		setBackground(GameConstants.getBoardImage());
		setStyle("-fx-border-color: yellow; -fx-border-width: 3; -fx-border-style: solid;");
	}
	
	public void unhighlight() {
		setBackground(GameConstants.getBoardImage());
		setStyle("-fx-border-color: transparent; -fx-border-width: 3; -fx-border-style: solid;");
	}
}
