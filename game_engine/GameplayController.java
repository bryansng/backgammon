package game_engine;

import java.util.Arrays;
import java.util.LinkedList;
import constants.DieInstance;
import constants.MessageType;
import move.Move;
import move.PipToPip;
import move.RollMoves;

public class GameplayController implements ColorParser, InputValidator {
	private LinkedList<RollMoves> moves;
	private boolean startedFlag, rolledFlag, movedFlag;
	
	private Player bottomPlayer, topPlayer, pPrevious, pCurrent, pOpponent;
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
	}
	
	// should activate by /start.
	public void start() {
		// reset game entirely.
		reset();
		
		// get which player starts first.
		pCurrent = getFirstPlayerToRoll();
		//pPrevious = pCurrent;
		pOpponent = getSecondPlayerToRoll(pCurrent);
		infoPnl.print("First player to move is: " + pCurrent.getName() + ".");
		
		startedFlag = true;
	}
	
	// auto roll die to see which player first.
	// if draw, roll again.
	private Player getFirstPlayerToRoll() {
		int[] res = null;
		res = game.getBoard().rollDices(DieInstance.SINGLE);
		int bottomPlayerRoll = res[0];
		int topPlayerRoll = res[1];
		
		if (bottomPlayerRoll > topPlayerRoll) {
			return bottomPlayer;
		} else if (topPlayerRoll > bottomPlayerRoll) {
			return topPlayer;
		} else {
			return getFirstPlayerToRoll();
		}
	}
	
	private Player getSecondPlayerToRoll(Player firstPlayer) {
		if (firstPlayer.equals(topPlayer)) {
			return bottomPlayer;
		} else {
			return topPlayer;
		}
	}
	
	// should activate by /roll.
	public void roll() {
		if (!isGameOver() && !pCurrent.equals(pPrevious)) {
			int[] rollResult;
			
			rollResult = game.getBoard().rollDices(pCurrent.getPOV());
			infoPnl.print("Dice result: " + Arrays.toString(rollResult) + ".", MessageType.DEBUG);
			infoPnl.print("Current player: " + pCurrent.getName() + " " + parseColor(pCurrent.getColor()), MessageType.DEBUG);
			
			moves = game.getBoard().getMoves(rollResult, pCurrent, pOpponent);
			for (RollMoves rollMoves : moves) {
				infoPnl.print(rollMoves.toString(), MessageType.DEBUG);
			}
			
			game.getBoard().highlightFromPipsChecker(moves);
			
			rolledFlag = true;
		}
	}
	
	public void move() {
		// set flag only when there are no moves left.
		if (moves.isEmpty()) {
			movedFlag = true;
		}

		// TODO check if player 1's move caused a game over.
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
					Point[] points = game.getBoard().getPoints();
					if (points[fromPip].isEmpty()) {
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
	public Player next() {
		Player temp = pCurrent;
		pCurrent = pOpponent;
		pOpponent = temp;
		
		rolledFlag = false;
		movedFlag = false;
		
		return pCurrent;
	}
	
	// TODO reset game entirely.
	// reset board.
	// reset flags.
	public void reset() {
		
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
	
	public LinkedList<RollMoves> getValidMoves() {
		return moves;
	}
}
