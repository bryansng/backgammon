package game_engine;

import java.util.HashMap;
import java.util.LinkedList;
import constants.DieInstance;
import constants.GameConstants;
import constants.MoveResult;
import constants.PlayerPerspectiveFrom;
import exceptions.PlayerNoPerspectiveException;
import javafx.scene.paint.Color;
import move.Move;
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
	public Board() {
		super();
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
		MoveResult moveResult = isMove(fromPip, toPip, null);
		
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
	 * Highlight the pips.
	 * @param exceptPipNum, except this point number.
	 */
	public void highlightAllPipsExcept(int exceptPipNum) {
		unhighlightPipsAndCheckers();
		
		for (int i = 0; i < pips.length; i++) {
			if (i != exceptPipNum) {
				pips[i].setHighlightImage();
			}
		}
	}
	
	/**
	 * Highlight the top checkers of fromPips in possible moves.
	 * @param moves the possible moves.
	 */
	public void highlightFromPipsChecker(HashMap<String, Move> test) {
		unhighlightPipsAndCheckers();
		
		for (Move aMove : test.values()) {
			if (aMove instanceof PipToPip) {
				PipToPip move = (PipToPip) aMove;
					pips[move.getFromPip()].top().setHighlightImage();
				}
			}
		}
		
		/*PipToPip move = null;
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					move = (PipToPip) aMove;
					pips[move.getFromPip()].top().setHighlightImage();
				}
			}
		}*/
	
	
	/**
	 * Highlight the toPips of the fromPip in possible moves.
	 * Used to highlight the toPips that player can move checkers from fromPip. 
	 * @param moves the possible moves.
	 * @param fromPip
	 */
	public void highlightToPips(HashMap<String, Move> test, int fromPip) {
		unhighlightPipsAndCheckers();
		

		boolean isFromPipInMoves = false;
		for (Move aMove : test.values()) {
			if (aMove instanceof PipToPip) {
				PipToPip move = (PipToPip) aMove;
				if (move.getFromPip() == fromPip) {
					isFromPipInMoves = true;
					pips[move.getToPip()].setHighlightImage();
				}
			}
		}
		
		
		/*for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					if (move.getFromPip() == fromPip) {
						isFromPipInMoves = true;
						pips[move.getToPip()].setHighlightImage();
					}
				}
			}
		}*/
		
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
	 * Calculate the possible moves based on die roll.
	 * @param rollResult roll die result.
	 * @param pCurrent current player.
	 * @param pOpponent opponent player.
	 * @return the possible moves.
	 */
	
	HashMap<String, Move> test = new HashMap<>();
	public HashMap<String, Move> getMoves(int[] rollResult, Player pCurrent, Player pOpponent) {
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
		
		return test;
	}
	
	private String mapToLetter(int index) {	
		while (test.containsKey(Character.toString((char) index + 65))) {index++;}
		
		return Character.toString((char) index + 65);
	}
	
	/**
	 * Adds a new move to rollMoves depending if it is a valid move.
	 * Currently only considers PipToPip.
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
		
		int toPip = getPossibleToPip(pCurrent, fromPip, diceResult);
		// TODO getPossibleToHome etc.
		
		MoveResult moveResult;
		if (isInRange(toPip) && ((moveResult = isMove(fromPip, toPip, pCurrent)) != MoveResult.NOT_MOVED) && (moveResult != MoveResult.PIP_EMPTY)) {
			if (isSumMove) {
				Move intermediateMove;
				if ((intermediateMove = isSumMove(moves, fromPip, toPip)) != null) {
					test.put(mapToLetter(fromPip), new PipToPip(fromPip, toPip, rollMoves, intermediateMove));
					rollMoves.getMoves().add(new PipToPip(fromPip, toPip, rollMoves, intermediateMove));
					addedAsMove = true;
				}
			} else {
				test.put(mapToLetter(fromPip), new PipToPip(fromPip, toPip, rollMoves));
				rollMoves.getMoves().add(new PipToPip(fromPip, toPip, rollMoves));
				addedAsMove = true;
			}
		}
		return addedAsMove;
	}
	
	/**
	 * Calculates the possible toPips with the given fromPip, diceResult and pCurrent.
	 * @param pCurrent current player.
	 * @param fromPip
	 * @param diceResult roll dice result.
	 * @return toPip.
	 */
	private int getPossibleToPip(Player pCurrent, int fromPip, int diceResult) {
		int possibleToPip = -1;
		
		// BOTTOM - home at bottom, point index from small to big, 1-24. 
		// TOP - home at top, point index from big to small, 24-1.
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
	 * Checks if the toPip is within range 0-23.
	 * @param toPip
	 * @return the boolean value indicating if so.
	 */
	public boolean isInRange(int toPip) {
		return toPip >= 0 && toPip < GameConstants.NUMBER_OF_PIPS;
	}
	
	/**
	 * Check if the toPip is a possible move, i.e. able to place checkers there.
	 * @param fromPip
	 * @param toPip
	 * @param pCurrent current player.
	 * @return the move result if we were to make that move.
	 */
	private MoveResult isMove(int fromPip, int toPip, Player pCurrent) {
		MoveResult moveResult = MoveResult.NOT_MOVED;
		
		if (!pips[fromPip].isEmpty()) {
			if (isPipColorEqualsPlayerColor(fromPip, pCurrent)) {
				if (pips[fromPip].topCheckerColourEquals(pips[toPip])) {
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
		boolean isFromPipColourEqualsPlayerColor = true;
		
		if (player != null && !pips[pipNum].isEmpty()) {
			isFromPipColourEqualsPlayerColor = pips[pipNum].topCheckerColourEquals(player.getColor());
		}
		return isFromPipColourEqualsPlayerColor;
	}
}
