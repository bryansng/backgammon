package game_engine;

import constants.MoveResult;
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
	private Bars bars;
	private Board board;
	private HomePanel leftHome, rightHome;
	
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
		leftHome = new HomePanel();
		rightHome = new HomePanel();
		bars = new Bars();
		
		HBox middlePart = board;
		middlePart.getChildren().add(1, bars);
		middlePart.getChildren().add(0, leftHome);
		middlePart.getChildren().add(rightHome);

		topUserPnl = new UserPanel(middlePart.getWidth());
		bottomUserPnl = new UserPanel(middlePart.getWidth());
		
		getChildren().addAll(topUserPnl, middlePart, bottomUserPnl);
		setStyle(Settings.getGameColour());
	}
	
	/**
	 * Moves a checker from a point to the bar.
	 * i.e. pops a checker from one point and push it to the bar.
	 * 
	 * @param fro, one-based index, the point number to pop from.
	 * @return returns a integer value indicating if the checker was moved.
	 */
	public MoveResult moveToBar(int fro) {
		MoveResult moveResult = MoveResult.NOT_MOVED;
		
		fro--;
		Point[] points = board.getPoints();
		Bar bar = bars.getBar(points[fro].top().getColour());
		// Checking if its empty is actually done by moveCheckers,
		// since this method is always called after moveCheckers.
		// so this is actually not needed, but is left here just in case.
		if (!points[fro].isEmpty()) {
			bar.push(points[fro].pop());
			moveResult = MoveResult.MOVED_TO_BAR;
		}
		points[fro].drawCheckers();
		bar.drawCheckers();
		
		return moveResult;
	}
	
	public MoveResult moveFromBar(String fromBar, int to) {
		MoveResult moveResult = MoveResult.NOT_MOVED;
		
		to--;
		Point[] points = board.getPoints();
		Bar bar = bars.getBar(fromBar);
		if (!bar.isEmpty()) {
			if (bar.topCheckerColourEquals(points[to])) {
				points[to].push(bar.pop());
				moveResult = MoveResult.MOVED_FROM_BAR;
			} else {
				if (points[to].size() == 1) {
					moveResult = MoveResult.MOVE_TO_BAR;
				}
			}
		}
		points[to].drawCheckers();
		bar.drawCheckers();
		
		return moveResult;
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
	public MoveResult moveCheckers(int fro, int to) {
		return board.moveCheckers(fro, to);
	}
}
