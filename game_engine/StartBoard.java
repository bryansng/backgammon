package game_engine;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class StartBoard extends HBox {
	protected final int MAXPOINTS = Settings.NUMBER_OF_POINTS;
	protected Point[] points;
	protected BorderPane leftBoard, rightBoard;

	protected HBox leftDice, rightDice;
	protected Dices dices;

	/**
	 * Default Constructor
	 * 		- Initializes board.
	 * 		- Initializes points with their initial checkers.
	 * 		- Initializes dices.
	 */
	public StartBoard() {
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
				points[i] = new Point(Color.BLACK, rotation, i);
			} else {
				points[i] = new Point(Color.WHITE, rotation, i);
			}
			
			switch (i) {
				case 0:
					points[i].initCheckers(2, Color.BLACK);
					break;
				case 5:
					points[i].initCheckers(5, Color.WHITE);
					break;
				case 7:
					points[i].initCheckers(3, Color.WHITE);
					break;
				case 11:
					points[i].initCheckers(5, Color.BLACK);
					break;
				case 12:
					points[i].initCheckers(5, Color.WHITE);
					break;
				case 16:
					points[i].initCheckers(3, Color.BLACK);
					break;
				case 18:
					points[i].initCheckers(5, Color.BLACK);
					break;
				case 23:
					points[i].initCheckers(2, Color.WHITE);
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
		VBox quad4 = createQuadrants(1, 6, "bottom");
		VBox quad3 = createQuadrants(7, 12, "bottom");
		VBox quad2 = createQuadrants(13, 18, "top");
		VBox quad1 = createQuadrants(19, 24, "top");

		rightBoard.setBottom(quad4);
		leftBoard.setBottom(quad3);
		leftBoard.setTop(quad2);
		rightBoard.setTop(quad1);
	}

	/**
	 * Creates a VBox with the points and their labels.
	 * @param startRange One-based index, starting index.
	 * @param endRange One-based index, ending index.
	 * @param side The side where the points will be situated, top or bottom of board.
	 * @return VBox with HBox of points and HBox of labels as children.
	 */
	private VBox createQuadrants(int startRange, int endRange, String side) {
		VBox quad = new VBox();
		HBox setLabels = createSetOfPoints(startRange, endRange, side);
		HBox setPoints = createSetOfLabels(startRange, endRange, side);

		if (side.equals("bottom"))
			quad.getChildren().addAll(setLabels, setPoints);
		else {
			quad.getChildren().addAll(setPoints, setLabels);
		}
		return quad;
	}

	/**
	 * Creates a HBox with the labels within the range of startRange and endRange.
	 * @param startRange One-based index, starting index.
	 * @param endRange One-based index, ending index.
	 * @param side The side where the points will be situated, top or bottom of board.
	 * @return HBox with the labels as children.
	 */
	private HBox createSetOfLabels(int startRange, int endRange, String side) {
		HBox set = new HBox();
		set.setPrefSize(rightBoard.getPrefWidth(), Settings.getPointNumberLabelHeight());
		
		// Handles the evenly distributed spacings between the points.
		set.setAlignment(Pos.CENTER);
		// why by 5 again?
		double spacing = (rightBoard.getPrefWidth()-6*(Settings.getPointSize().getWidth())) / 5;
		set.setSpacing(spacing);
		
		set.setStyle(Settings.getGameColour());
		
		// If bottom of board, points are numbered from smallest to highest from right to left.
		// Else, from smallest to highest from left to right.
		if (side.equals("bottom"))
			for (int i = endRange-1; i >= startRange-1; i--) {
				set.getChildren().add(new PointNumberLabel(i+1));
			}
		else {
			for (int i = startRange-1; i < endRange; i++) {
				set.getChildren().add(new PointNumberLabel(i+1));
			}
		}
		return set;
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
		dices = new Dices(Color.RED);
	}
}
