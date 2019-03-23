package game;

import java.util.ArrayList;
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
import move.SumMove;

/**
 * This class represents the Board object in Backgammon from the perspective of moves calculation.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class BoardMoves extends BoardComponents implements ColorParser {
	private GameComponentsController game;
	
	public BoardMoves(GameComponentsController game) {
		super();
		this.game = game;
	}
	
	// With the sumMove and intermediate Move,
	// we can calculate the difference in dice result, 
	// then find the remaining RollMoves with that dice result.
	//
	// Once found, we calculate the move to get from the intermediateMove to the sumMove,
	// that will be the new Move added into the RollMoves of the dice result.
	public void addPipToPipHopMoves(Moves moves, Player pCurrent, SumMove sMove, Move intermediateMove) {
		int diceResult = sMove.getRollMoves().getDiceResult() - intermediateMove.getRollMoves().getDiceResult();
		for (RollMoves aRollMoves : moves) {
			if (diceResult == aRollMoves.getDiceResult()) {
				if (sMove instanceof PipToPip) {
					PipToPip im = (PipToPip) intermediateMove;
					PipToPip sm = (PipToPip) sMove;
					int fromPip = im.getToPip();
					int toPip = sm.getToPip();
					boolean isHit = isHit(isPipToPipMove(fromPip, toPip, pCurrent));
					boolean isSumMove = aRollMoves.isSumRollMoves();

					// check if a move with that specific fro and to is already in the RollMoves' moves.
					if (!hasSimilarMoves(fromPip, toPip, aRollMoves)) {
						if (isSumMove) {
							LinkedList<Move> intermediateMoves;
							if ((intermediateMoves = isSumMove(moves, fromPip, toPip, aRollMoves, intermediateMove)) != null) {
								aRollMoves.getMoves().add(new PipToPip(fromPip, toPip, aRollMoves, isHit, intermediateMoves));
							}
						} else {
								aRollMoves.getMoves().add(new PipToPip(fromPip, toPip, aRollMoves, isHit));
						}
					}
				}
			}
		}
	}
	
	// check if there is a similar moves with 'fromPip' and 'toPip' in
	// the moves of aRollMoves.
	private boolean hasSimilarMoves(int fromPip, int toPip, RollMoves aRollMoves) {
		boolean hasSimilar = false;
		for (Move aMove : aRollMoves.getMoves()) {
			if (aMove.getFro() == fromPip && aMove.getTo() == toPip) {
				hasSimilar = true;
				break;
			}
		}
		return hasSimilar;
	}
	
	// update the hits of each moves after a move.
	public void updateIsHit(Moves moves, Player pCurrent) {
		for (RollMoves aRollMoves : moves) {
			for (Move aMove : aRollMoves.getMoves()) {
				// check if the fromPip and toPip gives a isHit.
				// if so, then set the rollMoves as hit.
				if (aMove instanceof BarToPip) {
					BarToPip move = (BarToPip) aMove;
					aMove.setHit(isHit(isBarToPipMove(move.getFromBar(), move.getTo())));
				} else if (aMove instanceof PipToPip) {
					aMove.setHit(isHit(isPipToPipMove(aMove.getFro(), aMove.getTo(), pCurrent)));
				}
			}
		}
	}
	
	/**
	 * Different from calculateMoves.
	 * 		- it calculates using remaining dice results, instead of adding more.
	 * 		- able to specify the range of pips to recalculate moves for. (default is 0 to 23)
	 * @param prevMoves previous possible moves.
	 * @param pCurrent current player.
	 * @return the possible moves.
	 */
	public Moves recalculateMoves(Moves prevMoves, Player pCurrent) {
		int startRange = 0;
		int endRange = pips.length;  
		Moves moves = prevMoves;
		// recalculate using remaining dice results.
		for (Iterator<RollMoves> iterRollMoves = moves.iterator(); iterRollMoves.hasNext();) {
			RollMoves aRollMoves = iterRollMoves.next();
			
			aRollMoves.getMoves().clear();
			boolean hasMove = false;
			Dice dice = aRollMoves.getDice();
			boolean isSumMove = aRollMoves.isSumRollMoves();
			boolean hasCheckersInBar = hasCheckersInBar(pCurrent);

			// BarToPip
			// NOTE again: SumMove don't apply to BarToPips.
			if (hasCheckersInBar && !isSumMove) {
				if (addedAsBarToPipMove(moves, aRollMoves, pCurrent, dice.getDiceResult(), isSumMove))
					hasMove = true;
			// PipToPip or PipToHome
			} else {
				// loop through pips,
				// if white, loop from start of pips array,
				// if black, loop from end of pips array.
				// REASON: moves will be printed in ascending order.
				if (pCurrent.getColor() == Settings.getBottomPerspectiveColor()) {
					for (int fromPip = startRange; fromPip < endRange; fromPip++) {
						// addAsMove returns a boolean indicating if move is valid and added as move.
						 if (addedAsMove(moves, aRollMoves, pCurrent, fromPip, dice.getDiceResult(), isSumMove)) {
							 hasMove = true;
						 }
					}
				} else {
					for (int fromPip = endRange-1; fromPip >= startRange; fromPip--) {
						// addAsMove returns a boolean indicating if move is valid and added as move.
						 if (addedAsMove(moves, aRollMoves, pCurrent, fromPip, dice.getDiceResult(), isSumMove)) {
							 hasMove = true;
						 }
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
	 * @return the possible moves.
	 */
	public Moves calculateMoves(DieResults rollResult, Player pCurrent) {
		if (GameConstants.FORCE_DOUBLE_INSTANCE) {
			rollResult = dices.getDoubleRoll(DieInstance.DOUBLE);
		} else if (GameConstants.FORCE_DOUBLE_ONES) {
			rollResult = dices.getDoubleOnes(DieInstance.DOUBLE);
		} else if (GameConstants.FORCE_DOUBLE_TWOS) {
			rollResult = dices.getDoubleTwos(DieInstance.DOUBLE);
		}
		
		// calculate rollmoves of normal moves.
		// calculate rollmoves of sum moves.
		// combine normal moves and sum moves together.
		Moves moves = new Moves(rollResult);
		calculateNormalMoves(moves, rollResult, pCurrent);
		calculateSumMoves(moves, rollResult, pCurrent);
		return moves;
	}
	
	// calculates the normal moves, creates the move and adds them into 'moves'.
	private void calculateNormalMoves(Moves moves, DieResults dieRes, Player pCurrent) {
		boolean hasCheckersInBar = hasCheckersInBar(pCurrent);
		
		DieInstance instance = DieInstance.DEFAULT;
		if (dieRes.getFirst().getDiceResult() == dieRes.getLast().getDiceResult()) {
			instance = DieInstance.DOUBLE;
		}
		
		// consider each die result.
		for (Iterator<Dice> iterDieRes = dieRes.iterator(); iterDieRes.hasNext();) {
			Dice aDice = iterDieRes.next();
			RollMoves rollMoves = new RollMoves(aDice, null);
			
			// BarToPip
			if (hasCheckersInBar) {
				addedAsBarToPipMove(moves, rollMoves, pCurrent, aDice.getDiceResult(), false);
			// PipToPip or PipToHome
			} else {
				// loop through pips.
				if (pCurrent.getColor() == Settings.getBottomPerspectiveColor()) {
					// loop through pips.
					for (int fromPip = 0; fromPip < pips.length; fromPip++) {
						// addAsMove returns a boolean indicating if move is valid and added as move.
						addedAsMove(moves, rollMoves, pCurrent, fromPip, aDice.getDiceResult(), false);
					}
				} else {
					// loop through pips.
					for (int fromPip = pips.length-1; fromPip >= 0; fromPip--) {
						// addAsMove returns a boolean indicating if move is valid and added as move.
						addedAsMove(moves, rollMoves, pCurrent, fromPip, aDice.getDiceResult(), false);
					}
				}
			}
			moves.add(rollMoves);
			
			// Since double (same dice result),
			// remaining RollMoves will be the same as the first calculated RollMoves.
			//
			// So, we simply add copies of the RollMoves and set their respective
			// dice objects.
			if (instance == DieInstance.DOUBLE) {
				moves.add(new RollMoves(rollMoves).setDice(iterDieRes.next()));
				moves.add(new RollMoves(rollMoves).setDice(iterDieRes.next()));
				moves.add(new RollMoves(rollMoves).setDice(iterDieRes.next()));
				return;
			}
		}
	}

	// calculates the sum moves, creates the move and adds them into 'moves'.
	private void calculateSumMoves(Moves moves, DieResults dieRes, Player pCurrent) {
		boolean hasCheckersInBar = hasCheckersInBar(pCurrent);
		
		DieInstance instance = DieInstance.DEFAULT;
		if (dieRes.getFirst().getDiceResult() == dieRes.getLast().getDiceResult()) {
			instance = DieInstance.DOUBLE;
		}
		
		// Consider sum of roll die results.
		// sumMove starts at 2nd element in rollResult.
		int theSum = 0, i = 0;
		for (Dice aDice : dieRes) {
			theSum += aDice.getDiceResult();
			
			if (i > 0) {
				RollMoves rollMoves;
				
				// links 3rd sumRollMoves to 3 normalRollMoves, mainly the 3 normalRollMoves on the right.
				// i.e. [3,3,3,3] has [6,6,9], [9] is the 3rd,
				// it links with the 3s on the right, starting from the 2nd index.
				if (i == dieRes.size()/2) {	// should be the 3rd element, i = 2.
					// range is 1 to 3.
					rollMoves = new RollMoves(theSum, calculateDependentRollMoves(moves, i-1, dieRes.size()-1));
				} else {
					rollMoves = new RollMoves(theSum, calculateDependentRollMoves(moves, 0, i));
				}

				// BarToPip's sumMoves not calculated, there is issues with its sumMove.
				//
				// PipToPip or PipToHome
				if (!hasCheckersInBar) {
					if (pCurrent.getColor() == Settings.getBottomPerspectiveColor()) {
						// loop through pips.
						for (int fromPip = 0; fromPip < pips.length; fromPip++) {
							addedAsMove(moves, rollMoves, pCurrent, fromPip, theSum, true);
						}
					} else {
						// loop through pips.
						for (int fromPip = pips.length-1; fromPip >= 0; fromPip--) {
							addedAsMove(moves, rollMoves, pCurrent, fromPip, theSum, true);
						}
					}
				}
				moves.add(rollMoves);
				
				// links the other sumRollMoves to other normalRollMoves.
				// i.e. [3,3,3,3] has [6,6]. First 6 is taken care of above, Second 6 below.
				if (instance == DieInstance.DOUBLE && i == dieRes.size()/2-1) {
					rollMoves = new RollMoves(rollMoves);
					rollMoves.setDependentRollMoves(calculateDependentRollMoves(moves, i+1, dieRes.size()-1));
					moves.add(rollMoves);
				}
			}
			i++;
		}
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
			rollMoves.getMoves().add(new BarToPip(pCurrent.getColor(), toPip, rollMoves, isHit));
			addedAsMove = true;
			/*
			// currently, this isSumMove's entire body is not used,
			// because sumMoves actually don't apply to BarToPip moves.
			// i.e. move finish normalMoves of BarToPip,
			// then able to move remaining dice roll results.
			if (isSumMove) {
				LinkedList<Move> intermediateMoves;
				if ((intermediateMoves = isSumMove(moves, getBearOnFromPip(pCurrent), toPip, rollMoves)) != null) {
					rollMoves.getMoves().add(new BarToPip(pCurrent.getColor(), toPip, rollMoves, isHit, intermediateMoves));
					addedAsMove = true;
				}
			}*/
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
		int toPip = getPossibleToPip(pCurrent, fromPip, diceResult);
		
		// PipToPip
		if (addedAsPipToPipMove(moves, rollMoves, pCurrent, fromPip, toPip, diceResult, isSumMove))
			addedAsMove = true;
		
		// PipToHome
		if (addedAsPipToHomeMove(moves, rollMoves, pCurrent, fromPip, toPip, diceResult, isSumMove))
			addedAsMove = true;
		
		return addedAsMove;
	}
	
	private boolean addedAsPipToPipMove(Moves moves, RollMoves rollMoves, Player pCurrent, int fromPip, int toPip, int diceResult, boolean isSumMove) {
		boolean addedAsMove = false;
		MoveResult moveResult;
		if (isPipNumberInRange(toPip) && ((moveResult = isPipToPipMove(fromPip, toPip, pCurrent)) != MoveResult.NOT_MOVED) && (moveResult != MoveResult.PIP_EMPTY)) {
			boolean isHit = isHit(moveResult);
			if (isSumMove) {
				LinkedList<Move> intermediateMoves;
				if ((intermediateMoves = isSumMove(moves, fromPip, toPip, rollMoves)) != null) {
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
	
	private boolean addedAsPipToHomeMove(Moves moves, RollMoves rollMoves, Player pCurrent, int fromPip, int toPip, int diceResult, boolean isSumMove) {
		boolean addedAsMove = false;
		if (canToHome(pCurrent, fromPip, diceResult, toPip, isSumMove)) {
			if (isSumMove) {
				LinkedList<Move> intermediateMoves;
				if ((intermediateMoves = isSumMove(moves, fromPip, getBearOffToPip(pCurrent), rollMoves)) != null) {
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
		boolean isValidMove = isPipToHomeMove(fromPip, pCurrent) == MoveResult.MOVED_TO_HOME_FROM_PIP;
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
	
	public MoveResult isPipToHomeMove(int fromPip, Player pCurrent) {
		MoveResult moveResult = MoveResult.NOT_MOVED;
		if (!pips[fromPip].isEmpty()) {
			if (isPipColorEqualsPlayerColor(fromPip, pCurrent)) {
				moveResult = MoveResult.MOVED_TO_HOME_FROM_PIP;
			}
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
	 * It is sum move if it has an intermediate move in rollMoves,
	 * and if all the required intermediate moves are present
	 * (this is determined by the intermediate moves' RollMoves' dice result).
	 * 
	 * This function searches, hasIntermediate() determines if it is an intermediate move.
	 * @param moves, the possible moves.
	 * @param fro sumMove's fro (BarToPip's -1 or 24, or PipToPip's fromPip).
	 * @param to sumMove's to (PipToHome's -1 or 24, or PipToPip's toPip).
	 * @return the intermediate moves.
	 */
	private LinkedList<Move> isSumMove(Moves moves, int fro, int to, RollMoves theRollMoves) {
		LinkedList<Move> intermediateMoves = new LinkedList<>();
		ArrayList<Integer> diceResults = getDependedDiceResults(theRollMoves);
		Move theMove = null;
		for (RollMoves rollMove : moves) {
			for (Move aMove : rollMove.getMoves()) {
				if ((theMove = hasIntermediate(aMove, fro, to)) != null) {
					intermediateMoves.add(theMove);
					diceResults.remove(Integer.valueOf(theMove.getRollMoves().getDiceResult()));
				}
			}
		}
		if (intermediateMoves.isEmpty() || !diceResults.isEmpty()) {
			return null;
		}
		return intermediateMoves;
	}
	// difference from above,
	// 1. it checks if the 'intermediateMove's RollMoves 
	// is contained in the rollMoves's dependentRollMoves,
	// if it is, then the RollMoves and its moves will be removed,
	// so we don't bother.
	private LinkedList<Move> isSumMove(Moves moves, int fro, int to, RollMoves theRollMoves, Move intermediateMove) {
		LinkedList<Move> intermediateMoves = new LinkedList<>();
		ArrayList<Integer> diceResults = getDependedDiceResults(theRollMoves);
		Move theMove = null;
		for (RollMoves aRollMoves : moves) {
			if (aRollMoves.isNormalRollMoves() || !aRollMoves.getDependedRollMoves().contains(intermediateMove.getRollMoves())) {
				for (Move aMove : aRollMoves.getMoves()) {
					if ((theMove = hasIntermediate(aMove, fro, to)) != null) {
						intermediateMoves.add(theMove);
						diceResults.remove(Integer.valueOf(theMove.getRollMoves().getDiceResult()));
					}
				}
			}
		}
		if (intermediateMoves.isEmpty() || !diceResults.isEmpty()) {
			return null;
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
		if (fro == aMove.getFro()) {
			if (to > fro) {
				// if move's to pip is within the range of fromPip and toPip.
				if (aMove.getTo() > fro && aMove.getTo() < to) {
					intermediateMove = aMove;
				}
			} else {
				if (aMove.getTo() < fro && aMove.getTo() > to) {
					intermediateMove = aMove;
				}
			}
		}
		return intermediateMove;
	}
	
	// Used by isSumMoves to determine if the sumMoves has all the necessary
	// intermediate Moves.
	private ArrayList<Integer> getDependedDiceResults(RollMoves theRollMoves) {
		ArrayList<Integer> diceResults = new ArrayList<>();
		int size = theRollMoves.getDependedRollMoves().size();
		
		// When checking if sumMove for [8] of the roll [2,2,2,2] exists,
		// we check if there is an intermediate move with [2,4,6].
		//
		// NOTE: This ignores normal sum moves, and is only concerned
		// with sumMove of doubles.
		if (size > 2) {
			int pairSum = 0, i = 1;
			for (RollMoves aRollMoves : theRollMoves.getDependedRollMoves()) {
				pairSum += aRollMoves.getDiceResult();
				diceResults.add(pairSum);
				i++;
				if (i == size) break;
			}
		}
		return diceResults;
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
