package game;

import java.util.LinkedList;
import constants.GameConstants;
import constants.PlayerPerspectiveFrom;
import constants.Quadrant;
import game_engine.Player;
import game_engine.Settings;
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
 * @author Braddy Yeoh, 17357376
 *
 */
public class BoardComponents extends HBox {
	protected final int MAXPIPS = GameConstants.NUMBER_OF_PIPS;
	protected Pip[] pips;
	protected HalfBoard leftBoard, rightBoard;
	protected DoublingCubeHome leftCubeHome, rightCubeHome;
	protected BoardQuadrant quad1, quad2, quad3, quad4, whiteQuad, blackQuad;
	protected LinkedList<BoardQuadrant> quads;
	protected Dices leftDices, rightDices, dices;
	private boolean isLabelsFlipped;
	
	/**
	 * Default Constructor
	 * 		- Initializes board.
	 * 		- Initializes pips with their initial checkers.
	 * 		- Initializes dices.
	 */
	public BoardComponents() {
		super();
		isLabelsFlipped = false;
		quads = new LinkedList<>();
		leftBoard = new HalfBoard();
		rightBoard = new HalfBoard();
		getChildren().addAll(leftBoard, rightBoard);
		initCubeHomes();
		initDices();
		initPips();
	}
	
	private void initCubeHomes() {
		if (Settings.getWhiteHomeQuadrant() == Quadrant.BOTTOM_RIGHT) {
			rightCubeHome = new DoublingCubeHome(false, Color.WHITE);
			leftCubeHome = new DoublingCubeHome(false, Color.BLACK);
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
			
			initCheckers(i);
		}
		drawPips();
	}

