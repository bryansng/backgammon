package game_engine;

import java.util.Arrays;
import constants.DieInstance;
import constants.GameConstants;
import constants.MessageType;
import game.Bar;
import game.Home;
import game.Pip;
import interfaces.ColorParser;
import interfaces.IndexOffset;
import interfaces.InputValidator;
import javafx.scene.paint.Color;
import move.BarToPip;
import move.Move;
import move.Moves;
import move.PipToHome;
import move.PipToPip;
import move.RollMoves;
import ui.InfoPanel;

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
	private Moves moves;
	private boolean startedFlag, rolledFlag, movedFlag, firstRollFlag, topPlayerFlag;
	private Player bottomPlayer, topPlayer, pCurrent, pOpponent;
	
	private GameComponentsController game;
	private InfoPanel infoPnl;
	
	public GameplayController(GameComponentsController game, InfoPanel infoPnl, Player bottomPlayer, Player topPlayer) {
		this.bottomPlayer = bottomPlayer;
		this.topPlayer = topPlayer;
		this.game = game;
		this.infoPnl = infoPnl;
		resetGameplay();
	}
	
	private void resetGameplay() {
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
			infoPnl.print("First player to move is: " + pCurrent.getName() + ".");
			firstRollFlag = false;
			
			// if first player is top player, then we swap the pip number labels.
			if (pCurrent.equals(topPlayer)) {
				game.getBoard().swapPipLabels();
				topPlayerFlag = true;
			}
		} else {
			rollResult = game.getBoard().rollDices(pCurrent.getPOV());
		}
		rolledFlag = true;
		
		infoPnl.print("Dice result: " + Arrays.toString(rollResult) + ".", MessageType.DEBUG);
		infoPnl.print("Current player: " + pCurrent.getName() + " " + parseColor(pCurrent.getColor()), MessageType.DEBUG);
		
		// calculate possible moves.
		moves = game.getBoard().calculateMoves(rollResult, pCurrent);
		handleEndOfMovesCalculation(moves);
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
		boolean moveMadeCausedPlayerAbleBearOff = !moves.isEmpty() && game.getBoard().isAllCheckersInHomeBoard(pCurrent);
		if (moveMadeCausedPlayerAbleBearOff || moves.hasDiceResultsLeft()) {
			recalculateMoves();
		} else if (moves.isEmpty()) {
			movedFlag = true;
		} else {
			printMoves();
		}

		if (isGameOver()) {
			infoPnl.print("Game over.", MessageType.ANNOUNCEMENT);
			Home filledHome = game.getMainHome().getFilledHome();
			if (filledHome.equals(game.getMainHome().getHome(topPlayer.getColor())))
				infoPnl.print("Congratulations, " + topPlayer.getName() + " won.");
			if (filledHome.equals(game.getMainHome().getHome(bottomPlayer.getColor())))
				infoPnl.print("Congratulations, " + bottomPlayer.getName() + " won.");
			resetGameplay();
		}
	}
	
	public void recalculateMoves() {
		// recalculate moves.
		infoPnl.print("Recalculating moves.", MessageType.DEBUG);
		moves = game.getBoard().recalculateMoves(moves, pCurrent);
		handleEndOfMovesCalculation(moves);
	}
	
	// placed after calculation and recalculation of moves,
	// used to check if there are moves able to be made,
	// if not, end turn for current player, via next().
	private void handleEndOfMovesCalculation(Moves moves) {
		if (moves.hasDiceResultsLeft()) {
			recalculateMoves();
		} else if (moves.isEmpty()) {
			infoPnl.print("No more moves to be made, turn forfeited.", MessageType.WARNING);
			next();
		} else {
			printMoves();
			// highlight top checkers.
			game.getBoard().highlightFromPipsAndFromBarChecker(moves);
		}
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
		
		if ((theValidMove = moves.isValidPipToPip(fro, to)) != null) {
			isValidMove = true;
			infoPnl.print("Is valid PipToPip.", MessageType.DEBUG);
		} else if ((theValidMove = moves.isValidPipToHome(fro, to)) != null) {
			isValidMove = true;
			infoPnl.print("Is valid PipToHome.", MessageType.DEBUG);
		} else if ((theValidMove = moves.isValidBarToPip(fro, to)) != null) {
			isValidMove = true;
			infoPnl.print("Is valid BarToPip.", MessageType.DEBUG);
		}
		
		if (isValidMove) {
			moves.removeRollMoves(theValidMove.getRollMoves());
			
			// Pre-emption: if its a valid move, check if it caused the pip to be empty.
			// if so, remove all moves with this fromPip.
			removeMovesOfEmptyCheckersStorer(theValidMove);
		}
		return isValidMove;
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
		if (theMove instanceof PipToPip || theMove instanceof PipToHome) {
			Pip[] pips = game.getBoard().getPips();
			int fromPip = -1;
			if (theMove instanceof PipToPip) fromPip = ((PipToPip) theMove).getFromPip();
			else if (theMove instanceof PipToHome) fromPip = ((PipToHome) theMove).getFromPip();
			
			if (pips[fromPip].size() == 1 || pips[fromPip].isEmpty()) {
				moves.removeMovesOfFromPip(fromPip);
				infoPnl.print("Removing moves of pip: " + correct(fromPip), MessageType.DEBUG);
			}
		} else if (theMove instanceof BarToPip) {
			Color barColor = ((BarToPip) theMove).getFromBar();
			Bar fromBar = game.getBars().getBar(barColor);

			if (fromBar.size() == 1 || fromBar.isEmpty()) {
				moves.removeMovesOfFromBar(barColor);
				infoPnl.print("Removing moves of bar: " + parseColor(barColor), MessageType.DEBUG);
			}
		}
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
	public void highlightPips(String fromBar) {
		// gameplay mode.
		if (isRolled()) {
			game.getBoard().highlightToPipsAndToHome(getValidMoves(), fromBar);
		// free for all mode, i.e. before /start.
		} else {
			game.getBoard().highlightAllPipsExcept(-1);
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
	
	// prints possible moves, with an useless letter beside the moves.
	private void printMoves() {
		String spaces = "  ";
		String extraSpace = spaces + spaces + spaces;
		char prefix = 'A';
		String suffix = "";
		String intermediateMove = "";
		String msg = "Remaining rollMoves: " + moves.size() + ", moves:";
		for (RollMoves aRollMoves : moves) {
			if (GameConstants.DEBUG_MODE) {
				msg += "\n" + spaces;
				msg += "Normal: " + aRollMoves.isNormalRollMoves();
				msg += ", Sum: " + aRollMoves.isSumRollMoves();
				msg += ", isUsed: " + aRollMoves.isUsed();
				msg += ", Roll of " + aRollMoves.getRollResult() + "\n";
				msg += aRollMoves.printDependentRollMoves(spaces);
			}
			else msg += "\n" + spaces + "Roll of " + aRollMoves.getRollResult() + "\n";
			
			for (Move aMove : aRollMoves.getMoves()) {
				suffix = "";
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					if (move.isHit()) suffix = "*";
					if (GameConstants.DEBUG_MODE) intermediateMove = move.printIntermediate(extraSpace);
					msg += extraSpace + prefix + ". " + correct(move.getFromPip()) + "-" + correct(move.getToPip()) + suffix + "\n" + intermediateMove;
				} else if (aMove instanceof PipToHome) {
					PipToHome move = (PipToHome) aMove;
					if (GameConstants.DEBUG_MODE) intermediateMove = move.printIntermediate(extraSpace);
					msg += extraSpace + prefix + ". " + correct(move.getFromPip()) + "-Off\n" + intermediateMove;
				} else if (aMove instanceof BarToPip) {
					BarToPip move = (BarToPip) aMove;
					if (move.isHit()) suffix = "*";
					if (GameConstants.DEBUG_MODE) intermediateMove = move.printIntermediate(extraSpace);
					msg += extraSpace + prefix + ". Bar-" + correct(move.getToPip()) + suffix + "\n" + intermediateMove;
				}
				prefix++;
			}
		}
		infoPnl.print(msg);
	}
	
	/**
	 * Check if any homes are filled.
	 * Game over when one of the player has all 15 checkers at their home.
	 * @return boolean value indicating if game is over.
	 */
	private boolean isGameOver() {
		return game.getMainHome().getFilledHome() != null;
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
	
	public Moves getValidMoves() {
		return moves;
	}
}
