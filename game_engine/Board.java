package game_engine;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This class represents the Board object in Backgammon.
 * This class initializes an array of points with their starting checkers.
 * This class creates a board made out of modular panes.
 * 
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
public class Board extends VBox {
	private final int MAXPOINTS = 24;
	private Point[] points;
	private HBox topPart, bottomPart, jail;
	private BorderPane leftBoard, rightBoard;
	private HBox leftDice, rightDice;
	private Dices dices;
	private VBox leftHome, rightHome;
	
	/**
	 * Default Constructor
	 * 		- Initializes the modular panes.
	 * 		- Initializes points with their initial checkers.
	 */
	public Board() {
		super();
		points = new Point[MAXPOINTS];
		leftBoard = new BorderPane();
		rightBoard = new BorderPane();
		leftHome = new VBox();
		rightHome = new VBox();
		topPart = new HBox();
		bottomPart = new HBox();
		jail = new HBox();
		
		initBoard();
		initPoints();
		drawDices();
	}
	
	/**
	 * Initializes the board, by adding the modular panes to the board instance variable.
	 * i.e. initializing the board layout.
	 */
	public void initBoard() {
		double halfBoardWidth = Settings.getHalfBoardSize().getWidth(); 
		double halfBoardHeight = Settings.getHalfBoardSize().getHeight();
		double pointWidth = Settings.getPointSize().getWidth();
		
		// the left and right board that make up the entire board.
		leftBoard.setPrefSize(halfBoardWidth, halfBoardHeight);
		rightBoard.setPrefSize(halfBoardWidth, halfBoardHeight);
		leftBoard.setStyle("-fx-background-color: forestgreen;");
		rightBoard.setStyle("-fx-background-color: forestgreen;");
		
		// left and right homes.
		leftHome.setPrefSize(pointWidth, halfBoardHeight);
		rightHome.setPrefSize(pointWidth, halfBoardHeight);
		
		// the jail for the checkers.
		jail.setPrefSize(pointWidth, halfBoardHeight);
		jail.setStyle("-fx-background-color: transparent;");
		
		// where the game is.
		HBox middlePart = new HBox();
		middlePart.getChildren().addAll(leftHome, leftBoard, jail, rightBoard, rightHome);
		//middlePart.getChildren().addAll(leftBoard, jail, rightBoard);
		
		// Top and bottom, where players' name and score goes.
		topPart.setPrefSize(middlePart.getWidth(), Settings.getTopBottomHeight());
		topPart.setStyle("-fx-background-color: transparent;");
		bottomPart.setPrefSize(middlePart.getWidth(), Settings.getTopBottomHeight());
		bottomPart.setStyle("-fx-background-color: transparent;");
		
		getChildren().addAll(topPart, middlePart, bottomPart);
		setStyle("-fx-background-color: saddlebrown;");
	}
	
	/**
	 * 
	 */
	private void drawDices() {
		/** IGNORE THIS ATM, currently considering to use red for all,
		 * then when its the player's turn then change the dice to that
		 * player's side than to create new HBox of dices.
		 * 
		 * left uses red.
		 * right uses black.
		 */
		dices = new Dices("RED");
		//rightDice = new Dices("BLACK");
	}
	
	/**
	 * 
	 * 
	 * playerNum is currently zero-based indexing.
	 * 1 is the player with the perspective from the bottom, dices will be on the left.
	 * 2 is the player with the perspective from the top, dices will be on the right.
	 * 
	 * @param playerNum - integer that represents which player it is. (i.e. player 1 or 2).
	 * @return
	 */
	public int[] rollDices(int playerNum) {
		int[] res = null;
		
		switch (playerNum) {
			case 1:
				leftDice = dices;
				rightDice = null;
				res = dices.getTotalRoll();
				break;
			case 2:
				leftDice = null;
				rightDice = dices;
				res = dices.getTotalRoll();
				break;
			default:
				leftDice = null;
				rightDice = null;
		}
		leftBoard.setCenter(leftDice);
		rightBoard.setCenter(rightDice);
		
		return res;
	}
	
