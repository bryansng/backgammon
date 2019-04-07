package game;

import constants.DieInstance;
import constants.GameEndScore;
import constants.MoveResult;
import constants.PlayerPerspectiveFrom;
import constants.Quadrant;
import game_engine.GameComponentsController;
import game_engine.Settings;
import javafx.scene.paint.Color;
import move.BarToPip;
import move.Move;
import move.Moves;
import move.PipToHome;
import move.PipToPip;
import move.RollMoves;

/**
 * This class represents the Board object in Backgammon from the perspective of its functions.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Board extends BoardMoves {
	private GameComponentsController game;
	
	public Board(GameComponentsController game) {
		super(game);
		this.game = game;
	}
	
	// unhighlight both the cube homes that will be in the middle of the two half boards.
	public void unhighlightAllCubeHome() {
		leftCubeHome.unhighlight();
		rightCubeHome.unhighlight();
	}
	
	// display and highlight both cube homes that will be in the middle of the two half boards.
	public void highlightAllCubeHome() {
		game.unhighlightCube();
		leftBoard.setCenter(leftCubeHome);
		rightBoard.setCenter(rightCubeHome);
		leftCubeHome.highlight();
		rightCubeHome.highlight();
	}
	
	// display and highlight a specific cube home.
	// finally, highlight cube.
	public void highlightCubeHome(Color pColor) {
		game.unhighlightCube();
		game.unhighlightAllCubeZones();
		
		DoublingCubeHome cubeHome = getCubeHomeOfPlayer(pColor);
		getHalfBoardOfPlayer(pColor).setCenter(cubeHome);
		cubeHome.highlight();
		game.highlightCube();
	}
	
	// display the cube home that the cube is in.
	public void drawCubeHome() {
		DoublingCubeHome cubeHome = getHomeCubeIsIn();
		getHalfBoardOfPlayer(cubeHome.getColor()).setCenter(cubeHome);
	}
	
	// returns boolean value indicating if cube is in the board.
	// i.e. in one of the two cube homes, leftCubeHome or rightCubeHome.
	public boolean isCubeInBoard() {
		return getHomeCubeIsIn() != null;
	}
	
	// returns the cube home where the cube is in.
	public DoublingCubeHome getHomeCubeIsIn() {
		DoublingCubeHome theCubeHome = null;
		if (!getCubeHomeOfPlayer(Settings.getBottomPerspectiveColor()).isEmpty()) {
			theCubeHome = getCubeHomeOfPlayer(Settings.getBottomPerspectiveColor());
		} else if (!getCubeHomeOfPlayer(Settings.getTopPerspectiveColor()).isEmpty()) {
			theCubeHome = getCubeHomeOfPlayer(Settings.getTopPerspectiveColor());
		}
		return theCubeHome;
	}
	
	/**
	 * Un-highlight the pips and checkers.
	 */
	public void unhighlightPipsAndCheckers() {
		for (int i = 0; i < pips.length; i++) {
			pips[i].setNormalImage();
			
			if (!pips[i].isEmpty()) {
				pips[i].getTopChecker().setNormalImage();
			}
		}
	}
	
	/**
	 * Highlight the pips and homes.
	 * @param exceptPipNum, except this point number.
	 */
	public void highlightAllPipsExcept(int exceptPipNum) {
		game.unhighlightAll();
		
		for (int i = 0; i < pips.length; i++) {
			if (i != exceptPipNum) {
				pips[i].setHighlightImage();
			}
		}
		game.getMainHome().highlight();
	}
	
	/**
	 * Highlight the top checkers of fromPips and fromBars in possible moves.
	 * @param moves the possible moves.
	 */
	public void highlightFromPipsAndFromBarChecker(Moves moves) {
		game.unhighlightAll();
		
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof BarToPip) {
					BarToPip move = (BarToPip) aMove;
					game.getBars().highlight(move.getFromBar());
				// pip to home or pip to pip.
				} else {
					pips[aMove.getFro()].getTopChecker().setHighlightImage();
				}
			}
		}
	}
	
	/**
	 * Highlight the toPips of the fromPip in possible moves.
	 * Used to highlight the toPips that player can move checkers from fromPip. 
	 * @param moves the possible moves.
	 * @param fromPip
	 */
	public void highlightToPipsAndToHome(Moves moves, int fromPip) {
		game.unhighlightAll();
		
		boolean isFromPipInMoves = false;
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					if (move.getFromPip() == fromPip) {
						isFromPipInMoves = true;
						pips[move.getToPip()].setHighlightImage();
					}
				} else if (aMove instanceof PipToHome) {
					PipToHome move = (PipToHome) aMove;
					if (move.getFromPip() == fromPip) {
						isFromPipInMoves = true;
						game.getMainHome().getHome(pips[move.getFromPip()].getTopChecker().getColor()).highlight();
					}
				}
			}
		}
		
		// Highlight the selected pip's top checker.
		// Provided the fromPip is part of the moves.
		if (isFromPipInMoves) pips[fromPip].getTopChecker().setHighlightImage();
	}
	public void highlightToPipsAndToHome(Moves moves, String fromBar) {
		game.unhighlightAll();
		
		boolean isFromBarInMoves = false;
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof BarToPip) {
					BarToPip move = (BarToPip) aMove;
					if (move.getFromBar() == parseColor(fromBar)) {
						isFromBarInMoves = true;
						pips[move.getToPip()].setHighlightImage();
					}
				}
			}
		}
		
		// Highlight the selected pip's top checker.
		// Provided the fromPip is part of the moves.
		if (isFromBarInMoves) game.getBars().highlight(parseColor(fromBar));
	}
	
	/**
	 * Execute the roll dice methods of dices object.
	 * BOTTOM is the player with the perspective from the bottom, dices will be on the right.
	 * TOP is the player with the perspective from the top, dices will be on the left.
	 * @param pov - player's point of view. (i.e. TOP or BOTTOM).
	 * @return result of each dice roll in terms of an array of integers.
	 */
	public DieResults rollDices(PlayerPerspectiveFrom pov) {
		leftBoard.setCenter(null);
		rightBoard.setCenter(null);
		DieResults res = null;
		
		switch (pov) {
			case BOTTOM:
				leftDices = null;
				rightDices = dices;
				rightBoard.setCenter(rightDices);
				leftBoard.setCenter(null);
				res = dices.getTotalRoll(DieInstance.DEFAULT);
				break;
			case TOP:
				rightDices = null;
				leftDices = dices;
				leftBoard.setCenter(leftDices);
				rightBoard.setCenter(null);
				res = dices.getTotalRoll(DieInstance.DEFAULT);
				break;
			case NONE:
				leftBoard.setCenter(null);
				rightBoard.setCenter(null);
		}
		
		return res;
	}
	
	/**
	 * Execute the roll dice methods of dices object.
	 * Used to check which player rolls first.
	 * If draw, roll again.
	 * @param instance, instance where the dices are single, double or default.
	 * @return result of each dice roll in terms of an array of integers.
	 */
	public DieResults rollDices(DieInstance instance) {
		leftBoard.setCenter(null);
		rightBoard.setCenter(null);
		DieResults res = new DieResults();
		
		switch (instance) {
			case SINGLE:
				leftDices = null;
				rightDices = null;
				leftDices = new Dices(Color.RED);
				rightDices = new Dices(Color.RED);
				res.add(((Dices)leftDices).getTotalRoll(instance).getFirst());
				res.add(((Dices)rightDices).getTotalRoll(instance).getFirst());
				leftBoard.setCenter(leftDices);
				rightBoard.setCenter(rightDices);
				break;
			default:
				leftBoard.setCenter(null);
				rightBoard.setCenter(null);
		}
		
		// if draw, roll again.
		if (res.getFirst().getDiceResult() == res.getLast().getDiceResult()) {
			res = rollDices(instance);
		}
		return res;
	}
	
	// Used by game listener to redraw dices.
	public void redrawDices(Color pColor) {
		HalfBoard pBoard = getHalfBoardOfPlayer(pColor);
		pBoard.setCenter(getDicesOfPlayer(pColor));
	}
	public void redrawDices() {
		leftBoard.setCenter(leftDices);
		rightBoard.setCenter(rightDices);
	}
	
	/**
	 * Moves a checker between pips.
	 * i.e. pops a checker from one point and push it to the other.
	 * 
	 * @param fromPip, zero-based index, the point number to pop from.
	 * @param toPip, zero-based index, the point number to push to.
	 * @return returns a integer value indicating if the checker was moved.
	 */
	public MoveResult moveCheckers(int fromPip, int toPip) {
		MoveResult moveResult = isPipToPipMove(fromPip, toPip, null);
		
		switch (moveResult) {
			case MOVED_TO_PIP:
				pips[toPip].push(pips[fromPip].pop());
				pips[toPip].drawCheckers();
				pips[fromPip].drawCheckers();
				break;
			case MOVED_TO_BAR:
				break;
			default:
		}
		
		return moveResult;
	}
	
	// calculate the game score at game end based on rules.
	public int getGameScore(Color loserColor) {
		GameEndScore score = GameEndScore.SINGLE;

		// if loser has checkers bore off.
		if (isCheckersInHome(loserColor))
			score = GameEndScore.SINGLE;
		// if loser has no checkers bore off, and
		// furthest checker is in opponent's inner board or bar.
		else if (isCheckersInBar(loserColor) || isCheckersInWinnerInnerBoard(loserColor))
			score = GameEndScore.BACKGAMMON;
		// if loser has no checkers bore off, and
		// furthest checker is in outer board or their inner board.
		else if (isCheckersInOuterBoard(loserColor) || isCheckersInInnerBoard(loserColor))
			score = GameEndScore.GAMMON;
		
		return score.ordinal();
	}
	
	// checks if loser's checkers are in the outer board.
	private boolean isCheckersInOuterBoard(Color loserColor) {
		if (Settings.getWhiteHomeQuadrant() == Quadrant.BOTTOM_RIGHT || Settings.getWhiteHomeQuadrant() == Quadrant.TOP_RIGHT) {
			return quad2.hasCheckerColor(loserColor) || quad3.hasCheckerColor(loserColor);
		} else {
			return quad1.hasCheckerColor(loserColor) || quad4.hasCheckerColor(loserColor);
		}
	}
	
	// checks if loser's checkers are in their inner board.
	private boolean isCheckersInInnerBoard(Color loserColor) {
		return getHomeQuadOfPlayer(loserColor).hasCheckerColor(loserColor);
	}
	
	// checks if loser's checkers are in their home.
	private boolean isCheckersInHome(Color loserColor) {
		return !game.getMainHome().getHome(loserColor).isEmpty();
	}
	
	// checks if loser's checkers are in winner's inner board.
	private boolean isCheckersInWinnerInnerBoard(Color loserColor) {
		if (loserColor == Settings.getBottomPerspectiveColor()) {
			return getHomeQuadOfPlayer(Settings.getTopPerspectiveColor()).hasCheckerColor(loserColor);
		} else {
			return getHomeQuadOfPlayer(Settings.getBottomPerspectiveColor()).hasCheckerColor(loserColor);
		}
	}
	
	// checks if loser has checkers in his bar.
	private boolean isCheckersInBar(Color loserColor) {
		return !game.getBars().getBar(loserColor).isEmpty();
	}
}
