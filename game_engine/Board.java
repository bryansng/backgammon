package game_engine;

import java.util.LinkedList;
import constants.DieInstance;
import constants.GameConstants;
import constants.MoveResult;
import constants.PlayerPerspectiveFrom;
import exceptions.PlayerNoPerspectiveException;
import javafx.scene.paint.Color;
import move.BarToPip;
import move.Move;
import move.PipToHome;
import move.PipToPip;
import move.RollMoves;

/**
 * This class represents the Board object in Backgammon from the perspective of its functions.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Board extends BoardComponents {
	private GameComponentsController game;
	
	public Board(GameComponentsController game) {
		super();
		this.game = game;
	}
	
	/**
	 * Un-highlight the pips and checkers.
	 */
	public void unhighlightPipsAndCheckers() {
		for (int i = 0; i < pips.length; i++) {
			pips[i].setNormalImage();
			
			if (!pips[i].isEmpty()) {
				pips[i].top().setNormalImage();
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
		game.getMainHome().highlight(Settings.getTopPerspectiveColor());
		game.getMainHome().highlight(Settings.getBottomPerspectiveColor());
	}
	
	/**
	 * Highlight the top checkers of fromPips in possible moves.
	 * @param moves the possible moves.
	 */
	public void highlightFromPipsAndFromBarChecker(LinkedList<RollMoves> moves) {
		game.unhighlightAll();
		
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					pips[move.getFromPip()].top().setHighlightImage();
				} else if (aMove instanceof PipToHome) {
					PipToHome move = (PipToHome) aMove;
					pips[move.getFromPip()].top().setHighlightImage();
				} else if (aMove instanceof BarToPip) {
					@SuppressWarnings("unused")
					BarToPip move = (BarToPip) aMove;
					// TODO need color to know which bar to highlight.
					// cannot use toPip's color, since toPip can be empty.
					//game.getBars().highlight();
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
	public void highlightToPipsAndToHome(LinkedList<RollMoves> moves, int fromPip) {
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
						game.getMainHome().getHome(pips[move.getFromPip()].top().getColor()).highlight();
					}
				}
			}
		}
		
		// Highlight the selected pip's top checker.
		// Provided the fromPip is part of the moves.
		if (isFromPipInMoves) pips[fromPip].top().setHighlightImage();
	}
	
	/**
	 * Execute the roll dice methods of dices object.
	 * BOTTOM is the player with the perspective from the bottom, dices will be on the left.
	 * TOP is the player with the perspective from the top, dices will be on the right.
	 * @param pov - player's point of view. (i.e. TOP or BOTTOM).
	 * @return result of each dice roll in terms of an array of integers.
	 */
	public int[] rollDices(PlayerPerspectiveFrom pov) {
		int[] res = null;
		
		switch (pov) {
			case BOTTOM:
				leftDice = dices;
				rightDice = null;
				res = dices.getTotalRoll(DieInstance.DEFAULT);
				break;
			case TOP:
				leftDice = null;
				rightDice = dices;
				res = dices.getTotalRoll(DieInstance.DEFAULT);
				break;
			case NONE:
				leftDice = null;
				rightDice = null;
		}
		leftBoard.setCenter(leftDice);
		rightBoard.setCenter(rightDice);
		
		return res;
	}
	
	/**
	 * Execute the roll dice methods of dices object.
	 * Used to check which player rolls first.
	 * If draw, roll again.
	 * @param instance, instance where the dices are single, double or default.
	 * @return result of each dice roll in terms of an array of integers.
	 */
	public int[] rollDices(DieInstance instance) {
		int[] res = new int[2];
		
		switch (instance) {
			case SINGLE:
				leftDice = new Dices(Color.RED);
				rightDice = new Dices(Color.RED);
				res[0] = ((Dices)leftDice).getTotalRoll(instance)[0];
				res[1] = ((Dices)rightDice).getTotalRoll(instance)[0];
				break;
			default:
				leftDice = null;
				rightDice = null;
		}
		leftBoard.setCenter(leftDice);
		rightBoard.setCenter(rightDice);
		
		// if draw, roll again.
		if (res[0] == res[1]) {
			res = rollDices(instance);
		}
		return res;
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
	
	/**
	 * Calculate the possible moves based on die roll.
	 * @param rollResult roll die result.
	 * @param pCurrent current player.
	 * @param pOpponent opponent player.
	 * @return the possible moves.
	 */
	public LinkedList<RollMoves> getMoves(int[] rollResult, Player pCurrent, Player pOpponent) {
		if (GameConstants.FORCE_DOUBLE_INSTANCE) {
			rollResult = dices.getDoubleRoll(DieInstance.DEFAULT);
		}
		
		DieInstance instance = DieInstance.DEFAULT;
		if (rollResult[0] == rollResult[1]) {
			instance = DieInstance.DOUBLE;
		}
		
		LinkedList<RollMoves> moves = new LinkedList<>();
		
		// take sum by pairs of rollResult.
		int pairSum = 0;
		
		// consider each die result.
		boolean hasMove;
		RollMoves rollMoves = null;
		for (int i = 0; i < 2; i++) {
			hasMove = false;
			pairSum += rollResult[i];
			rollMoves = new RollMoves(rollResult[i], false);
			
			// loop through pips.
			for (int fromPip = 0; fromPip < pips.length; fromPip++) {
				// addAsMove returns a boolean indicating if move is valid and added as move.
				 if (addedAsMove(moves, rollMoves, pCurrent, fromPip, rollResult[i], false)) {
					 hasMove = true;
				 }
			}
			if (hasMove) {
				moves.add(rollMoves);
				if (instance == DieInstance.DOUBLE) moves.add(rollMoves);
			}
		}

		// consider sum of die result.
		hasMove = false;
		rollMoves = new RollMoves(pairSum, true);
		for (int fromPip = 0; fromPip < pips.length; fromPip++) {
			if (addedAsMove(moves, rollMoves, pCurrent, fromPip, pairSum, true)) {
				hasMove = true;
			}
		}
		if (hasMove) {
			moves.add(rollMoves);
			if (instance == DieInstance.DOUBLE) moves.add(rollMoves);
		}
		
		return moves;
	}
	
	/**
	 * Adds a new move to rollMoves depending if it is a valid move.
	 * @param moves the possible moves
	 * @param rollMoves the roll dice result related to the move at consideration to add.
	 * @param pCurrent current player.
	 * @param fromPip
	 * @param diceResult roll dice result.
	 * @param isSumMove boolean indicating if its a sum move, i.e. 5+10=15.
	 * @return boolean value indicating if the move is added.
	 */
	private boolean addedAsMove(LinkedList<RollMoves> moves, RollMoves rollMoves, Player pCurrent, int fromPip, int diceResult, boolean isSumMove) {
		boolean addedAsMove = false;
		
		// BarToPip
		
		// pipToPip
		if (addedAsPipToPipMove(moves, rollMoves, pCurrent, fromPip, diceResult, isSumMove))
			addedAsMove = true;
		
		// pipToHome
		if (addedAsPipToHomeMove(rollMoves, pCurrent, fromPip, diceResult))
			addedAsMove = true;
		
		return addedAsMove;
	}
	
	private boolean addedAsPipToPipMove(LinkedList<RollMoves> moves, RollMoves rollMoves, Player pCurrent, int fromPip, int diceResult, boolean isSumMove) {
		boolean addedAsMove = false;
		MoveResult moveResult;
		int toPip = getPossibleToPip(pCurrent, fromPip, diceResult);
		if (isPipNumberInRange(toPip) && ((moveResult = isPipToPipMove(fromPip, toPip, pCurrent)) != MoveResult.NOT_MOVED) && (moveResult != MoveResult.PIP_EMPTY)) {
			if (isSumMove) {
				Move intermediateMove;
				if ((intermediateMove = isSumMove(moves, fromPip, toPip)) != null) {
					rollMoves.getMoves().add(new PipToPip(fromPip, toPip, rollMoves, intermediateMove));
					addedAsMove = true;
				}
			} else {
				rollMoves.getMoves().add(new PipToPip(fromPip, toPip, rollMoves));
				addedAsMove = true;
			}
		}
		return addedAsMove;
	}
	
	private boolean addedAsPipToHomeMove(RollMoves rollMoves, Player pCurrent, int fromPip, int diceResult) {
		boolean addedAsMove = false;
		int toPip = getPossibleToPip(pCurrent, fromPip, diceResult);
		if (canToHome(pCurrent, fromPip, diceResult, toPip)) {
			rollMoves.getMoves().add(new PipToHome(fromPip, rollMoves));
			addedAsMove = true;
		}
		return addedAsMove;
	}
	
	// if all checkers at home board.
	// if dice result enough to go to home (enough if its getPossibleToPip is out of bounds).
	// if checkers getting fewer,
	// those at pip 1, 2, 3 can be bear-offed with dice result of 4,5,6.
	private boolean canToHome(Player pCurrent, int fromPip, int diceResult, int toPip) {
		boolean canToHome = false;
		
		if (isAllCheckersInHomeBoard(pCurrent) && !game.getBars().isCheckersInBar(pCurrent) && isPipNumberAtBounds(toPip) && (isPipToHomeMove(fromPip) == MoveResult.MOVED_TO_HOME_FROM_PIP)) {
			if (!hasBetterPipsToBearOff(pCurrent, fromPip, diceResult))
				canToHome = true;
		}
		
		return canToHome;
	}
	
	public MoveResult isPipToHomeMove(int fromPip) {
		MoveResult moveResult = MoveResult.NOT_MOVED;
		if (!pips[fromPip].isEmpty()) {
			moveResult = MoveResult.MOVED_TO_HOME_FROM_PIP;
		} else {
			moveResult = MoveResult.PIP_EMPTY;
		}
		return moveResult;
	}
	
	// used to check if the toPip can reach home, home is a toPip of value -1 and 24.
	private boolean isPipNumberAtBounds(int pipNum) {
		return (!isPipNumberInRange(pipNum)) && (pipNum == -1 || pipNum == GameConstants.NUMBER_OF_PIPS);
	}
	
	/**
	 * Calculates the possible toPips with the given fromPip, diceResult and pCurrent.
	 * NOTE: This controls direction of toPip, making it one-directional.
	 * @param pCurrent current player.
	 * @param fromPip
	 * @param diceResult roll dice result.
	 * @return toPip.
	 */
	private int getPossibleToPip(Player pCurrent, int fromPip, int diceResult) {
		int possibleToPip = -1;
		
		// BOTTOM - home at bottom, so direction is growing towards 1, toPip gets smaller.
		// TOP - home at top, so direction is growing towards 24, toPip gets bigger.
		switch (pCurrent.getPOV()) {
			case BOTTOM:
				possibleToPip = fromPip - diceResult;
				break;
			case TOP:
				possibleToPip = fromPip + diceResult;
				break;
			default:
				throw new PlayerNoPerspectiveException();
		}
		return possibleToPip;
	}
	
	/**
	 * Checks if the pip number is within range 0-23.
	 * @param pipNum pip number.
	 * @return the boolean value indicating if so.
	 */
	public boolean isPipNumberInRange(int pipNum) {
		return pipNum >= 0 && pipNum < GameConstants.NUMBER_OF_PIPS;
	}
	
	/**
	 * Check if the toPip is a possible move, i.e. able to place checkers there.
	 * @param fromPip
	 * @param toPip
	 * @param pCurrent current player.
	 * @return the move result if we were to make that move.
	 */
	private MoveResult isPipToPipMove(int fromPip, int toPip, Player pCurrent) {
		MoveResult moveResult = MoveResult.NOT_MOVED;
		
		if (!pips[fromPip].isEmpty()) {
			if (isPipColorEqualsPlayerColor(fromPip, pCurrent)) {
				if (pips[fromPip].topCheckerColorEquals(pips[toPip])) {
					moveResult = MoveResult.MOVED_TO_PIP;
				} else {
					if (pips[toPip].size() == 1) {
						moveResult = MoveResult.MOVE_TO_BAR;
					}
				}
			}
		} else {
			moveResult = MoveResult.PIP_EMPTY;
		}
		
		return moveResult;
	}
	
	/**
	 * It is sum move if it has an intermediate move in rollMoves.
	 * This function searches, hasIntermediate determines if it is an intermediate move.
	 * @param moves, the possible moves.
	 * @param fromPip
	 * @param toPip
	 * @return the intermediate move.
	 */
	private Move isSumMove(LinkedList<RollMoves> moves, int fromPip, int toPip) {
		Move intermediateMove = null;
		for (RollMoves rollMove : moves) {
			for (Move aMove : rollMove.getMoves()) {
				if ((intermediateMove = hasIntermediate(aMove, fromPip, toPip)) != null) {
					break;
				}
			}
			if (intermediateMove != null) break;
		}
		return intermediateMove;
	}
	
	/**
	 * Determines and returns the intermediate move.
	 * @param aMove the move that might be the intermediate move.
	 * @param fromPip sumMove's fromPip.
	 * @param toPip	sumMove's toPip.
	 * @return the intermediate move.
	 */
	private Move hasIntermediate(Move aMove, int fromPip, int toPip) {
		// it is an intermediate move if,
		// the move's fromPip is sumMove's fromPip.
		// the move's toPip lies between sumMove's fromPip and sumMove's toPip.
		Move intermediateMove = null;
		if (aMove instanceof PipToPip) {
			PipToPip move = (PipToPip) aMove;
			if (fromPip == move.getFromPip()) {
				if (toPip > fromPip) {
					// if move's to pip is within the range of fromPip and toPip.
					if (move.getToPip() > fromPip && move.getToPip() < toPip) {
						intermediateMove = move;
					}
				} else {
					if (move.getToPip() < fromPip && move.getToPip() > toPip) {
						intermediateMove = move;
					}
				}
			}
		}
		return intermediateMove;
	}
	
	/**
	 * Returns boolean value indicating if pip's top checker color equals player color.
	 * If player is null, return true.
	 * @param pipNum pip number.
	 * @param player
	 * @return the boolean value.
	 */
	private boolean isPipColorEqualsPlayerColor(int pipNum, Player player) {
		boolean isFromPipColorEqualsPlayerColor = true;
		if (player != null && !pips[pipNum].isEmpty()) {
			isFromPipColorEqualsPlayerColor = pips[pipNum].topCheckerColorEquals(player.getColor());
		}
		return isFromPipColorEqualsPlayerColor;
	}
}
