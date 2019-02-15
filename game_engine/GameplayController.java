package game_engine;

import java.util.Arrays;
import java.util.LinkedList;
import constants.DieInstance;
import constants.MessageType;

public class GameplayController implements ColorParser {
	private boolean startedFlag, rolledFlag, movedFlag;
	
	private Player bottomPlayer, topPlayer, pPrevious, pCurrent, pOpponent;
	private GameComponentsController game;
	private InfoPanel infoPnl;
	
	public GameplayController(GameComponentsController game, InfoPanel infoPnl, Player bottomPlayer, Player topPlayer) {
		this.bottomPlayer = bottomPlayer;
		this.topPlayer = topPlayer;
		this.game = game;
		this.infoPnl = infoPnl;
		
		startedFlag = false;
		rolledFlag = false;
		movedFlag = false;
	}
	
	// should activate by /start.
	public boolean start() {
		boolean isStarted = false;
		
		if (!startedFlag) {
			// reset game entirely.
			reset();
			
			// get which player starts first.
			pCurrent = getFirstPlayerToRoll();
			//pPrevious = pCurrent;
			pOpponent = getSecondPlayerToRoll(pCurrent);
			infoPnl.print("First player to move is: " + pCurrent.getName() + ".");
			
			startedFlag = true;
			isStarted = true;
		}
		
		return isStarted;
	}
	
	// should activate by /roll.
	public boolean roll() {
		boolean isRolled = false;
		
		if (!isGameOver() && !pCurrent.equals(pPrevious)) {
			int[] rollResult;
			
			// handle player 1's moves.
			// 1. roll die.
			rollResult = game.rollDices(pCurrent.getPOV());
			infoPnl.print("Dice result: " + Arrays.toString(rollResult) + ".", MessageType.DEBUG);
			infoPnl.print("Current player: " + pCurrent.getName() + " " + parseColor(pCurrent.getColor()), MessageType.DEBUG);
			
			// 2ai. calculate the possible moves based on die roll.
			// need possible pipe to move from, and possible moves of that pip to move to.
			// possible pip to move from should be in sorted order.
			// possible pip to move to should be in sorted order as well.
			LinkedList<PipMove> moves = game.getPossiblePipsToMove(rollResult, pCurrent, pOpponent);
			for (PipMove m : moves) {
				infoPnl.print(m.toString(), MessageType.DEBUG);
			}
			
			// 2aii. based on roll die, highlight possible points to move checkers from.
			// 2b. based on points clicked, highlight possible points to move to.
			// 2c. upon de-select, highlight again the possible points to move checkers from.
			// 3. based on points clicked, move checkers and update board.
			
			// TODO check if player 1's move caused a game over.
			
			// handle player 2's moves.
			rolledFlag = true;
			isRolled = true;
		}
		
		return isRolled;
	}
	
	// should activate by /next.
	// Swap players - used to change turns.
	public Player next() {
		Player temp = pCurrent;
		pCurrent = pOpponent;
		pOpponent = temp;
		
		return pCurrent;
	}
	
	// TODO reset game entirely.
	// reset board.
	// reset flags.
	public void reset() {
		
	}
	
	// auto roll die to see which player first.
	// if draw, roll again.
	private Player getFirstPlayerToRoll() {
		int[] res = null;
		res = game.rollDices(DieInstance.SINGLE);
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
}
