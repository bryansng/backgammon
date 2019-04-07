package game_engine;

import java.util.HashMap;
import java.util.LinkedList;
import constants.GameConstants;
import constants.MessageType;
import game.Bar;
import game.Pip;
import interfaces.ColorParser;
import interfaces.ColorPerspectiveParser;
import interfaces.IndexOffset;
import interfaces.InputValidator;
import interfaces.IntegerLettersParser;
import javafx.scene.paint.Color;
import move.BarToPip;
import move.Move;
import move.Moves;
import move.PipToHome;
import move.PipToPip;
import move.RollMoves;
import move.SumMove;
import ui.InfoPanel;

/**
 * This class handles the gameplay moves of Backgammon.
 * Sub-controller of GameplayController.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class GameplayMovesController implements ColorParser, ColorPerspectiveParser, InputValidator, IndexOffset, IntegerLettersParser {
	private Moves moves, noDuplicateRollMoves;
	private HashMap<String, Move> map;
	private boolean isMovesMapped;
	private int stalemateCount;

	private CommandController cmd;
	private GameComponentsController game;
	private GameplayController gameplay;
	private InfoPanel infoPnl;
	
	public GameplayMovesController(GameComponentsController game, GameplayController gameplay, InfoPanel infoPnl) {
		this.game = game;
		this.infoPnl = infoPnl;
		this.gameplay = gameplay;
		this.map = new HashMap<>();
		reset();
	}
	
	public void reset() {
		moves = null;
		noDuplicateRollMoves = null;
		isMovesMapped = false;
		stalemateCount = 0;
		map.clear();
	}
	
	public void setCommandController(CommandController cmd) {
		this.cmd = cmd;
	}
	
	public void recalculateMoves() {
		if (gameplay.isRolled()) {
			infoPnl.print("Recalculating moves.", MessageType.DEBUG);
			moves = game.getBoard().recalculateMoves(moves, gameplay.getCurrent());
			handleEndOfMovesCalculation(moves);
		}
	}
	
	// placed after calculation and recalculation of moves,
	// used to check if there are moves able to be made,
	// if not, end turn for current player, via next().
	public void handleEndOfMovesCalculation(Moves moves) {
		if (isStalemate()) return;
		
		if (moves.hasDiceResultsLeft()) {
			recalculateMoves();
		} else if (moves.isEmpty()) {
			infoPnl.print("No moves available, turn forfeited.", MessageType.WARNING);
			
			// if rolled, but no available moves,
			// we unhighlight the cube.
			game.getCube().setNormalImage();
			infoPnl.print("Setting cube to normal.", MessageType.DEBUG);
			
			// facial expression.
			game.getEmojiOfPlayer(gameplay.getCurrent().getColor()).setLoseFace(true);
			infoPnl.print("Setting facial expressions.", MessageType.DEBUG);
			
			infoPnl.print("Nexting...", MessageType.DEBUG);
			gameplay.next();
		} else {
			handleCharacterMapping();
			// highlight top checkers.
			game.getBoard().highlightFromPipsAndFromBarChecker(moves);

			// Chris's requirements:
			// If only one move left, help player make that move.
			if (Settings.ENABLE_AUTO_PLAY && getTotalNumberOfMoves() == 1) {
				infoPnl.print("Only one move to be made. Moving automatically...");
				cmd.runCommand(getMapping("A"));
			} else {
				printMoves();
			}
		}
	}
	private int getTotalNumberOfMoves() {
		int count = 0;
		for (RollMoves aRollMoves : moves) {
			count += aRollMoves.getMoves().size();
		}
		return count;
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
							game.getBoard().addPipToPipHopMoves(moves, gameplay.getCurrent(), aMove, intermediateMove);
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
	
	// prints possible moves, with an useless letter beside the moves.
	public void printMoves() {
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
	public void handleCharacterMapping() {
		if (!GameConstants.VERBOSE_MODE) {
			noDuplicateRollMoves = getNoDuplicateRollMoves();
			getLargerRollMoves();
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
		isMovesMapped = true;
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
		return isMovesMapped;
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
	
	// Rules:
	// If either number can be played, but not both, player must play the larger one.
	private void getLargerRollMoves() {
		// get total number of moves.
		int numOfMoves = 0;
		for (RollMoves aRollMoves : noDuplicateRollMoves) {
			numOfMoves += aRollMoves.getMoves().size();
			
			// if greater than 2, the above rule don't apply.
			// we simply end the function.
			if (numOfMoves > 2) return;
		}
		
		if (numOfMoves == 2) {
			// get the two moves from the roll moves.
			LinkedList<Move> someMoves = new LinkedList<>();
			for (RollMoves aRollMoves : noDuplicateRollMoves) {
				if (aRollMoves.getMoves().size() == 1) {
					someMoves.add(aRollMoves.getMoves().getFirst());
				}
			}
			
			// check if both moves start from same location (pip/bar).
			if (someMoves.size() == 2) {
				if (someMoves.getFirst().getRollMoves().isNormalRollMoves() && someMoves.getLast().getRollMoves().isNormalRollMoves()) {
					Move move1 = someMoves.getFirst();
					Move move2 = someMoves.getLast();
					
					if (move1.getFro() == move2.getFro()) {
						// get the one with the larger dice result,
						// remove the other one.
						if (move1.getRollMoves().getDiceResult() > move2.getRollMoves().getDiceResult()) {
							move2.getRollMoves().getMoves().remove(move2);
							infoPnl.print("Rule: Removed move of smaller dice roll.", MessageType.DEBUG);
						} else if (move1.getRollMoves().getDiceResult() < move2.getRollMoves().getDiceResult()) {
							move1.getRollMoves().getMoves().remove(move1);
						} else {
							return;
						}
					}
				}
			}
		}
	}
	
	public String correct(int pipNum) {
		return getOutputPipNumber(pipNum, gameplay.isTopPlayer());
	}
	
	public Moves getValidMoves() {
		return moves;
	}
	public void setValidMoves(Moves moves) {
		this.moves = moves;
	}
	public void setStalemateCount(int stalemateCount) {
		this.stalemateCount = stalemateCount;
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
			infoPnl.print("Stalemate detected. Neither players can move after many roll attempts. Ending current game.", MessageType.ERROR);
			isStalemate = true;
			reset();
		} else {
			stalemateCount++;
		}
		return isStalemate;
	}
}
