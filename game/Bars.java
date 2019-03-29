package game;

import constants.GameConstants;
import game_engine.Player;
import game_engine.Settings;
import interfaces.ColorPerspectiveParser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * This class represents a VBox of bars, situated in the middle of the board.
 * 
 * top is the black's bar.
 * bottom is the white's bar.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Bars extends VBox implements ColorPerspectiveParser {
	private Bar top;
	private Bar bottom;
	
	public Bars() {
		super();
		double pointWidth = GameConstants.getPipSize().getWidth();
		double pointHeight = GameConstants.getPipSize().getHeight();
		
		setMinSize(pointWidth, pointHeight*2);
		
		setAlignment(Pos.CENTER);
		initIndividualBars();
	}
	
	/**
	 * Initializes the individual bars and add them to bars VBox.
	 */
	public void initIndividualBars() {
		top = new Bar(Settings.getBottomPerspectiveColor());
		bottom = new Bar(Settings.getTopPerspectiveColor());
		bottom.setRotate(180.0);
		
		getChildren().addAll(top, bottom);
		
		setMargin(top, new Insets(GameConstants.getBarMargin(), 0.0, GameConstants.getBarMargin(), 0.0));
		setMargin(bottom, new Insets(GameConstants.getBarMargin(), 0.0, GameConstants.getBarMargin(), 0.0));
	}
	
	/**
	 * Returns the bar that stores the color of the checkers.
	 * @param color of the checkers.
	 * @return the bar that stores that particular color of checkers.
	 */
	public Bar getBar(Color color) {
		Bar bar = null;

		// top player's home is at top, its bear-on location is bottom.
		// bottom player's home is at bottom, its bear-on location is top.
		if (color == Settings.getBottomPerspectiveColor()) {
			bar = top;
		} else if (color == Settings.getTopPerspectiveColor()) {
			bar = bottom;
		}
		
		return bar;
	}
	
	// there are checkers in player's bar if its not empty.
	public boolean isCheckersInBar(Player pCurrent) {
		return !getBar(pCurrent.getColor()).isEmpty();
	}
	
	public void highlight(Color color) {
		Bar bar = getBar(color);
		if (!bar.isEmpty()) bar.getTopChecker().setHighlightImage();
	}
	
	public void unhighlight() {
		if (!top.isEmpty()) top.getTopChecker().setNormalImage();
		if (!bottom.isEmpty()) bottom.getTopChecker().setNormalImage();
	}
	
	public void reset() {
		top.reset();
		bottom.reset();
	}
}
