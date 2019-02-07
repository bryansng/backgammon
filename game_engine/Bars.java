package game_engine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

/**
 * This class represents a VBox of bars, situated in the middle of the board.
 * 
 * top is the black's bar.
 * bottom is the white's bar.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Bars extends VBox {
	private Bar top;
	private Bar bottom;
	
	public Bars() {
		super();
		double pointWidth = Settings.getPointSize().getWidth();
		double pointHeight = Settings.getPointSize().getHeight();
		
		setMinSize(pointWidth, pointHeight*2);
		setStyle(Settings.getGameColour());
		
		setAlignment(Pos.CENTER);
		initIndividualBars();
	}
	
	/**
	 * Initializes the individual bars and add them to bars VBox.
	 */
	public void initIndividualBars() {
		top = new Bar("black");
		bottom = new Bar("white");
		bottom.setRotate(180.0);
		
		getChildren().addAll(top, bottom);
		
		setMargin(top, new Insets(Settings.getBarMargin(), 0.0, Settings.getBarMargin(), 0.0));
		setMargin(bottom, new Insets(Settings.getBarMargin(), 0.0, Settings.getBarMargin(), 0.0));
	}
	
	/**
	 * Returns the bar that stores the colour of the checkers.
	 * @param colour of the checkers.
	 * @return the bar that stores that particular colour of checkers.
	 */
	public Bar getBar(String colour) {
		Bar bar = null;
		
		if (colour.equals("black")) {
			bar = top;
		} else if (colour.equals("white")) {
			bar = bottom;
		}
		
		return bar;
	}
}
