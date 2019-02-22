package game_engine;

import constants.GameConstants;
import constants.PlayerPerspectiveFrom;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * This class represents the Board object in Backgammon from the perspective of its components.
 * This class initializes an array of pips with their starting checkers.
 * This class creates a board made out of more components (quadrants of pip number labels and pips).
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class BoardComponents extends HBox {
	protected final int MAXPIPS = GameConstants.NUMBER_OF_PIPS;
	protected Pip[] pips;
	protected BorderPane leftBoard, rightBoard;
	protected BoardQuadrant quad1, quad2, quad3, quad4;
	protected HBox leftDice, rightDice;
	protected Dices dices;
	
	/**
	 * Default Constructor
	 * 		- Initializes board.
	 * 		- Initializes pips with their initial checkers.
	 * 		- Initializes dices.
	 */
	public BoardComponents() {
		super();
		leftBoard = new HalfBoard();
		rightBoard = new HalfBoard();
		getChildren().addAll(leftBoard, rightBoard);
		initDices();
		
		if (GameConstants.TEST_ACTUAL_GAME) {
			initPips();
		} else if (!GameConstants.DEBUG_MODE) {
			initPips();
		} else {
			initDebugPips();
		}
	}
	
	/**
	 * Initializes the pips, by setting the starting position of the checkers at their pips.
	 * Setting includes:
	 * 		- Handling the rotation of the pips (pointing downwards or upwards).
	 * 		- Handling the Color of the pips (black or white).
	 * 		- Drawing the pips (adding them to the leftBoard and rightBoard).
	 */
	private void initPips() {
		/* Pip colours.
		 * Odd - White.
		 * Even - Black.
		 *
		 * Pip starting positions.
		 * Format:
		 * Pips - NumberOfCheckers CheckersColor
		 * 1 - 2 B
		 * 6 - 5 W
		 * 8 - 3 W
		 * 12 - 5 B
		 * 13 - 5 W
		 * 17 - 3 B
		 * 19 - 5 B
		 * 24 - 2 W
		 */
		pips = new Pip[MAXPIPS];
		for (int i = 0; i < MAXPIPS; i++) {
			// Handles rotation of point.
			double rotation = 0;
			if (i >= 0 && i <= 11) {
				rotation = 0;
			} else if (i >= 12 && i <= 23) {
				rotation = 180;
			}
			
			// Handles point color.
			if ((i+1) % 2 != 0) {
				pips[i] = new Pip(Color.BLACK, rotation, i);
			} else {
				pips[i] = new Pip(Color.WHITE, rotation, i);
			}
			
			switch (i) {
				case 0:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				case 5:
					pips[i].initCheckers(5, Color.WHITE);
					break;
				case 7:
					pips[i].initCheckers(3, Color.WHITE);
					break;
				case 11:
					pips[i].initCheckers(5, Color.BLACK);
					break;
				case 12:
					pips[i].initCheckers(5, Color.WHITE);
					break;
				case 16:
					pips[i].initCheckers(3, Color.BLACK);
					break;
				case 18:
					pips[i].initCheckers(5, Color.BLACK);
					break;
				case 23:
					pips[i].initCheckers(2, Color.WHITE);
					break;
			}
		}
		drawPips();
	}
	
	/**
	 * Initializes the debug pips, used for testing.
	 */
	private void initDebugPips() {
		pips = new Pip[MAXPIPS];
		for (int i = 0; i < MAXPIPS; i++) {
			// Handles rotation of point.
			double rotation = 0;
			if (i >= 0 && i <= 11) {
				rotation = 0;
			} else if (i >= 12 && i <= 23) {
				rotation = 180;
			}
			
			// Handles point color.
			if ((i+1) % 2 != 0) {
				pips[i] = new Pip(Color.BLACK, rotation, i);
			} else {
				pips[i] = new Pip(Color.WHITE, rotation, i);
			}
			
			switch (i) {
				case 12:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				case 11:
					pips[i].initCheckers(1, Color.WHITE);
					break;
			}
		}
		drawPips();
	}
	
	/**
	 * Drawing the pips by adding them to the leftBoard and rightBoard.
	 */
	private void drawPips() {
		/*
		 * 1-6, black home board.
		 * 7-12, black outer board.
		 * 13-18, white outer board.
		 * 19-24, white home board.
		 */
		// Each quadrant has a set of 6 pips.
		quad4 = new BoardQuadrant(1, 6, PlayerPerspectiveFrom.BOTTOM, pips);
		quad3 = new BoardQuadrant(7, 12, PlayerPerspectiveFrom.BOTTOM, pips);
		quad2 = new BoardQuadrant(13, 18, PlayerPerspectiveFrom.TOP, pips);
		quad1 = new BoardQuadrant(19, 24, PlayerPerspectiveFrom.TOP, pips);

		rightBoard.setBottom(quad4);
		leftBoard.setBottom(quad3);
		leftBoard.setTop(quad2);
		rightBoard.setTop(quad1);
	}

	/**
	 * Swap top and bottom pip labels. 
	 * quad1's = quad4's
	 * quad2's = quad3's
	 */
	public void swapPipLabels() {
		HBox temp;
		
		temp = quad1.getLabels();
		quad1.drawQuadrant(quad4.getLabels());
		quad4.drawQuadrant(temp);
		
		temp = quad2.getLabels();
		quad2.drawQuadrant(quad3.getLabels());
		quad3.drawQuadrant(temp);
	}
	
	/**
	 * Returns the pips instance variable (array of pips).
	 * @return the pips instance variable.
	 */
	public Pip[] getPips() {
		return pips;
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
