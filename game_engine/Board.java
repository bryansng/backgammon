package game_engine;

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
 * This class represents the Board object in Backgammon.
 * This class initializes an array of pips with their starting checkers.
 * This class creates a board made out of modular panes.
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
	 * Un-highlight the pips.
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
	
	public void highlightFromPipsChecker(LinkedList<RollMoves> moves) {
		unhighlightPipsAndCheckers();

		PipToPip move = null;
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					move = (PipToPip) aMove;
					pips[move.getFromPip()].top().setHighlightImage();
				}
			}
		}
	}
	
	public void highlightToPips(LinkedList<RollMoves> moves, int fromPip) {
		unhighlightPipsAndCheckers();
		
		boolean isFromPipInMoves = false;
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					if (move.getFromPip() == fromPip) {
						isFromPipInMoves = true;
						pips[move.getToPip()].setHighlightImage();
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
	 * 
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
	 * @param instance instance where the dices are single, double or default.
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
	
	// 2ai. calculate the possible moves based on die roll.
	public LinkedList<RollMoves> getMoves(int[] rollResult, Player pCurrent, Player pOpponent) {
		// TODO before begin, check if its single or double cube instance.
		//check here.
		
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
				// NOTE: if its a double instance, we simply add the rollMoves twice at the end.
				//add here.
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
			// NOTE: if its a double instance, we simply add the rollMoves twice at the end.
			//add here.
		}
		
		return moves;
	}
	
	// Adds a new move to rollMoves depending if it is a valid move.
	// Currently only considers PipToPip.
	private boolean addedAsMove(LinkedList<RollMoves> moves, RollMoves rollMoves, Player pCurrent, int fromPip, int diceResult, boolean isSumMove) {
		boolean addedAsMove = false;
		
		int toPip = getPossibleToPip(pCurrent, fromPip, diceResult);
		// TODO getPossibleToHome etc.
		
		MoveResult moveResult;
		if (isInRange(toPip) && ((moveResult = isMove(fromPip, toPip, pCurrent)) != MoveResult.NOT_MOVED) && (moveResult != MoveResult.PIP_EMPTY)) {
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
	
	private boolean isInRange(int toPip) {
		return toPip >= 0 && toPip < GameConstants.NUMBER_OF_PIPS;
	}
	
	// check if the toPip is a possible move, i.e. able to place checkers there.
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
	
	// it is sum move if it has an intermediate move in rollMoves.
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
	
	private Move hasIntermediate(Move aMove, int fromPip, int toPip) {
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
	
	// return boolean value indicating if pip color equals player color.
	// if player is null, return true.
	private boolean isPipColorEqualsPlayerColor(int pipNum, Player player) {
		boolean isFromPipColourEqualsPlayerColor = true;
		
		if (player != null && !pips[pipNum].isEmpty()) {
			isFromPipColourEqualsPlayerColor = pips[pipNum].topCheckerColourEquals(player.getColor());
		}
		
		return isFromPipColourEqualsPlayerColor;
	}
}
