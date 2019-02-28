package game_engine;

import constants.GameConstants;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * This class represents the pip number labels of the Backgammon game.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class PipNumberLabel extends Label {
	public PipNumberLabel(int pipNum) {
		super(Integer.toString(pipNum));
		setAlignment(Pos.CENTER);
		setTextFill(Color.WHITE);
		setMinSize(GameConstants.getPipSize().getWidth(), GameConstants.getPipNumberLabelHeight());
	}
}
