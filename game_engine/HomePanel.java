package game_engine;

/**
 * This class represents the panel that contains the homes in Backgammon.
 * There are two HomePanels, one on the left of the board, the other on the right.
 * Each HomePanel has two homes, one top, one bottom.
 * 
 * top is the white's home.
 * bottom is the black's home.
 * 
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

public class HomePanel extends BorderPane {
	private Home top;
	private Home bottom;
	
	public HomePanel() {
		super();
		double halfBoardHeight = Settings.getHalfBoardSize().getHeight();
		double pointWidth = Settings.getPointSize().getWidth();
		
		setMinSize(pointWidth, halfBoardHeight);
		setStyle(Settings.getGameColour());
		initHomes();
	}

	/**
	 * Initializes the individual homes and add them to HomePanel.
	 */
	public void initHomes() {
		top = new Home("white");
		bottom = new Home("black");
		top.setRotate(180);
		
		double margin = Settings.getHomeMargin();
		
		setMargin(top, new Insets(margin));
		setAlignment(top, Pos.CENTER);
		setMargin(bottom, new Insets(margin));
		setAlignment(bottom, Pos.CENTER);
		
		setTop(top);
		setBottom(bottom);
	}

	/**
	 * Returns the home that stores the colour of the checkers.
	 * @param colour of the checkers.
	 * @return the home that stores that particular colour of checkers.
	 */
	public Home getHome(String colour) {
		Home home = null;
		
		if (colour.equals("black")) {
			home = top;
		} else if (colour.equals("white")) {
			home = bottom;
		}
		
		return home;
	}
}