	private void initCheckers() {
		for (int i = 0; i < MAXPIPS; i++) {
			initCheckers(i);
		}
	}
	private void initCheckers(int i) {
		if (GameConstants.FORCE_A_LOT_MOVES) {
			switch (i) {
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				case 19:
				case 18:
				case 17:
				case 16:
				case 15:
				case 14:
				case 13:
				case 12:
					pips[i].initCheckers(1, Color.WHITE);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_DOUBLE_BAD_PRINT_MOVES) {
			switch (i) {
				case 23:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				case 20:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				case 19:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				case 18:
					pips[i].initCheckers(5, Color.BLACK);
					break;
				case 15:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				case 12:
					pips[i].initCheckers(4, Color.WHITE);
					break;
				case 11:
					pips[i].initCheckers(4, Color.BLACK);
					break;
				case 5:
					pips[i].initCheckers(6, Color.WHITE);
					break;
				case 4:
					pips[i].initCheckers(1, Color.WHITE);
					break;
				case 3:
					pips[i].initCheckers(4, Color.WHITE);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_BAD_PRINT_MOVES) {
			switch (i) {
				case 18:
					pips[i].initCheckers(15, Color.BLACK);
					break;
				case 5:
					pips[i].initCheckers(15, Color.WHITE);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_ONE_CHECKER) {
			switch (i) {
				case 12:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				case 11:
					pips[i].initCheckers(1, Color.WHITE);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_TWO_CHECKER) {
			switch (i) {
				case 12:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				case 11:
					pips[i].initCheckers(2, Color.WHITE);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_TEST_SINGLE) {
			switch (i) {
				case 0:
				case 1:
					pips[i].initCheckers(3, Color.WHITE);
					break;
				case 22:
				case 23:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_TEST_GAMMON) {
			switch (i) {
				case 0:
				case 1:
					pips[i].initCheckers(3, Color.WHITE);
					break;
				case 10:
				case 15:
				case 22:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_TEST_BACKGAMMON) {
			switch (i) {
				case 0:
				case 1:
					pips[i].initCheckers(3, Color.WHITE);
					break;
				case 3:
				case 4:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_CHECKERS_EASY_HITTING_PIP_TO_PIP) {
			switch (i) {
				case 8:
				case 9:
				case 10:
				case 11:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				case 15:
				case 14:
				case 13:
				case 12:
					pips[i].initCheckers(1, Color.WHITE);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_CHECKERS_EASY_HITTING_PIP_TO_HOME) {
			switch (i) {
				case 0:
				case 1:
				case 2:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				case 3:
				case 4:
					pips[i].initCheckers(1, Color.WHITE);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_CHECKERS_EASY_HITTING_PIP_TO_PIP_INTERMEDIATE_MOVES) {
			switch (i) {
				case 10:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				case 9:
				case 11:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				case 14:
				case 13:
				case 12:
					pips[i].initCheckers(1, Color.WHITE);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_CHECKERS_AT_OPPOSITE_HOME_BOARD_AT_FRONT) {
			switch (i) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					pips[i].initCheckers(2, Color.WHITE);
					break;
				case 19:
				case 20:
				case 21:
				case 22:
				case 23:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_CHECKERS_AT_OPPOSITE_HOME_BOARD_AT_BACK) {
			switch (i) {
				case 3:
				case 4:
				case 5:
					pips[i].initCheckers(2, Color.WHITE);
					break;
				case 18:
				case 19:
				case 20:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_CANT_BEAR_OFF_WHEN_HIT) {
			switch (i) {
				case 0:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					pips[i].initCheckers(1, Color.WHITE);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_CHECKERS_IN_HOME_BOARD) {
			switch (i) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					pips[i].initCheckers(2, Color.WHITE);
					break;
				case 5:
					pips[i].initCheckers(5, Color.WHITE);
					break;
				case 23:
				case 22:
				case 21:
				case 20:
				case 19:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				case 18:
					pips[i].initCheckers(5, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_CHECKERS_IN_HOME_BOARD_AT_PIP_5) {
			switch (i) {
				case 0:
				case 1:
				case 2:
				case 3:
					pips[i].initCheckers(2, Color.WHITE);
					break;
				case 4:
					pips[i].initCheckers(5, Color.WHITE);
					break;
				case 23:
				case 22:
				case 21:
				case 20:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				case 19:
					pips[i].initCheckers(5, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_LESS_CHECKERS_IN_HOME_BOARD) {
			switch (i) {
				case 0:
				case 1:
				case 2:
					pips[i].initCheckers(2, Color.WHITE);
					break;
				case 23:
				case 22:
				case 21:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_LESSER_CHECKERS_IN_HOME_BOARD) {
			switch (i) {
				case 0:
				case 1:
					pips[i].initCheckers(1, Color.WHITE);
					break;
				case 22:
				case 23:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_ONE_CHECKER_OUTSIDE_HOME_BOARD) {
			switch (i) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					pips[i].initCheckers(2, Color.WHITE);
					break;
				case 5:
					pips[i].initCheckers(4, Color.WHITE);
					break;
				case 6:
					pips[i].initCheckers(1, Color.WHITE);
					break;
				case 23:
				case 22:
				case 21:
				case 20:
				case 19:
					pips[i].initCheckers(2, Color.BLACK);
					break;
				case 18:
					pips[i].initCheckers(4, Color.BLACK);
					break;
				case 17:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else if (GameConstants.FORCE_OPPONENT_CHECKER_INSIDE_HOME_BOARD) {
			switch (i) {
				case 0:
					pips[i].initCheckers(1, Color.BLACK);
					break;
				case 3:
				case 4:
				case 5:
					pips[i].initCheckers(5, Color.WHITE);
					break;
				case 20:
				case 19:
					pips[i].initCheckers(5, Color.BLACK);
					break;
				case 18:
					pips[i].initCheckers(4, Color.BLACK);
					break;
				default:
					pips[i].removeCheckers();
			}
		} else {
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
				default:
					pips[i].removeCheckers();
			}
		}
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
		if (Settings.getWhiteHomeQuadrant() == Quadrant.BOTTOM_RIGHT) {
			quad4 = new BoardQuadrant(1, 6, PlayerPerspectiveFrom.BOTTOM, Quadrant.BOTTOM_RIGHT, pips);
			quad3 = new BoardQuadrant(7, 12, PlayerPerspectiveFrom.BOTTOM, Quadrant.BOTTOM_LEFT, pips);
			quad2 = new BoardQuadrant(13, 18, PlayerPerspectiveFrom.TOP, Quadrant.TOP_LEFT, pips);
			quad1 = new BoardQuadrant(19, 24, PlayerPerspectiveFrom.TOP, Quadrant.TOP_RIGHT, pips);
			quads.add(quad4);
			quads.add(quad3);
			quads.add(quad2);
			quads.add(quad1);
			whiteQuad = quad4;
			blackQuad = quad1;
	
			rightBoard.setBottom(quad4);
			leftBoard.setBottom(quad3);
			leftBoard.setTop(quad2);
			rightBoard.setTop(quad1);
		} else {
			System.out.println("[Error] Main quadrant not BOTTOM_RIGHT, no initialization for this new quadrant, visit drawPips() in BoardComponents to change.");
		}
	}
	
	// Check if quadrant of pCurrent's home has all the checkers.
	// It checks all quadrants for any pCurrent's checkers,
	// if there is, then all of player's checkers are not in home board.
	public boolean isAllCheckersInHomeBoard(Player pCurrent) {
		BoardQuadrant pQuad = getHomeQuadOfPlayer(pCurrent.getColor());
		boolean hasAllCheckers = true;
		
		if (Settings.getWhiteHomeQuadrant() == Quadrant.BOTTOM_RIGHT) {
			Color pColor = pCurrent.getColor();
			for (BoardQuadrant quad : quads) {
				if (!quad.equals(pQuad) && quad.hasCheckerColor(pColor)) {
					hasAllCheckers = false;
					break;
				}
			}
		}
		return hasAllCheckers;
	}
	
	// if it is able to bear off, but there are better pips to bear off, then we bear off those first.
	// better pips to bear off = pips of the diceResult.
	protected boolean hasBetterPipsToBearOff(Player pCurrent, int fromPip, int diceResult) {
		return getHomeQuadOfPlayer(pCurrent.getColor()).hasBetterPipsToBearOff(pCurrent, fromPip, diceResult);
	}
	
	// get home quad of the player, based on player's color.
	public BoardQuadrant getHomeQuadOfPlayer(Color pColor) {
		BoardQuadrant pQuad = null;
		if (Settings.getWhiteHomeQuadrant() == Quadrant.BOTTOM_RIGHT) {
			if (pColor.equals(Color.WHITE)) {
				pQuad = whiteQuad;
			} else if (pColor.equals(Color.BLACK)) {
				pQuad = blackQuad;
			}
		}
		return pQuad;
	}

	// get dices of the player, based on player's color.
	public Dices getDicesOfPlayer(Color color) {
		Dices theDices = null;
		if (Settings.getWhiteHomeQuadrant() == Quadrant.BOTTOM_RIGHT) {
			if (color.equals(Color.WHITE)) {
				theDices = rightDices;
			} else if (color.equals(Color.BLACK)) {
				theDices = leftDices;
			}
		}
		return theDices;
	}
	
	// get half board of the player, based on player's color.
	public HalfBoard getHalfBoardOfPlayer(Color color) {
		HalfBoard hBoard = null;
		if (Settings.getWhiteHomeQuadrant() == Quadrant.BOTTOM_RIGHT) {
			if (color.equals(Color.WHITE)) {
				hBoard = rightBoard;
			} else if (color.equals(Color.BLACK)) {
				hBoard = leftBoard;
			}
		}
		return hBoard;
	}

	// get cube home of the player, based on player's color.
	public DoublingCubeHome getCubeHomeOfPlayer(Color color) {
		DoublingCubeHome cubeHome = null;
		if (Settings.getWhiteHomeQuadrant() == Quadrant.BOTTOM_RIGHT) {
			if (color.equals(Color.WHITE)) {
				cubeHome = rightCubeHome;
			} else if (color.equals(Color.BLACK)) {
				cubeHome = leftCubeHome;
			}
		}
		return cubeHome;
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
		
		if (isLabelsFlipped) isLabelsFlipped = false;
		else isLabelsFlipped = true;
	}
	
	/**
	 * Returns the pips instance variable (array of pips).
	 * @return the pips instance variable.
	 */
	public Pip[] getPips() {
		return pips;
	}
	
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
	
	public void removeCheckers() {
		// remove from pips.
		for (int i = 0; i < pips.length; i++) {
			pips[i].removeCheckers();
		}
	}
	
	public void reset() {
		initCheckers();
		
		leftDices = null;
		rightDices = null;
		leftBoard.setCenter(null);
		rightBoard.setCenter(null);
		
		if (isLabelsFlipped) swapPipLabels();
		
		dices.reset();
		leftCubeHome.reset();
		rightCubeHome.reset();;
	}
}
