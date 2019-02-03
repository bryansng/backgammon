package game_engine;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This class represents the game in Backgammon.
 * This class creates a game made out of modular panes/nodes.
 * 
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
public class GameController extends VBox {
	private UserPanel topUserPnl, bottomUserPnl;
	private Jail jail;
	private Board board;
	private Home leftHome, rightHome;
	
	/**
	 * Default Constructor
	 * 		- Initializes the modular panes.
	 * 		- Initializes points with their initial checkers.
	 */
	public GameController() {
		super();
		initGameComponents();
	}
	
	/**
	 * Initializes the board, by adding the modular panes to the board instance variable.
	 * i.e. initializing the board layout.
	 */
	public void initGameComponents() {
		board = new Board();
		leftHome = new Home();
		rightHome = new Home();
		jail = new Jail();
		
		HBox middlePart = board;
		middlePart.getChildren().add(1, jail);
		middlePart.getChildren().add(0, leftHome);
		middlePart.getChildren().add(rightHome);

		topUserPnl = new UserPanel(middlePart.getWidth());
		bottomUserPnl = new UserPanel(middlePart.getWidth());
		
		getChildren().addAll(topUserPnl, middlePart, bottomUserPnl);
		setStyle("-fx-background-color: saddlebrown;");
	}
	
	/**
	 * Buffer methods for Board instance variable - Keep functionality separate.
	 */
	public Point[] getPoints() {
		return board.getPoints();
	}
	public void unhighlightPoints() {
		board.unhighlightPoints();
	}
	public void highlightPoints(int exceptPointNum) {
		board.highlightPoints(exceptPointNum);
	}
	public int[] rollDices(int playerNum) {
		return board.rollDices(playerNum);
	}
	public boolean moveCheckers(int fro, int to) {
		return board.moveCheckers(fro, to);
	}
}
