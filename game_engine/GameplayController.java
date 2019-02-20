package game_engine;

import java.util.Arrays;
import java.util.LinkedList;
import constants.DieInstance;
import constants.MessageType;
import move.Move;
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
public class GameplayController implements ColorParser, InputValidator {
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
	
	// should activate by /start.
	// auto roll die to see which player first.
	public void start() {
		// get roll.
		roll();
		
		startedFlag = true;
	}
	
	// should activate by /roll.
	public void roll() {
		// start() calls this method(),
		// we only need to get the first player once.
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
		
		moves = game.getBoard().getMoves(rollResult, pCurrent, pOpponent);
		for (RollMoves rollMoves : moves) {
			infoPnl.print(rollMoves.toString(), MessageType.DEBUG);
		}
		
		game.getBoard().highlightFromPipsChecker(moves);
		
		rolledFlag = true;
	}
	
	private Player getFirstPlayerToRoll(int[] res) {
		int bottomPlayerRoll = res[0];
		int topPlayerRoll = res[1];
		
		if (bottomPlayerRoll > topPlayerRoll) {
			return bottomPlayer;
		} else if (topPlayerRoll > bottomPlayerRoll) {
			return topPlayer;
		}
		return null;
	}
	
	private Player getSecondPlayerToRoll(Player firstPlayer) {
		if (firstPlayer.equals(topPlayer)) {
			return bottomPlayer;
		} else {
			return topPlayer;
		}
	}
	
	public void move() {
		// set flag only when there are no moves left.
		if (moves.isEmpty()) {
			movedFlag = true;
		}

		// TODO check if player's move caused a game over.
		if (isGameOver()) {
		}
	}
	
	// check if it is valid to move checkers from 'fro' to 'to'.
	// i.e. is it part of the possible moves.
	public boolean isValidMove(String fro, String to) {
		boolean isValidMove = false;
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
					if (move.getToPip() == toPip) {
						isValidMove = true;
						theValidMove = (Move) move;
					}
					
					// check if fromPip is empty, i.e. no checkers,
					// if so, remove it from possible moves.
					Pip[] pips = game.getBoard().getPips();
					if (pips[fromPip].isEmpty()) {
						// remove the move from the set of moves in its RollMoves.
						rollMoves.getMoves().remove(move);
						isValidMove = false;
					}
				}
			}
		}
		if (isValidMove) removeRollMoves(theValidMove.getRollMoves());
		
		return isValidMove;
	}
	
	// only used for mouse clicks of fro.
	// atm, only consider pips, no bars.
	public boolean isValidFro(int fromPip) {
		boolean isValidFro = false;
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					if (((PipToPip) aMove).getFromPip() == fromPip) {
						isValidFro = true;
						break;
					}
				}
			}
		}
		return isValidFro;
	}
	
	// remove the rollMoves from the moves.
	// i.e. remove everything related to the dice roll result completely from moves.
	private void removeRollMoves(RollMoves rollMoves) {
		removeOtherRollMoves(rollMoves);
		moves.remove(rollMoves);
	}
	
	// Rules state,
	// - if sum result is moved, then two other result is forfeited.
	// - if either one result moved, sum result is forfeited.
	private void removeOtherRollMoves(RollMoves rollMoves) {
		// if sum moved, remove other two.
		if (rollMoves.isSumMove()) {
			moves = new LinkedList<>();
		}
		
		// if not sum moved, remove sum.
		if (rollMoves.isNormalMove()) {
			for (RollMoves aRollMoves : moves) {
				if (aRollMoves.isSumMove()) {
					moves.remove(aRollMoves);
					break;
				}
			}
		}
	}
	
	// should activate by /next.
	// Swap players - used to change turns.
	// swap the pip labels at each turn.
	public Player next() {
		Player temp = pCurrent;
		pCurrent = pOpponent;
		pOpponent = temp;
		
		if (pCurrent.equals(topPlayer)) {
			topPlayerFlag = true;
		} else {
			topPlayerFlag = false;
		}
		
		roll();
		game.getBoard().swapPipLabels();
		movedFlag = false;
		return pCurrent;
	}
	
	public void highlightPips(int fromPip) {
		if (isRolled()) {
			game.getBoard().highlightToPips(getValidMoves(), fromPip);
			/*
			PipMove aMove = gameplay.getMoveOf(fromPip);
			if (aMove != null) {
				game.getBoard().highlightToPips(aMove);
			} else {
				infoPnl.print("There is no possible moves related to fromPip: " + (fromPip+1), MessageType.DEBUG);
			}
			*/
		} else {
			game.getBoard().highlightAllPipsExcept(fromPip);
		}
	}
	
	public void unhighlightPips() {
		if (isStarted()) {
			if (isMoved()) game.getBoard().unhighlightPipsAndCheckers();
			else game.getBoard().highlightFromPipsChecker(getValidMoves());
		} else {
			game.getBoard().unhighlightPipsAndCheckers();
		}
	}
	
	// game over when one of the player has all 15 checkers at their homes.
	// check if the homes are full.
	private boolean isGameOver() {
		return game.isHomeFilled();
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
