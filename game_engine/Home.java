package game_engine;

import javafx.geometry.Pos;

/**
 * This class represents the homes where the checkers will bear-off to.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Home extends CheckersStorer {
	private String checkerColoursToStore;

	public Home(String colour) {
		super();
		checkerColoursToStore = colour;
		
		double pointHeight = Settings.getPointSize().getHeight();
		double pointWidth = Settings.getPointSize().getWidth();

		setMinSize(pointWidth, pointHeight);
		setMaxSize(pointWidth, pointHeight);
		setAlignment(Pos.BOTTOM_CENTER);
		setStyle(Settings.getBoardColour());
	}
	
	/**
	 * Returns the colour that this home in particular represents.
	 * @return colour that this home represents.
	 */
	public String getColour() {
		return checkerColoursToStore;
	}
}