	/**
	 * Initializes the points, by setting the starting position of the checkers at their points.
	 * Setting includes:
	 * 		- Handling the rotation of the points (pointing downwards or upwards).
	 * 		- Handling the Color of the points (black or white).
	 * 		- Drawing the points (adding them to the leftBoard and rightBoard).
	 */
	private void initPoints() {
		/* Point colours.
		 * Odd - White.
		 * Even - Black.
		 *
		 * Point starting positions.
		 * Format:
		 * Points - NumberOfCheckers CheckersColor
		 * 1 - 2 B
		 * 6 - 5 W
		 * 8 - 3 W
		 * 12 - 5 B
		 * 13 - 5 W
		 * 17 - 3 B
		 * 19 - 5 B
		 * 24 - 2 W
		 */
		for (int i = 0; i < MAXPOINTS; i++) {
			// Handles rotation of point.
			double rotation = 0;
			if (i >= 0 && i <= 11) {
				rotation = 0;
			} else if (i >= 12 && i <= 23) {
				rotation = 180;
			}
			
			// Handles point color.
			if ((i+1) % 2 != 0) {
				points[i] = new Point("BLACK", rotation, i);
			} else {
				points[i] = new Point("WHITE", rotation, i);
			}
			
			switch (i) {
				case 0:
					points[i].initCheckers(2, "BLACK");
					break;
				case 5:
					points[i].initCheckers(5, "WHITE");
					break;
				case 7:
					points[i].initCheckers(3, "WHITE");
					break;
				case 11:
					points[i].initCheckers(5, "BLACK");
					break;
				case 12:
					points[i].initCheckers(5, "WHITE");
					break;
				case 16:
					points[i].initCheckers(3, "BLACK");
					break;
				case 18:
					points[i].initCheckers(5, "BLACK");
					break;
				case 23:
					points[i].initCheckers(2, "WHITE");
					break;
			}
		}
		drawPoints();
	}
	
	/**
	 * Drawing the points by adding them to the leftBoard and rightBoard.
	 */
	private void drawPoints() {
		/*
		 * 1-6, black home board.
		 * 7-12, black outer board.
		 * 13-18, white outer board.
		 * 19-24, white home board.
		 */
		// Each quadrant has a set of 6 points.
		HBox quad4 = createSetOfPoints(1, 6, "BOTTOM");
		HBox quad3 = createSetOfPoints(7, 12, "BOTTOM");
		HBox quad2 = createSetOfPoints(13, 18, "TOP");
		HBox quad1 = createSetOfPoints(19, 24, "TOP");

		rightBoard.setBottom(quad4);
		leftBoard.setBottom(quad3);
		leftBoard.setTop(quad2);
		rightBoard.setTop(quad1);
	}
	
	/**
	 * Creates a HBox with the points within the range of startRange and endRange.
	 * @param startRange One-based index, starting index.
	 * @param endRange One-based index, ending index.
	 * @param side The side where the points will be situated, top or bottom of board.
	 * @return HBox with the points as children.
	 */
	private HBox createSetOfPoints(int startRange, int endRange, String side) {
		HBox set = new HBox();
		set.setPrefSize(rightBoard.getPrefWidth(), Settings.getPointSize().getHeight());
		
		// Handles the evenly distributed spacings between the points.
		set.setAlignment(Pos.CENTER);
		// why by 5 again?
		double spacing = (rightBoard.getPrefWidth()-6*(Settings.getPointSize().getWidth())) / 5;
		set.setSpacing(spacing);
		
		// If bottom of board, points are numbered from smallest to highest from right to left.
		// Else, from smallest to highest from left to right.
		if (side.equals("BOTTOM"))
			for (int i = endRange-1; i >= startRange-1; i--) {
				set.getChildren().add(points[i]);
			}
		else {
			for (int i = startRange-1; i < endRange; i++) {
				set.getChildren().add(points[i]);
			}
		}
		return set;
	}
	
	/**
	 * Returns the points instance variable (array of points).
	 * @return the points instance variable.
	 */
	public Point[] getPoints() {
		return points;
	}
	
	/**
	 * Moves a checker between points.
	 * i.e. pops a checker from one point and push it to the other.
	 * 
	 * @param fro, one-based index, the point number to pop from.
	 * @param to, one-based index, the point number to push to.
	 * @return
	 */
	public boolean moveCheckers(int fro, int to) {
		// Adjust indexes to zero-based indexing.
		fro--;
		to--;
		boolean moved = false;
		
		// if point has no checkers, no point to pop or push. 
		if (!points[fro].isEmpty()) {
			points[to].push(points[fro].pop());
			points[to].drawCheckers();
			points[fro].drawCheckers();
			moved = true;
		}
		
		// boolean value is returned instead of void,
		// is because this provides flexibility in the future
		// where we can print to the info panel telling the user
		// that their move was invalid, and that they should
		// try another move.
		return moved;
	}
}
