package game_engine;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import constants.DieInstance;
import constants.MessageType;
import move.BarToPip;
import move.Move;
import move.PipToHome;
import move.PipToPip;
import move.RollMoves;

/**
 * This class handles the gameplay of Backgammon.
 * Sub-controller of MainController.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class GameplayController implements ColorParser, InputValidator, IndexOffset {
	private LinkedList<RollMoves> moves;
	private boolean startedFlag, rolledFlag, movedFlag, firstRollFlag, topPlayerFlag;
	private Player bottomPlayer, topPlayer, pCurrent, pOpponent;
	
	private GameComponentsController game;
	private InfoPanel infoPnl;
	
	public GameplayController(GameComponentsController game, InfoPanel infoPnl, Player bottomPlayer, Player topPlayer) {
		this.bottomPlayer = bottomPlayer;
		this.topPlayer = topPlayer;
		this.game = game;
		this.infoPnl = infoPnl;
		
		moves = null;
		startedFlag = false;
		rolledFlag = false;
		movedFlag = false;
		firstRollFlag = true;
		topPlayerFlag = false;
	}
	
	/**
	 * Auto roll die to see which player moves first.
	 * Called at /start.
	 */
	public void start() {
		roll();
		startedFlag = true;
	}
	
	/**
	 * Rolls die, calculates possible moves and highlight top checkers.
	 * Called at /roll.
	 */
	public void roll() {
		// start() calls this method(),
		// we only get the first player once.
		int[] rollResult;
		if (firstRollFlag) {
			rollResult = game.getBoard().rollDices(DieInstance.SINGLE);
			pCurrent = getFirstPlayerToRoll(rollResult);
			pOpponent = getSecondPlayerToRoll(pCurrent);
			infoPnl.print( "First player to move is: " + pCurrent.getName() + ".");
			firstRollFlag = false;
			
			// if first player is top player, then we swap the pip number labels.
			if (pCurrent.equals(topPlayer)) {
				game.getBoard().swapPipLabels();
				topPlayerFlag = true;
			}
		} else {
			rollResult = game.getBoard().rollDices(pCurrent.getPOV());
		}
		
		infoPnl.print("Dice result: " + Arrays.toString(rollResult) + ".", MessageType.DEBUG);
		infoPnl.print("Current player: " + pCurrent.getName() + " " + parseColor(pCurrent.getColor()), MessageType.DEBUG);
		
		// calculate possible moves.
		moves = game.getBoard().getMoves(rollResult, pCurrent, pOpponent);
		printMoves();
		
		// highlight top checkers.
		game.getBoard().highlightFromPipsAndFromBarChecker(moves);
		rolledFlag = true;
	}
	
	// prints possible moves, with an useless letter beside the moves.
	private void printMoves() {
		String spaces = "  ";
		String extraSpace = spaces + spaces + spaces;
		char prefix = 'A';
		String msg = "Remaining moves:";
		for (RollMoves aRollMoves : moves) {
			msg += "\n" + spaces + "Roll of " + aRollMoves.getRollResult() + "\n";
			for (Move aMove : aRollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					msg += extraSpace + prefix + ". " + correct(move.getFromPip()) + "-" + correct(move.getToPip()) + "\n";
				} else if (aMove instanceof PipToHome) {
					PipToHome move = (PipToHome) aMove;
					msg += extraSpace + prefix + ". " + correct(move.getFromPip()) + "-Off\n";
				} else if (aMove instanceof BarToPip) {
					BarToPip move = (BarToPip) aMove;
					msg += extraSpace + prefix + ". Bar-" + correct(move.getToPip()) + "\n";
				}
				prefix++;
			}
		}
		infoPnl.print(msg);
	}

	/**
	 * Returns first player to roll based on roll die result.
	 * @param rollResult roll die result.
	 * @return first player to roll.
	 */
	private Player getFirstPlayerToRoll(int[] rollResult) {
		int bottomPlayerRoll = rollResult[0];
		int topPlayerRoll = rollResult[1];
		
		if (bottomPlayerRoll > topPlayerRoll) {
			return bottomPlayer;
		} else if (topPlayerRoll > bottomPlayerRoll) {
			return topPlayer;
		}
		return null;
	}
	
	/**
	 * Returns the second player to roll based on first player.
	 * i.e. its one or the other.
	 * @param firstPlayer first player to roll.
	 * @return second player to roll.
	 */
	private Player getSecondPlayerToRoll(Player firstPlayer) {
		if (firstPlayer.equals(topPlayer)) {
			return bottomPlayer;
		} else {
			return topPlayer;
		}
	}
	
	/**
	 * Called at /move.
	 */
	public void move() {
		// set flag only when there are no moves left.
		if (moves.isEmpty()) movedFlag = true;
		
		// prints moves left.
		printMoves();

		// TODO check if player's move caused a game over.
		if (isGameOver()) {}
	}
	
	/**
	 * Checks if it is valid to move checkers from 'fro' to 'to'.
	 * i.e. is it part of possible moves.
	 * @param fro either fromPip or fromBar
	 * @param to either toPip or toHome (toBar automatically handled internally)
	 * @return boolean value indicating if move is valid.
	 */
	public boolean isValidMove(String fro, String to) {
		boolean isValidMove = false;
		Move theValidMove = null;
		
		if ((theValidMove = isValidPipToPip(fro, to)) != null) {
			isValidMove = true;
		} else if ((theValidMove = isValidPipToHome(fro, to)) != null) {
			isValidMove = true;
		}
		
		if (isValidMove) {
			removeRollMoves(theValidMove.getRollMoves());
			
			// Pre-emption: if its a valid move, check if it caused the pip to be empty.
			// if so, remove all moves with this fromPip.
			removeMovesOfEmptyCheckersStorer(theValidMove);
		}
		return isValidMove;
	}
	
	private Move isValidPipToHome(String fro, String to) {
		Move theValidMove = null;
		
		if (isPip(fro) && isBarOrHome(to)) {
			int fromPip = Integer.parseInt(fro);
			String toHome = to.toLowerCase();
			infoPnl.print("Selected fromPip: " + (fromPip+1), MessageType.DEBUG);
			infoPnl.print("Selected toHome: " + toHome, MessageType.DEBUG);
			
			for (RollMoves rollMoves : moves) {
				// check if fromPip is part of possible moves.
				PipToHome move = null;
				boolean hasFromPip = false;
				for (Move aMove : rollMoves.getMoves()) {
					if (aMove instanceof PipToHome) {
						move = (PipToHome) aMove;
						if (move.getFromPip() == fromPip) {
							hasFromPip = true;
							break;
						}
					}
				}
				
				if (hasFromPip) {
					theValidMove = (Move) move;
					
					// check if fromPip is empty, i.e. no checkers,
					// if so, remove it from possible moves.
					Pip[] pips = game.getBoard().getPips();
					if (pips[fromPip].isEmpty()) {
						// remove the move from the set of moves in its RollMoves.
						rollMoves.getMoves().remove(move);
						theValidMove = null;
					}
				}
			}
		}
		return theValidMove;
	}
	
	@SuppressWarnings("unused")
	private Move isValidBarToPip() {
		return null;
	}
	
	private Move isValidPipToPip(String fro, String to) {
		Move theValidMove = null;
		
		if (isPip(fro) && isPip(to)) {
			int fromPip = Integer.parseInt(fro);
			int toPip = Integer.parseInt(to);
			infoPnl.print("Selected fromPip: " + (fromPip+1), MessageType.DEBUG);
			infoPnl.print("Selected toPip: " + (toPip+1), MessageType.DEBUG);
			
			for (RollMoves rollMoves : moves) {
				// check if fromPip is part of possible moves.
				PipToPip move = null;
				boolean hasFromPip = false;
				for (Move aMove : rollMoves.getMoves()) {
					if (aMove instanceof PipToPip) {
						move = (PipToPip) aMove;
						if (move.getFromPip() == fromPip) {
							hasFromPip = true;
							break;
						}
					}
				}
				
				if (hasFromPip) {
					// check if toPip is part of fromPip's possible toPips.
					if (move.getToPip() == toPip) theValidMove = (Move) move;
					
					// check if fromPip is empty, i.e. no checkers,
					// if so, remove it from possible moves.
					Pip[] pips = game.getBoard().getPips();
					if (pips[fromPip].isEmpty()) {
						// remove the move from the set of moves in its RollMoves.
						rollMoves.getMoves().remove(move);
						theValidMove = null;
					}
				}
			}
		}
		return theValidMove;
	}
	
	/**
	 * Helper function of isValidMove().
	 * Used to check if by executing 'theMove', the fromPip of 'theMove' becomes empty.
	 * If the fromPip will become empty, we remove all other possible moves that rely
	 * on this fromPip (because no checker at the fromPip means no move, plus nullException
	 * will be raised).
	 * @param theMove 'theMove'.
	 */
	private void removeMovesOfEmptyCheckersStorer(Move theMove) {
		// we use this way of iterating and use iter.remove() to remove,
		// if not while removing will raised ConcurrentModificationException.
		if (theMove instanceof PipToPip) {
			Pip[] pips = game.getBoard().getPips();
			int fromPip = ((PipToPip) theMove).getFromPip();
			if (pips[fromPip].size() == 1) {
				for (Iterator<RollMoves> iterRollMoves = moves.iterator(); iterRollMoves.hasNext();) {
					RollMoves aRollMoves = iterRollMoves.next();
					for (Iterator<Move> iterMove = aRollMoves.getMoves().iterator(); iterMove.hasNext();) {
						Move aMove = iterMove.next();
						if (aMove instanceof PipToPip) {
							if (((PipToPip) aMove).getFromPip() == fromPip) {
								iterMove.remove();
							}
						}
					}
					
					// removes the entire rollMoves if it has no moves left.
					if (aRollMoves.getMoves().isEmpty()) iterRollMoves.remove();
				}
			}
		}
	}
	
	/**
	 * Remove the rollMoves from moves.
	 * i.e. remove everything related to the dice roll result completely from moves.
	 * @param rollMoves rollMoves to remove.
	 */
	private void removeRollMoves(RollMoves rollMoves) {
		removeOtherRollMoves(rollMoves);
		moves.remove(rollMoves);
	}
	
	/**
	 * Removes other rollMoves from moves based on argument 'rollMoves'.
	 * Rules state,
	 * - if sum result is moved, then two other result is forfeited.
	 * - if either one result moved, sum result is forfeited.
	 * @param rollMoves rollMoves that was removed.
	 */
	private void removeOtherRollMoves(RollMoves rollMoves) {
		// we use this way of iterating and use iter.remove() to remove,
		// if not while removing will raised ConcurrentModificationException.
		int count = 1;
		for (Iterator<RollMoves> iter = moves.iterator(); iter.hasNext();) {
			RollMoves aRollMoves = iter.next();

			// if sum moved, remove other two.
			if (rollMoves.isSumMove() && aRollMoves.isNormalMove()) {
				iter.remove();
				if (count == 2) break;
				count++;
			}
			// if not sum moved, remove sum.
			if (rollMoves.isNormalMove() && aRollMoves.isSumMove()) {
				iter.remove();
				break;
			}
		}
	}
	
	/**
	 * Checks if fromPip is part of possible moves.
	 * Only used for mouse clicks of fro.
	 * Atm, considers pips, no bars.
	 * @param fromPip
	 * @return boolean value indicating if fromPip is part of possible moves.
	 */
	public boolean isValidFro(int fromPip) {
		boolean isValidFro = false;
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					if (((PipToPip) aMove).getFromPip() == fromPip) {
						isValidFro = true;
						break;
					}
				} else if (aMove instanceof PipToHome) {
					if (((PipToHome) aMove).getFromPip() == fromPip) {
						isValidFro = true;
						break;
					}
				}
			}
		}
		return isValidFro;
	}
	
	/**
	 * Swap players and pip number labels, used to change turns.
	 * Called at /next.
	 * @return the next player to roll.
	 */
	public Player next() {
		// swap players.
		Player temp = pCurrent;
		pCurrent = pOpponent;
		pOpponent = temp;
		
		if (pCurrent.equals(topPlayer)) {
			topPlayerFlag = true;
		} else {
			topPlayerFlag = false;
		}
		
		roll();		// auto roll.
		game.getBoard().swapPipLabels();
		movedFlag = false;
		return pCurrent;
	}
	
	/**
	 * Highlight pips and checkers based on mode.
	 * Used by EventController.
	 * @param fromPip
	 */
	public void highlightPips(int fromPip) {
		// gameplay mode.
		if (isRolled()) {
			game.getBoard().highlightToPipsAndToHome(getValidMoves(), fromPip);
		// free for all mode, i.e. before /start.
		} else {
			game.getBoard().highlightAllPipsExcept(fromPip);
		}
	}
	
	/**
	 * Unhighlight pips based on mode. 
	 * Used by EventController.
	 */
	public void unhighlightPips() {
		// gameplay mode.
		if (isStarted()) {
			if (isMoved()) game.unhighlightAll();
			else game.getBoard().highlightFromPipsAndFromBarChecker(getValidMoves());
		// free for all mode, i.e. before /start.
		} else {
			game.unhighlightAll();
		}
	}
	
	/**
	 * Check if any homes are filled.
	 * Game over when one of the player has all 15 checkers at their home.
	 * @return boolean value indicating if game is over.
	 */
	private boolean isGameOver() {
		return game.isHomeFilled();
	}
	
	public String correct(int pipNum) {
		return getOutputPipNumber(pipNum, topPlayerFlag);
	}
	
	public boolean isStarted() {
		return startedFlag;
	}
	
	public boolean isRolled() {
		return rolledFlag;
	}
	
	public boolean isMoved() {
		return movedFlag;
	}
	
	public boolean isTopPlayer() {
		return topPlayerFlag;
	}
	
	public LinkedList<RollMoves> getValidMoves() {
		return moves;
	}
}
