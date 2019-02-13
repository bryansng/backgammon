package game_engine;

import constants.MoveResult;
import constants.PlayerPerspectiveFrom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * This class represents the game in Backgammon.
 * This class creates a game made out of modular panes/nodes.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class GameController extends VBox {
	private PlayerPanel topUserPnl, bottomUserPnl;
	private Bars bars;
	private Board board;
	private HomePanel leftHome, rightHome, mainHome;
	
	/**
	 * Default Constructor
	 * 		- Initializes the modular panes.
	 * 		- Initializes points with their initial checkers.
	 */
	public GameController(Player bottomPlayer, Player topPlayer) {
		super();
		initGameComponents(bottomPlayer, topPlayer);
	}
	
	/**
	 * Initializes the board, by adding the modular panes to the board instance variable.
	 * i.e. initializing the board layout.
	 */
	public void initGameComponents(Player bottomPlayer, Player topPlayer) {
		board = new Board();
		leftHome = new HomePanel();
		rightHome = new HomePanel();
		mainHome = rightHome;
		bars = new Bars();
		
		HBox middlePart = board;
		middlePart.setMinWidth(Settings.getMiddlePartWidth());
		middlePart.getChildren().add(1, bars);
		middlePart.getChildren().add(0, leftHome);
		middlePart.getChildren().add(rightHome);
		
		topUserPnl = new PlayerPanel(middlePart.getMinWidth(), topPlayer);
		bottomUserPnl = new PlayerPanel(middlePart.getMinWidth(), bottomPlayer);
		
		getChildren().addAll(topUserPnl, middlePart, bottomUserPnl);
		setStyle(Settings.getGameColour());
		setMaxHeight(topUserPnl.getMinHeight() + middlePart.getHeight() + bottomUserPnl.getMinHeight());
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
		Bar bar = bars.getBar(points[fro].top().getColor());
		// Checking if its empty is actually done by moveCheckers,
		// since this method is always called after moveCheckers.
		// so this is actually not needed, but is left here just in case.
		if (!points[fro].isEmpty()) {
			bar.push(points[fro].pop());
			moveResult = MoveResult.MOVED_TO_BAR;
			
			points[fro].drawCheckers();
			bar.drawCheckers();
		}
		return moveResult;
	}
	
	/**
	 * Moves a checker from bar to a point.
	 * i.e. pops a checker from bar and push it to a point.
	 * 
	 * @param fromBar, color of the bar to pop from.
	 * @param to, one-based index, the point number to push to.
	 * @return returns a integer value indicating if the checker was moved.
	 */
	public MoveResult moveFromBar(Color fromBar, int to) {
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
			
			points[to].drawCheckers();
			bar.drawCheckers();
		}
		return moveResult;
	}
	
	/**
	 * Moves a checker from a point to its home.
	 * i.e. pops a checker from bar and push it to a point.
	 * 
	 * @param fro, one-based index, the point number to pop from.
	 * @return returns a integer value indicating if the checker was moved.
	 */
	public MoveResult moveToHome(int fro) {
		MoveResult moveResult = MoveResult.NOT_MOVED;
		
		fro--;
		Point[] points = board.getPoints();
		if (!points[fro].isEmpty()) {
			Home home = mainHome.getHome(points[fro].top().getColor());
			home.push(points[fro].pop());
			moveResult = MoveResult.MOVED_TO_HOME_FROM_PIP;

			points[fro].drawCheckers();
			home.drawCheckers();
		}
		return moveResult;
	}
	
	/**
	 * Moves a checker from bar to its home.
	 * i.e. pops a checker from bar and push it to a point.
	 * 
	 * @param fromBar, color of the bar to pop from.
	 * @return returns a integer value indicating if the checker was moved.
	 */
	public MoveResult moveToHome(Color fromBar) {
		MoveResult moveResult = MoveResult.NOT_MOVED;
		
		Bar bar = bars.getBar(fromBar);
		if (!bar.isEmpty()) {
			Home home = mainHome.getHome(bar.top().getColor());
			home.push(bar.pop());
			moveResult = MoveResult.MOVED_TO_HOME_FROM_BAR;

			bar.drawCheckers();
			home.drawCheckers();
		}
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
	public int[] rollDices(PlayerPerspectiveFrom pov) {
		return board.rollDices(pov);
	}
	public MoveResult moveCheckers(int fro, int to) {
		return board.moveCheckers(fro, to);
	}
}
