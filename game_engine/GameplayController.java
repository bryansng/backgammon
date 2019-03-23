package game_engine;

import java.util.HashMap;
import constants.DieInstance;
import constants.GameConstants;
import constants.MessageType;
import game.Bar;
import game.DieResults;
import game.Home;
import game.Pip;
import interfaces.ColorParser;
import interfaces.ColorPerspectiveParser;
import interfaces.IndexOffset;
import interfaces.InputValidator;
import interfaces.IntegerLettersParser;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import move.BarToPip;
import move.Move;
import move.Moves;
import move.PipToHome;
import move.PipToPip;
import move.RollMoves;
import move.SumMove;
import ui.InfoPanel;

/**
 * This class handles the gameplay of Backgammon.
 * Sub-controller of MainController.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class GameplayController implements ColorParser, ColorPerspectiveParser, InputValidator, IndexOffset, IntegerLettersParser {
	private Moves moves, noDuplicateRollMoves;
	private HashMap<String, Move> map;
	private boolean startedFlag, rolledFlag, movedFlag, firstRollFlag, topPlayerFlag, movesMapped;
	private Player bottomPlayer, topPlayer, pCurrent, pOpponent;
	private int stalemateCount;
	
	private GameComponentsController game;
	private InfoPanel infoPnl;
	
	public GameplayController(GameComponentsController game, InfoPanel infoPnl, Player bottomPlayer, Player topPlayer) {
		this.bottomPlayer = bottomPlayer;
		this.topPlayer = topPlayer;
		this.game = game;
		this.infoPnl = infoPnl;
		this.map = new HashMap<>();
		reset();
	}
	
	public void reset() {
		moves = null;
		noDuplicateRollMoves = null;
		startedFlag = false;
		rolledFlag = false;
		movedFlag = false;
		firstRollFlag = true;
		topPlayerFlag = false;
		movesMapped = false;
		stalemateCount = 0;
		map.clear();
	}
	
	/**
	 * Auto roll die to see which player moves first.
	 * Called at /start.
	 */
	public void start() {
		startedFlag = true;
		roll();
	}
	
	/**
	 * Rolls die, calculates possible moves and highlight top checkers.
	 * Called at /roll.
	 */
	public void roll() {
		// start() calls this method(),
		// we only get the first player once.
		DieResults rollResult;
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
		infoPnl.print("Roll dice result: " + rollResult + ".");
		rolledFlag = true;
		
		// calculate possible moves.
		moves = null;
		moves = game.getBoard().calculateMoves(rollResult, pCurrent);
		handleEndOfMovesCalculation(moves);
	}
	
	/**
	 * Returns first player to roll based on roll die result.
	 * @param rollResult roll die result.
	 * @return first player to roll.
	 */
	private Player getFirstPlayerToRoll(DieResults rollResult) {
		int bottomPlayerRoll = rollResult.getFirst().getDiceResult();
		int topPlayerRoll = rollResult.getLast().getDiceResult();

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
		stalemateCount = 0;
		
		// if game over, then announce winner and reset gameplay.
		if (isGameOver()) {
			infoPnl.print("Game over.", MessageType.ANNOUNCEMENT);
			Home filledHome = game.getMainHome().getFilledHome();
			if (filledHome.equals(game.getMainHome().getHome(topPlayer.getColor())))
				infoPnl.print("Congratulations, " + topPlayer.getName() + " won.");
			else if (filledHome.equals(game.getMainHome().getHome(bottomPlayer.getColor())))
				infoPnl.print("Congratulations, " + bottomPlayer.getName() + " won.");
			reset();
		// else, proceed to gameplay.
		} else {
			updateMovesAfterMoving();
			
			boolean moveMadeCausedPlayerAbleBearOff = !moves.isEmpty() && game.getBoard().isAllCheckersInHomeBoard(pCurrent);
			if (moveMadeCausedPlayerAbleBearOff || moves.hasDiceResultsLeft()) {
				recalculateMoves();
			} else if (moves.isEmpty()) {
				movedFlag = true;
				infoPnl.print("Move over.");
				next();
			} else {
				handleCharacterMapping();
				printMoves();
			}
		}
	}
	
	private void updateMovesAfterMoving() {
		game.getBoard().updateIsHit(moves, pCurrent);
	}
	
	public void recalculateMoves() {
		infoPnl.print("Recalculating moves.", MessageType.DEBUG);
		moves = game.getBoard().recalculateMoves(moves, pCurrent);
		handleEndOfMovesCalculation(moves);
	}
	
	// placed after calculation and recalculation of moves,
	// used to check if there are moves able to be made,
	// if not, end turn for current player, via next().
	private void handleEndOfMovesCalculation(Moves moves) {
		if (isStalemate()) return;
		
		if (moves.hasDiceResultsLeft()) {
			recalculateMoves();
		} else if (moves.isEmpty()) {
			infoPnl.print("No moves available, turn forfeited.", MessageType.WARNING);
			next();
		} else {
			handleCharacterMapping();
			printMoves();
			
			// highlight top checkers.
			game.getBoard().highlightFromPipsAndFromBarChecker(moves);
		}
	}
	
	/**
	 * Checks if it is valid to move checkers from 'fro' to 'to'.
	 * i.e. is it part of possible moves.
	 * @param fro either fromPip or fromBar
	 * @param to  either toPip or toHome (toBar automatically handled internally)
	 * @return boolean value indicating if move is valid.
	 */
	private Move theValidMove = null;
	public boolean isValidMove(String fro, String to) {
		boolean isValidMove = false;
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
		
		updateMovesDuringValidation();
		return isValidMove;
	}
	
	private void updateMovesDuringValidation() {
		if (theValidMove != null) {
			// check and update for the ability to move checkers from
			// intermediate move to sumMove.
			updatePipToPipHopMoves(theValidMove);
			
			moves.removeRollMoves(theValidMove.getRollMoves());
			
			// Pre-emption: if its a valid move, check if it caused the pip to be empty.
			// if so, remove all moves with this fromPip.
			removeMovesOfEmptyCheckersStorer(theValidMove);
		}
	}
	
	private void updatePipToPipHopMoves(Move intermediateMove) {
		RollMoves tempRollMoves = new RollMoves();
		for (RollMoves aRollMoves : moves) {
			// check if any sumMoves in a sumRollMoves have that
			// as an intermediate move.
			if (aRollMoves.isSumRollMoves()) {
				// this prevents us from adding duplicate moves to RollMoves.
				if (!aRollMoves.equalsValueOf(tempRollMoves)) {
					tempRollMoves = aRollMoves;
					for (Move move : aRollMoves.getMoves()) {
						SumMove aMove = (SumMove) move;
						if (aMove.getIntermediateMoves().contains(intermediateMove)) {
							game.getBoard().addPipToPipHopMoves(moves, pCurrent, aMove, intermediateMove);
						}
					}
				}
			}
		}
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
			int fromPip = theMove.getFro();
			
			if (pips[fromPip].size() == 1 || pips[fromPip].isEmpty()) {
				moves.removeMovesOfFro(fromPip);
				infoPnl.print("Removing moves of pip: " + correct(fromPip), MessageType.DEBUG);
			}
		} else if (theMove instanceof BarToPip) {
			Color barColor = ((BarToPip) theMove).getFromBar();
			Bar fromBar = game.getBars().getBar(barColor);
			int fromBarPipNum = theMove.getFro();
			
			if (fromBar.size() == 1 || fromBar.isEmpty()) {
				moves.removeMovesOfFro(fromBarPipNum);
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
		// this needs to be set first,
		// if not during wait, players can /next more than once.
		movedFlag = false;
		
		infoPnl.print("Swapping turns...", MessageType.ANNOUNCEMENT);
		infoPnl.print("It is now " + pOpponent.getName() + "'s (" + parseColor(pOpponent.getColor()) + ") move.");
		
		// pause for 2 seconds before "next-ing".
		if (Settings.ENABLE_NEXT_PAUSE) {
			Timeline nextPause = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
				nextFunction();
			}));
			nextPause.setCycleCount(1);
			nextPause.play();
		} else nextFunction();
		return pCurrent;
	}
	private void nextFunction() {
		// swap players.
		Player temp = pCurrent;
		pCurrent = pOpponent;
		pOpponent = temp;

		if (pCurrent.equals(topPlayer)) {
			topPlayerFlag = true;
		} else {
			topPlayerFlag = false;
		}
		game.getBoard().swapPipLabels();
		
		roll(); // auto roll.
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
		int letterValue = 1;
		String suffix = "";
		String intermediateMove = "";
		String msg = "";
		if (GameConstants.VERBOSE_MODE) msg += "Remaining rollMoves: " + moves.size() + ", moves:";
		else if (GameConstants.DEBUG_MODE) msg += "Remaining rollMoves: " + noDuplicateRollMoves.size() + ", moves:";
		else msg += "Available moves:";
		
		Moves loopMoves = noDuplicateRollMoves;
		if (GameConstants.VERBOSE_MODE) loopMoves = moves;
		
		for (RollMoves aRollMoves : loopMoves) {
			if (GameConstants.VERBOSE_MODE) {
				msg += spaces;
				msg += "Normal: " + aRollMoves.isNormalRollMoves();
				msg += ", Sum: " + aRollMoves.isSumRollMoves();
				msg += ", isUsed: " + aRollMoves.isUsed();
				msg += aRollMoves.printDependentRollMoves(spaces);
			}
			msg += "\n" + spaces + "Roll of " + aRollMoves.getDiceResult() + "\n";
			
			for (Move aMove : aRollMoves.getMoves()) {
				suffix = "";
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					if (move.isHit()) suffix = "*";
					if (GameConstants.VERBOSE_MODE) intermediateMove = printIntermediate(move, extraSpace);
					msg += extraSpace + toLetters(letterValue) + ". " + correct(move.getFromPip()) + "-" + correct(move.getToPip()) + suffix + "\n" + intermediateMove;
				} else if (aMove instanceof PipToHome) {
					PipToHome move = (PipToHome) aMove;
					if (GameConstants.VERBOSE_MODE) intermediateMove = printIntermediate(move, extraSpace);
					msg += extraSpace + toLetters(letterValue) + ". " + correct(move.getFromPip()) + "-Off\n" + intermediateMove;
				} else if (aMove instanceof BarToPip) {
					BarToPip move = (BarToPip) aMove;
					if (move.isHit()) suffix = "*";
					if (GameConstants.VERBOSE_MODE) intermediateMove = printIntermediate(move, extraSpace);
					msg += extraSpace + toLetters(letterValue) + ". Bar-" + correct(move.getToPip()) + suffix + "\n" + intermediateMove;
				}
				letterValue++;
			}
			if (GameConstants.VERBOSE_MODE) msg += "\n";
		}
		infoPnl.print(msg);
	}
	
	private String printIntermediate(SumMove sumMove, String spaces) {
		String s = "";
		if (sumMove.hasIntermediateMoves()) {
			String prefix = "IM - ";
			s += spaces + "IntermediateMoves:\n";
			
			for (Move aMove : sumMove.getIntermediateMoves()) {
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					s += spaces + prefix + "fromPip: " + correct(move.getFromPip()) + ", toPip: " + correct(move.getToPip()) + "\n";
				} else if (aMove instanceof PipToHome) {
					PipToHome move = (PipToHome) aMove;
					s += spaces + prefix + "fromPip: " + correct(move.getFromPip()) + ", toHome\n";
				} else if (aMove instanceof BarToPip) {
					BarToPip move = (BarToPip) aMove;
					s += spaces + prefix + "fromBar: " + parseColor(move.getFromBar()) + ", toPip: " + correct(move.getToPip()) + "\n";
				}
			}
		}
		return s;
	}
	
	/**
	 * Letters should be mapped to non-duplicate moves.
	 * 
	 * The 'moves' instance variable contains duplicate moves since
	 * that is how moves are calculated and maintained.
	 * 
	 * This method handles the translation of
	 * moves -> non-duplicate moves -> character mappings.
	 */
	private void handleCharacterMapping() {
		if (!GameConstants.VERBOSE_MODE) {
			noDuplicateRollMoves = getNoDuplicateRollMoves();
		}
		mapCharToMoves();
	}
	
	/**
	 * Iterates through all the possible moves and associates it with a key.
	 * The key is a letter from the alphabet.
	 */
	private void mapCharToMoves() {
		map.clear();
		Moves loopMoves = noDuplicateRollMoves;
		if (GameConstants.VERBOSE_MODE) loopMoves = moves;
		
		int letterValue = 1;
		for (RollMoves aRollMoves : loopMoves) {
			for (Move aMove : aRollMoves.getMoves()) {
				map.put(toLetters(letterValue), aMove);
				letterValue++;
			}
		}
		movesMapped = true;
	}
	
	/**
	 * Returns character/letter/key to be mapped with a move object.
	 * @return the character/letter/key.
	 */
	@SuppressWarnings("unused")
	private String createKey() {
		char key = 'A';
		int ascii = 0;
		
		String output = "";
		if (map.containsKey("Z")) {
			while (map.containsKey(Character.toString(key) + Character.toString((char) (key + ascii)))) ascii++;
			output = Character.toString(key) + Character.toString((char) (key + ascii));
		} else {
			while (map.containsKey(Character.toString((char) (key + ascii)))) ascii++;
			output = Character.toString((char) (key + ascii));
		}
		return output;
	}
	
	/**
	 * Returns a boolean value indicating whether the key exists in the hashmap.
	 * @param key to search in the hashmap.
	 * @return the boolean value.
	 */
	public boolean isKey(String key) {
		return map.containsKey(key);
	}
	
	/**
	 * Translates the key received from player input to a move command.
	 * @param key to search in the hashmap to get the move object.
	 * @return a move command.
	 */
	public String getMapping(String key) {
		String cmd = "/move ";
		Move aMove = map.get(key);
		if (aMove instanceof PipToPip) {
			cmd += aMove.getFro() + " " + aMove.getTo();
		} else if (aMove instanceof PipToHome) {
			PipToHome theMove = (PipToHome) aMove;
			cmd += theMove.getFromPip() + " " + parseColor(theMove.getToHome());
		} else if (aMove instanceof BarToPip) {
			BarToPip theMove = (BarToPip) aMove;
			cmd += parseColor(theMove.getFromBar()) + " " + theMove.getToPip();
		}
		return cmd;
	}
	
	public boolean isMapped() {
		return movesMapped;
	}
	
	/**
	 * Returns the moves without duplicate roll moves.
	 * @return new moves instance variable without duplicate roll moves.
	 */
	private Moves getNoDuplicateRollMoves() {
		noDuplicateRollMoves = new Moves(moves.getDieResults());
		RollMoves prev = moves.get(0);
		noDuplicateRollMoves.add(prev);
		for (RollMoves curr : moves) {
			if (!prev.equalsValueOf(curr)) {
				noDuplicateRollMoves.add(curr);
				prev = curr;
			}
		}
		return noDuplicateRollMoves;
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
	
	public Player getOpponent() {
		return pOpponent;
	}
	
	/**
	 * Used to detect for stalemates,
	 * i.e. where both players have no possible moves regardless of whatever they rolled.
	 * 
	 * Stalemates are checked after every moves calculation.
	 * - After every move calculation to detect stalements (endless recalculation).
	 * - Stalemates are resolved as long as player moves (counter is reset at move()).
	 */
	private final static int STALEMATE_LIMIT = 30;
	private boolean isStalemate() {
		boolean isStalemate = false;
		
		if (stalemateCount > STALEMATE_LIMIT) {
			infoPnl.print("Stalemate detected. Neither players can move after roll. Ending current game.", MessageType.ERROR);
			isStalemate = true;
			reset();
		} else {
			stalemateCount++;
		}
		return isStalemate;
	}
}
