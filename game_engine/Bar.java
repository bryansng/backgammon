package game_engine;

import javafx.geometry.Pos;

/**
 * This class represents the bar game component in Backgammon.
 * 
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
public class Bar extends CheckersStorer {
	private String checkerColoursToStore;
	
	public Bar(String colour) {
		super();
		checkerColoursToStore = colour;
		
		double pointWidth = Settings.getPointSize().getWidth();
		double pointHeight = Settings.getPointSize().getHeight();
		
		setMinSize(pointWidth, pointHeight);
		setMaxSize(pointWidth, pointHeight);
		setAlignment(Pos.BOTTOM_CENTER);
	}
	
	/**
	 * Returns the colour that this bar in particular represents.
	 * @return colour that this bar represents.
	 */
	public String getColour() {
		return checkerColoursToStore;
	}
}
