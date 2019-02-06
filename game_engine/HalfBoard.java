package game_engine;

import javafx.scene.layout.BorderPane;

/**
 * This class represents half of the board of the Backgammon.
 * 
 * This level of modularization allows us to increase customization. 
 * 
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
public class HalfBoard extends BorderPane {
	public HalfBoard() {
		super();
		double halfBoardWidth = Settings.getHalfBoardSize().getWidth();
		double halfBoardHeight = Settings.getHalfBoardSize().getHeight();
		
		setPrefSize(halfBoardWidth, halfBoardHeight);
		setStyle(Settings.getBoardColour());
	}
}
