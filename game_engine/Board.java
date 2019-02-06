package game_engine;

import constants.MoveResult;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * This class represents the Board object in Backgammon.
 * This class initializes an array of points with their starting checkers.
 * This class creates a board made out of modular panes.
 * 
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
public class Board extends HBox {
	private final int MAXPOINTS = 24;
	private Point[] points;
	private BorderPane leftBoard, rightBoard;

	private HBox leftDice, rightDice;
	private Dices dices;

	/**
	 * Default Constructor
	 * 		- Initializes board.
	 * 		- Initializes points with their initial checkers.
	 * 		- Initializes dices.
	 */
	public Board() {
		super();
		leftBoard = new HalfBoard();
		rightBoard = new HalfBoard();
		getChildren().addAll(leftBoard, rightBoard);
		initPoints();
		initDices();
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
		points = new Point[MAXPOINTS];
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
					points[i].initCheckers(2, "black");
					break;
				case 5:
					points[i].initCheckers(5, "white");
					break;
				case 7:
					points[i].initCheckers(3, "white");
					break;
				case 11:
					points[i].initCheckers(5, "black");
					break;
				case 12:
					points[i].initCheckers(5, "white");
					break;
				case 16:
					points[i].initCheckers(3, "black");
					break;
				case 18:
					points[i].initCheckers(5, "black");
					break;
				case 23:
					points[i].initCheckers(2, "white");
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
		HBox quad4 = createSetOfPoints(1, 6, "bottom");
		HBox quad3 = createSetOfPoints(7, 12, "bottom");
		HBox quad2 = createSetOfPoints(13, 18, "top");
		HBox quad1 = createSetOfPoints(19, 24, "top");

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
		if (side.equals("bottom"))
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
	 * Moves a checker between points.
	 * i.e. pops a checker from one point and push it to the other.
	 * 
	 * @param fro, one-based index, the point number to pop from.
	 * @param to, one-based index, the point number to push to.
	 * @return returns a integer value indicating if the checker was moved.
	 */
	public MoveResult moveCheckers(int fro, int to) {
		// Adjust indexes to zero-based indexing.
		fro--;
		to--;
		MoveResult moveResult = MoveResult.NOT_MOVED;
		
		if (!points[fro].isEmpty()) {
			if (points[fro].topCheckerColourEquals(points[to])) {
				points[to].push(points[fro].pop());
				moveResult = MoveResult.MOVED;
			} else {
				if (points[to].size() == 1) {
					moveResult = MoveResult.MOVE_TO_BAR;
				}
			}
		}
		points[to].drawCheckers();
		points[fro].drawCheckers();
		
		// boolean value is returned instead of void,
		// is because this provides flexibility in the future
		// where we can print to the info panel telling the user
		// that their move was invalid, and that they should
		// try another move.
		return moveResult;
	}
	
	/**
	 * Un-highlight the points.
	 * // TODO merge un-highlight and highlight together, give the ability to specify which points.
	 */
	public void unhighlightPoints() {
		for (int i = 0; i < points.length; i++) {
			points[i].setNormalImage(); 
		}
	}
	
	/**
	 * Highlight the points.
	 * @param exceptPointNum, except this point number.
	 */
	public void highlightPoints(int exceptPointNum) {
		for (int i = 0; i < points.length; i++) {
			if (i == exceptPointNum) {
				points[i].setNormalImage();
			} else {
				points[i].setHighlightImage();
			}
		}
	}
	
	/**
	 * Returns the points instance variable (array of points).
	 * @return the points instance variable.
	 */
	public Point[] getPoints() {
		return points;
	}
	
	/**
	 * 
	 */
	private void initDices() {
		/** IGNORE THIS ATM, currently considering to use red for all,
		 * then when its the player's turn then change the dice to that
		 * player's side than to create new HBox of dices.
		 * 
		 * left uses red.
		 * right uses black.
		 */
		dices = new Dices("RED");
	}
	
	/**
	 * Execute the roll dice methods of dices object.
	 * 
	 * Based on the playerNum, draw the dices at the player's
	 * respective side of the board.
	 * 
	 * playerNum is currently zero-based indexing.
	 * 1 is the player with the perspective from the bottom, dices will be on the left.
	 * 2 is the player with the perspective from the top, dices will be on the right.
	 * 
	 * @param playerNum - integer that represents which player it is. (i.e. player 1 or 2).
	 * @return result of each dice roll in terms of an array of integers.
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
}
