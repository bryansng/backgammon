package game;

import java.util.Iterator;
import java.util.LinkedList;
import constants.DieInstance;
import constants.GameConstants;
import constants.MoveResult;
import exceptions.PlayerNoPerspectiveException;
import game_engine.GameComponentsController;
import game_engine.Player;
import game_engine.Settings;
import interfaces.ColorParser;
import javafx.scene.paint.Color;
import move.BarToPip;
import move.Move;
import move.Moves;
import move.PipToHome;
import move.PipToPip;
import move.RollMoves;

/**
 * This class represents the Board object in Backgammon from the perspective of moves calculation.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class BoardMoves extends BoardComponents implements ColorParser {
	private GameComponentsController game;
	
	public BoardMoves(GameComponentsController game) {
		super();
		this.game = game;
	}
	
	/**
	 * Different from calculateMoves.
	 * 		- it calculates using remaining dice results, instead of adding more.
	 * @param prevMoves
	 * @param pCurrent
	 * @return
	 */
	public Moves recalculateMoves(Moves prevMoves, Player pCurrent) {
		Moves moves = prevMoves;
		// recalculate using remaining dice results.
		for (Iterator<RollMoves> iterRollMoves = moves.iterator(); iterRollMoves.hasNext();) {
			RollMoves aRollMoves = iterRollMoves.next();
			
			aRollMoves.getMoves().clear();
			boolean hasMove = false;
			int rollResult = aRollMoves.getRollResult();
			boolean isSumMove = aRollMoves.isSumRollMoves();
			boolean hasCheckersInBar = hasCheckersInBar(pCurrent);

			// BarToPip
			if (hasCheckersInBar) {
				if (addedAsBarToPipMove(moves, aRollMoves, pCurrent, rollResult, isSumMove))
					hasMove = true;
			// PipToPip or PipToHome
			} else {
				// loop through pips.
				for (int fromPip = 0; fromPip < pips.length; fromPip++) {
					// addAsMove returns a boolean indicating if move is valid and added as move.
					 if (addedAsMove(moves, aRollMoves, pCurrent, fromPip, rollResult, isSumMove)) {
						 hasMove = true;
					 }
				}
			}
			if (!hasMove) {
				iterRollMoves.remove();
			}
		}
		return moves;
	}
	
	/**
	 * Calculate the possible moves based on die roll.
	 * @param rollResult roll die result.
	 * @param pCurrent current player.
	 * @param isRecalculate recalculation doesn't add sumMove.
	 * @return the possible moves.
	 */
	public Moves calculateMoves(int[] rollResult, Player pCurrent) {
		if (GameConstants.FORCE_DOUBLE_INSTANCE) {
			rollResult = dices.getDoubleRoll(DieInstance.DEFAULT);
		} else if (GameConstants.FORCE_DOUBLE_ONES) {
			rollResult = dices.getDoubleOnes(DieInstance.DEFAULT);
		}
		
		// calculate rollmoves of normal moves.
		// calculate rollmoves of sum moves.
		// combine normal moves and sum moves together.
		Moves moves = new Moves();
		calculateNormalMoves(moves, rollResult, pCurrent);
		calculateSumMoves(moves, rollResult, pCurrent);
		return moves;
	}
	
	private LinkedList<Move> calculateNormalMoves(Moves moves, int[] rollResult, Player pCurrent) {
		boolean hasCheckersInBar = hasCheckersInBar(pCurrent);
		
		// consider each die result.
		for (int i = 0; i < rollResult.length; i++) {
			RollMoves rollMoves = new RollMoves(rollResult[i], null);
			
			// BarToPip
			if (hasCheckersInBar) {
				addedAsBarToPipMove(moves, rollMoves, pCurrent, rollResult[i], false);
			// PipToPip or PipToHome
			} else {
				// loop through pips.
				for (int fromPip = 0; fromPip < pips.length; fromPip++) {
					// addAsMove returns a boolean indicating if move is valid and added as move.
					addedAsMove(moves, rollMoves, pCurrent, fromPip, rollResult[i], false);
				}
			}
			moves.add(rollMoves);
		}
		return null;
	}
	
	private LinkedList<Move> calculateSumMoves(Moves moves, int[] rollResult, Player pCurrent) {
		boolean hasCheckersInBar = hasCheckersInBar(pCurrent);
		
		DieInstance instance = DieInstance.DEFAULT;
		if (rollResult[0] == rollResult[1]) {
			instance = DieInstance.DOUBLE;
		}
		
		// Consider sum of roll die results.
		// sumMove starts at 2nd element in rollResult.
		int theSum = 0;
		for (int i = 0; i < rollResult.length; i++) {
			theSum += rollResult[i];
			
			if (i > 0) {
				RollMoves rollMoves = new RollMoves(theSum, calculateDependentRollMoves(moves, 0, i));
				
				// BarToPip
				if (hasCheckersInBar) {
					addedAsBarToPipMove(moves, rollMoves, pCurrent, theSum, true);
				// PipToPip or PipToHome
				} else {
					for (int fromPip = 0; fromPip < pips.length; fromPip++) {
						addedAsMove(moves, rollMoves, pCurrent, fromPip, theSum, true);
					}
				}
				moves.add(rollMoves);
				
				// links the other sumRollMoves to other normalRollMoves.
				// i.e. [3,3,3,3] has [6,6]. First 6 is taken care of above, Second 6 below.
				if (instance == DieInstance.DOUBLE && i == rollResult.length/2-1) {
					rollMoves = new RollMoves(rollMoves);
					rollMoves.setDependentRollMoves(calculateDependentRollMoves(moves, i+1, rollResult.length-1));
					moves.add(rollMoves);
				}
			}
		}
		
		return null;
	}
	
	private LinkedList<RollMoves> calculateDependentRollMoves(Moves moves, int startRange, int endRange) {
		LinkedList<RollMoves> dependentRollMoves = new LinkedList<>();
		for (int i = startRange; i <= endRange; i++) {
			dependentRollMoves.add(moves.get(i));
		}
		return dependentRollMoves;
	}
	
	private boolean hasCheckersInBar(Player pCurrent) {
		return !game.getBars().getBar(pCurrent.getColor()).isEmpty();
	}
	
	private boolean addedAsBarToPipMove(Moves moves, RollMoves rollMoves, Player pCurrent, int diceResult, boolean isSumMove) {
		boolean addedAsMove = false;
		
		MoveResult moveResult;
		int toPip = getPossibleToPip(pCurrent, getBearOnFromPip(pCurrent), diceResult);
		if (isPipNumberInRange(toPip) && ((moveResult = isBarToPipMove(pCurrent.getColor(), toPip)) != MoveResult.NOT_MOVED) && (moveResult != MoveResult.PIP_EMPTY)) {
			boolean isHit = isHit(moveResult);
			if (isSumMove) {
				LinkedList<Move> intermediateMoves;
				if ((intermediateMoves = isSumMove(moves, getBearOnFromPip(pCurrent), toPip)) != null) {
					rollMoves.getMoves().add(new BarToPip(pCurrent.getColor(), toPip, rollMoves, isHit, intermediateMoves));
					addedAsMove = true;
				}
			} else {
				rollMoves.getMoves().add(new BarToPip(pCurrent.getColor(), toPip, rollMoves, isHit));
				addedAsMove = true;
			}
		}
		return addedAsMove;
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
	private boolean addedAsMove(Moves moves, RollMoves rollMoves, Player pCurrent, int fromPip, int diceResult, boolean isSumMove) {
		boolean addedAsMove = false;
		
		// pipToPip
		if (addedAsPipToPipMove(moves, rollMoves, pCurrent, fromPip, diceResult, isSumMove))
			addedAsMove = true;
		
		// pipToHome
		if (addedAsPipToHomeMove(moves, rollMoves, pCurrent, fromPip, diceResult, isSumMove))
			addedAsMove = true;
		
		return addedAsMove;
	}
	
	private boolean addedAsPipToPipMove(Moves moves, RollMoves rollMoves, Player pCurrent, int fromPip, int diceResult, boolean isSumMove) {
		boolean addedAsMove = false;
		MoveResult moveResult;
		int toPip = getPossibleToPip(pCurrent, fromPip, diceResult);
		if (isPipNumberInRange(toPip) && ((moveResult = isPipToPipMove(fromPip, toPip, pCurrent)) != MoveResult.NOT_MOVED) && (moveResult != MoveResult.PIP_EMPTY)) {
			boolean isHit = isHit(moveResult);
			if (isSumMove) {
				LinkedList<Move> intermediateMoves;
				if ((intermediateMoves = isSumMove(moves, fromPip, toPip)) != null) {
					rollMoves.getMoves().add(new PipToPip(fromPip, toPip, rollMoves, isHit, intermediateMoves));
					addedAsMove = true;
				}
			} else {
				rollMoves.getMoves().add(new PipToPip(fromPip, toPip, rollMoves, isHit));
				addedAsMove = true;
			}
		}
		return addedAsMove;
	}
	
	// fromPip boundary is either -1 or 24.
	private int getBearOnFromPip(Player pCurrent) {
		return Settings.getPipBearOnBoundary(pCurrent.getPOV());
	}
	
	// fromPip boundary is either -1 or 24.
	private int getBearOffToPip(Player pCurrent) {
		return Settings.getPipBearOffBoundary(pCurrent.getPOV());
	}
	
	// check if move is a hit, based on moveResult.
	private boolean isHit(MoveResult moveResult) {
		boolean isHit = false;
		switch (moveResult) {
			case MOVE_TO_BAR:
				isHit = true;
				break;
			default:
		}
		return isHit;
	}
	
	private boolean addedAsPipToHomeMove(Moves moves, RollMoves rollMoves, Player pCurrent, int fromPip, int diceResult, boolean isSumMove) {
		boolean addedAsMove = false;
		int toPip = getPossibleToPip(pCurrent, fromPip, diceResult);
		if (canToHome(pCurrent, fromPip, diceResult, toPip, isSumMove)) {
			if (isSumMove) {
				LinkedList<Move> intermediateMoves;
				if ((intermediateMoves = isSumMove(moves, fromPip, getBearOffToPip(pCurrent))) != null) {
					rollMoves.getMoves().add(new PipToHome(fromPip, pCurrent.getColor(), rollMoves, intermediateMoves));
					addedAsMove = true;
				}
			} else {
				rollMoves.getMoves().add(new PipToHome(fromPip, pCurrent.getColor(), rollMoves));
				addedAsMove = true;
			}
		}
		return addedAsMove;
	}
	
	private boolean canToHome(Player pCurrent, int fromPip, int diceResult, int toPip, boolean isSumMove) {
		boolean canToHome = false;
		
		boolean isCheckersInHomeBoard = isAllCheckersInHomeBoard(pCurrent) && !game.getBars().isCheckersInBar(pCurrent);
		boolean isValidMove = isPipToHomeMove(fromPip) == MoveResult.MOVED_TO_HOME_FROM_PIP;
		boolean hasBetterPipsToBearOff = hasBetterPipsToBearOff(pCurrent, fromPip, diceResult);
		
		// if all checkers at home board, and fromPip not empty.
		if (isCheckersInHomeBoard && isValidMove) {
			// if dont have better pips, and diceResult is greater than the pip number,
			// i.e. rolled [2,6], only checkers at pip 3,2,1,
			// then pip 3 should be able to bear-off with 6.
			//
			// NOTE: a sumMove greater than 6 (toPip will be out of range) is not permitted.
			// i.e. rolled [2,6], sum is [8], checkers at pip 3,2,1,
			// then pip 3 should bear-off with 6, not 8.
			// 
			// NOTE: a sumMove below than 6 is allowed, only if its toPip is atBounds.
			// The next if condition takes care of this.
			if (!hasBetterPipsToBearOff && !isPipNumberInRange(toPip) && !isSumMove) {
				canToHome = true;
			}
			if (isPipNumberAtBounds(toPip)) {
				canToHome = true;
			}
		}
		
		return canToHome;
	}
	
	public MoveResult isBarToPipMove(Color fromBar, int toPip) {
		MoveResult moveResult = MoveResult.NOT_MOVED;
		
		Bar bar = game.getBars().getBar(fromBar);
		if (!bar.isEmpty()) {
			if (bar.topCheckerColorEquals(pips[toPip])) {
				moveResult = MoveResult.MOVED_FROM_BAR;
			} else {
				if (pips[toPip].size() == 1) {
					moveResult = MoveResult.MOVE_TO_BAR;
				}
			}
		} else {
			moveResult = MoveResult.PIP_EMPTY;
		}
		return moveResult;
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
		return (!isPipNumberInRange(pipNum)) && (pipNum == Settings.getTopBearOffBoundary() || pipNum == Settings.getBottomBearOffBoundary());
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
	 * Calculates the possible toPips with the given fromPip, diceResult and pCurrent.
	 * NOTE: This controls direction of toPip, making it one-directional.
	 * @param pCurrent current player.
	 * @param fromPip
	 * @param diceResult roll dice result.
	 * @return toPip.
	 */
	private int getPossibleToPip(Player pCurrent, int fromPip, int diceResult) {
		int toPip = -1;
		
		// BOTTOM - home at bottom, so direction is growing towards 1, toPip gets smaller.
		// TOP - home at top, so direction is growing towards 24, toPip gets bigger.
		switch (pCurrent.getPOV()) {
			case BOTTOM:
				toPip = fromPip - diceResult;
				break;
			case TOP:
				toPip = fromPip + diceResult;
				break;
			default:
				throw new PlayerNoPerspectiveException();
		}
		return toPip;
	}
	
	/**
	 * Check if the toPip is a possible move, i.e. able to place checkers there.
	 * @param fromPip
	 * @param toPip
	 * @param pCurrent current player.
	 * @return the move result if we were to make that move.
	 */
	protected MoveResult isPipToPipMove(int fromPip, int toPip, Player pCurrent) {
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
	 * This function searches, hasIntermediate() determines if it is an intermediate move.
	 * @param moves, the possible moves.
	 * @param fromPip
	 * @param toPip
	 * @return the intermediate move.
	 */
	private LinkedList<Move> isSumMove(Moves moves, int fromPip, int toPip) {
		LinkedList<Move> intermediateMoves = null;
		Move intermediateMove = null;
		for (RollMoves rollMove : moves) {
			for (Move aMove : rollMove.getMoves()) {
				if ((intermediateMove = hasIntermediate(aMove, fromPip, toPip)) != null) {
					intermediateMoves = new LinkedList<>();
					intermediateMoves.add(intermediateMove);
					break;
				}
			}
			if (intermediateMove != null) break;
		}
		return intermediateMoves;
	}
	
	/**
	 * Determines and returns the intermediate move.
	 * @param aMove the move that might be the intermediate move.
	 * @param fro sumMove's fro (BarToPip's -1 or 24, or PipToPip's fromPip).
	 * @param to sumMove's to (PipToHome's -1 or 24, or PipToPip's toPip).
	 * @return the intermediate move.
	 */
	private Move hasIntermediate(Move aMove, int fro, int to) {
		// it is an intermediate move if,
		// the move's fromPip is sumMove's fromPip.
		// the move's toPip lies between sumMove's fromPip and sumMove's toPip.
		//
		// for BarToPip, fromPip will be -1 or 24,
		// the move's fromPip and toPip will lie between the fro and to.
		//
		// for PipToHome, toPip will be -1 or 24,
		// the move's fromPip and toPip will lie between the fro and to.
		Move intermediateMove = null;
		if (aMove instanceof PipToPip) {
			PipToPip move = (PipToPip) aMove;
			if (fro == move.getFromPip()) {
				if (to > fro) {
					// if move's to pip is within the range of fromPip and toPip.
					if (move.getToPip() > fro && move.getToPip() < to) {
						intermediateMove = move;
					}
				} else {
					if (move.getToPip() < fro && move.getToPip() > to) {
						intermediateMove = move;
					}
				}
			}
		} else if (aMove instanceof PipToHome) {
			PipToHome move = (PipToHome) aMove;
			if (fro == move.getFromPip()) {
				if (to > fro) {
					// if move's to pip is within the range of fromPip and toPip.
					if (move.getToHomePipNum() > fro && move.getToHomePipNum() < to) {
						intermediateMove = move;
					}
				} else {
					if (move.getToHomePipNum() < fro && move.getToHomePipNum() > to) {
						intermediateMove = move;
					}
				}
			}
		} else if (aMove instanceof BarToPip) {
			BarToPip move = (BarToPip) aMove;
			if (fro == move.getFromBarPipNum()) {
				if (to > fro) {
					// if move's to pip is within the range of fromPip and toPip.
					if (move.getToPip() > fro && move.getToPip() < to) {
						intermediateMove = move;
					}
				} else {
					if (move.getToPip() < fro && move.getToPip() > to) {
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
