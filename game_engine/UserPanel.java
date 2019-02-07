package game_engine;

import javafx.scene.layout.HBox;

/**
 * This class represents the panel that holds the player's information.
 * i.e. player name and score.
 * 
 * There will be one in the top and one at the bottom of the game.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class UserPanel extends HBox {
	public UserPanel(double width) {
		super();
		setPrefSize(width, Settings.getTopBottomHeight());
		setStyle("-fx-background-color: transparent;");
	}
}
