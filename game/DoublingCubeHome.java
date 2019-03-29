package game;

import constants.GameConstants;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

/**
 * This class represents the box where the doubling cube resides.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class DoublingCubeHome extends DoublingCubeStorer {
	private boolean isOnBoard;
	
	// Used to determine which side the cube home is at on the board.
	// This is correlated with the player's color.
	private Color playerColor;
	
	public DoublingCubeHome() {
		this(true, null);
	}
	public DoublingCubeHome(boolean addCube, Color playerColor) {
		super();
		
		double width = GameConstants.getPipSize().getWidth();
		setMinSize(width, width);
		setMaxSize(width, width);
		setAlignment(Pos.CENTER);
		unhighlight();
		
		if (addCube) {
			isOnBoard = false;
			addCube();
		} else {
			isOnBoard = true;
			this.playerColor = playerColor;
		}
	}
	
	public void highlight() {
		setBackground(GameConstants.getBoardImage());
		setStyle("-fx-border-color: yellow; -fx-border-width: 3; -fx-border-style: solid;");
	}
	
	public void unhighlight() {
		setBackground(GameConstants.getBoardImage());
		setStyle("-fx-border-color: transparent; -fx-border-width: 3; -fx-border-style: solid;");
	}
	
	public boolean isOnBoard() {
		return isOnBoard;
	}
	
	public Color getColor() {
		return playerColor;
	}
	
	public void reset() {
		if (isOnBoard) {
			removeCube();
		} else {
			addCube();
		}
	}
}
