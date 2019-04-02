package game;

import constants.GameConstants;
import game_engine.Settings;
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
 * @author Braddy Yeoh, 17357376
 *
 */
public class HomePanel extends BorderPane {
	private boolean isMainHome;
	private Home top;
	private Home bottom;
	private DoublingCubeHome cubeHome;
	
	public HomePanel() {
		super();
		// set as main home by default,
		// so doubling cube doesn't occur at main home.
		isMainHome = true;
		
		double halfBoardHeight = GameConstants.getHalfBoardSize().getHeight();
		double pointWidth = GameConstants.getPipSize().getWidth();
		setMinSize(pointWidth, halfBoardHeight);
		setStyle("-fx-background-color: transparent");
		initHomes();
		drawHomes();
	}
	
	/**
	 * Initializes the individual homes and add them to HomePanel.
	 */
	private void initHomes() {
		top = new Home(Settings.getTopPerspectiveColor());
		bottom = new Home(Settings.getBottomPerspectiveColor());
	}
	
	private void initCheckersInHomes() {
		if (isMainHome) {
			if (GameConstants.FORCE_13_CHECKERS_AT_HOME) {
				top.initCheckers(13, top.getColor());
				bottom.initCheckers(13, bottom.getColor());
			} else if (GameConstants.FORCE_TEST_SINGLE) {
				top.initCheckers(11, top.getColor());
				bottom.initCheckers(9, bottom.getColor());
			} else if (GameConstants.FORCE_TEST_GAMMON) {
				bottom.initCheckers(9, bottom.getColor());
			} else if (GameConstants.FORCE_TEST_BACKGAMMON) {
				bottom.initCheckers(9, bottom.getColor());
			} else {
				top.removeCheckers();
				bottom.removeCheckers();
			}
		} else {
			top.removeCheckers();
			bottom.removeCheckers();
		}
	}
	
	private void drawHomes() {
		top.setRotate(180);
		double margin = GameConstants.getHomeMargin();
		
		setMargin(top, new Insets(margin));
		setAlignment(top, Pos.CENTER);
		setMargin(bottom, new Insets(margin));
		setAlignment(bottom, Pos.CENTER);
		
		setTop(top);
		setBottom(bottom);
	}
	
	private void initDoubleCube() {
		// if not main home, we add a box to store the doubling cube.
		if (!isMainHome) {
			cubeHome = new DoublingCubeHome();
			drawDoubleCubeHome();
		}
	}
	
	private void drawDoubleCubeHome() {
		double margin = GameConstants.getHomeMargin();
		setMargin(cubeHome, new Insets(margin));
		setAlignment(cubeHome, Pos.CENTER);
		
		setCenter(cubeHome);
	}
	
	public DoublingCubeHome getCubeHome() {
		return cubeHome;
	}

	/**
	 * Returns the home that stores the color of the checkers.
	 * @param colour of the checkers.
	 * @return the home that stores that particular color of checkers.
	 */
	public Home getHome(Color color) {
		Home home = null;
		
		if (color == Settings.getTopPerspectiveColor()) {
			home = top;
		} else if (color == Settings.getBottomPerspectiveColor()) {
			home = bottom;
		}
		
		return home;
	}
	
	/**
	 * Returns either home filled with checkers.
	 * If no homes are filled, returns null.
	 * @return filled home.
	 */	
	public Home getFilledHome() {
		Home home = null;
		
		if (top.isFilled())
			home = top;
		else if (bottom.isFilled())
			home = bottom;
		
		return home;
	}
	
	public void highlight(Color color) {
		getHome(color).highlight();
	}
	
	public void highlight() {
		top.highlight();
		bottom.highlight();
		if (!isMainHome) cubeHome.highlight();
	}
	
	public void unhighlight() {
		top.unhighlight();
		bottom.unhighlight();
		if (!isMainHome) cubeHome.unhighlight();
	}
	
	public void setAsMainHome(boolean isMainHome) {
		this.isMainHome = isMainHome;
		initCheckersInHomes();
		initDoubleCube();
	}
	
	public boolean isMainHome() {
		return isMainHome;
	}
	
	public void reset() {
		initCheckersInHomes();
		if (!isMainHome) {
			cubeHome.reset();
		}
	}
}
