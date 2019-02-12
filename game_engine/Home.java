package game_engine;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;

/**
 * This class represents the homes where the checkers will bear-off to.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Home extends CheckersStorer {
	private Color checkerColoursToStore;

	public Home(Color color) {
		super();
		checkerColoursToStore = color;
		
		double pointHeight = Settings.getPointSize().getHeight();
		double pointWidth = Settings.getPointSize().getWidth();

		setMinSize(pointWidth, pointHeight);
		setMaxSize(pointWidth, pointHeight);
		setAlignment(Pos.BOTTOM_CENTER);
		setStyle(Settings.getBoardColour());
	}
	
	/**
	 * Returns the color that this home in particular represents.
	 * @return color that this home represents.
	 */
	public Color getColour() {
		return checkerColoursToStore;
	}
}
