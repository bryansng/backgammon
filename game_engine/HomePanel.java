package game_engine;

import constants.GameConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

/**
 * This class represents the panel that contains the homes in Backgammon.
 * There are two HomePanels, one on the left of the board, the other on the right.
 * Each HomePanel has two homes, one top, one bottom.
 * 
 * top is the white's home.
 * bottom is the black's home.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class HomePanel extends BorderPane {
	private Home top;
	private Home bottom;
	
	public HomePanel() {
		super();
		double halfBoardHeight = GameConstants.getHalfBoardSize().getHeight();
		double pointWidth = GameConstants.getPipSize().getWidth();
		
		setMinSize(pointWidth, halfBoardHeight);
		setStyle("-fx-background-color: transparent");
		initHomes();
	}

	/**
	 * Initializes the individual homes and add them to HomePanel.
	 */
	public void initHomes() {
		top = new Home(Color.WHITE);
		bottom = new Home(Color.BLACK);
		top.setRotate(180);
		
		double margin = GameConstants.getHomeMargin();
		
		setMargin(top, new Insets(margin));
		setAlignment(top, Pos.CENTER);
		setMargin(bottom, new Insets(margin));
		setAlignment(bottom, Pos.CENTER);
		
		setTop(top);
		setBottom(bottom);
	}

	/**
	 * Returns the home that stores the color of the checkers.
	 * @param colour of the checkers.
	 * @return the home that stores that particular color of checkers.
	 */
	public Home getHome(Color color) {
		Home home = null;
		
		if (color == Color.BLACK) {
			home = top;
		} else if (color == Color.WHITE) {
			home = bottom;
		}
		
		return home;
	}
	
	/**
	 * Returns a boolean value indicating either homes are filled with checkers.
	 * @return the boolean value.
	 */
	public boolean isFilled() {
		return top.isFilled() || bottom.isFilled();
	}
}
