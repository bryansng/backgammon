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
public class Board{
	private final int MAXPOINTS = 24;
	private Point[] points;
	private VBox board;
	private HBox topPart, bottomPart, jail;
	private BorderPane leftBoard, rightBoard;
	// private BorderPane leftHome, rightHome;
	
	/**
	 * Default Constructor
	 * 		- Initializes the modular panes.
	 * 		- Initializes points with their initial checkers.
	 */
	public Board() {
		points = new Point[MAXPOINTS];
		board = new VBox();
		leftBoard = new BorderPane();
		rightBoard = new BorderPane();
		// homes
		topPart = new HBox();
		bottomPart = new HBox();
		jail = new HBox();
		
		initBoard();
		initPoints();
	}
	
	/**
	 * Initializes the board, by adding the modular panes to the board instance variable.
	 */
	public void initBoard() {
		// the left and right board that make up the entire board.
		double halfBoardWidth = Settings.getHalfBoardSize().getWidth(); 
		double halfBoardHeight = Settings.getHalfBoardSize().getHeight(); 
		leftBoard.setPrefSize(halfBoardWidth, halfBoardHeight);
		leftBoard.setStyle("-fx-background-color: forestgreen;");
		
		rightBoard.setPrefSize(halfBoardWidth, halfBoardHeight);
		rightBoard.setStyle("-fx-background-color: forestgreen;");
		
		/*
		Rectangle lH = new Rectangle(65, 270); 
		Rectangle rH = new Rectangle(65, 270); 
		lH.setFill(Color.FORESTGREEN);
		rH.setFill(Color.FORESTGREEN);
		leftHome.getChildren().add(lH);
		rightHome.getChildren().add(rH);
		*/
		
		// the jail for the checkers.
		jail.setPrefSize(Settings.getPointSize().getWidth(), halfBoardHeight);
		jail.setStyle("-fx-background-color: transparent;");
		
		// where the game is.
		HBox middlePart = new HBox();
		//middle.getChildren().addAll(leftHome, leftBoard, jail, rightBoard, rightHome);
		middlePart.getChildren().addAll(leftBoard, jail, rightBoard);
		
		// Top and bottom, where players' name and score goes.
		topPart.setPrefSize(middlePart.getWidth(), Settings.getTopBottomHeight());
		topPart.setStyle("-fx-background-color: transparent;");
		bottomPart.setPrefSize(middlePart.getWidth(), Settings.getTopBottomHeight());
		bottomPart.setStyle("-fx-background-color: transparent;");
		
		board.getChildren().addAll(topPart, middlePart, bottomPart);
		board.setStyle("-fx-background-color: saddlebrown;");
	}
	
	/**
	 * Returns the board instance variable.
	 * @return the board.
	 */
	public VBox getNode() {
		return board;
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
				points[i] = new Point("BLACK", rotation);
			} else {
				points[i] = new Point("WHITE", rotation);
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
				set.getChildren().add(points[i].getNode());
			}
		else {
			for (int i = startRange-1; i < endRange; i++) {
				set.getChildren().add(points[i].getNode());
			}
		}
		return set;
	}
	
	// pops a checker from one pipe and push it to the other.
	public boolean moveCheckers(int fro, int to) {
		// Adjust indexes to zero-based.
		fro--;
		to--;
		
		boolean moved = false;
		
		if (!points[fro].isEmpty()) {
			points[to].push(points[fro].pop());
			points[to].drawCheckers();
			points[fro].drawCheckers();
			moved = true;
		}
		return moved;
	}
}
