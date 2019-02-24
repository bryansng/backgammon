package game_engine;

import constants.GameConstants;
import javafx.scene.layout.BorderPane;

/**
 * This class represents half of the board of the Backgammon.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class HalfBoard extends BorderPane {
	public HalfBoard() {
		super();
		double halfBoardWidth = GameConstants.getHalfBoardSize().getWidth();
		double halfBoardHeight = GameConstants.getHalfBoardSize().getHeight();
		
		setPrefSize(halfBoardWidth, halfBoardHeight);
		setStyle(GameConstants.getBoardColor());
	}
}
