package game_engine;

import java.util.Arrays;
import constants.MessageType;
import constants.MoveResult;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * This class handles all the commands that is entered by the user.
 * Sub-controller of MainController.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class CommandController implements ColorParser {
	private Stage stage;
	private GameController game;
	private InfoPanel infoPnl;
	
	public CommandController(Stage stage, GameController game, InfoPanel infoPnl) {
		this.stage = stage;
		this.game = game;
		this.infoPnl = infoPnl;
	}
	
	/**
	 * Parse the text variable and runs it as a command.
	 * 
	 * @param text the string containing the command and its arguments.
	 */
	public void runCommand(String text) {
		String[] args = text.split(" ");
		String command = args[0];
		
		if (command.equals("/move")) {
			runMoveCommand(args);
		} else if (command.equals("/roll")) {
			runRollCommand(args);
		} else if (command.equals("/save")) {
			runSaveCommand();
		/**
		 * TODO /clear command, take the font size and height of info panel, calculate the number of lines.
		 * then print that amount of line with spaces.
		 */
		} else if (command.equals("/test")) {	
			runTestCommand();
		} else if (command.equals("/quit")) {	
			runQuitCommand();
		} else {
			infoPnl.print("Unknown Command.", MessageType.ERROR);
		}
	}

	/*
	 * Command: /move fromPip toPip			//both numbers
	 * Command: /move fromBar toPip			//left is a color, right a number
	 * Command: /move fromPip/bar toHome	//left is a color or number, right is a color.
	 * where fromPip and toPip will be one-index number based.
	 * where fromBar is the bar color.
	 * where toHome is the home color.
	*/
	public void runMoveCommand(String[] args) {
		String fro = args[1];
		String to = args[2];
		
		// handle out of bounds input.
		if (isIndexOutOfBounds(fro) || isIndexOutOfBounds(to)) {
			infoPnl.print("Invalid range, must be between 1-" + Settings.getNumberOfPoints() + ".", MessageType.ERROR);
			return;
		}
		
		MoveResult moveResult;
		// move from point/bar to home.
		if (to.equals("white") || to.equals("black")) {
			if (fro.equals("white") || fro.equals("black")) {
				Color fromBar = parseColor(fro);
				moveResult = game.moveToHome(fromBar);
			} else {
				int fromPip = Integer.parseInt(fro);
				moveResult = game.moveToHome(fromPip);
			}
			
			switch (moveResult) {
				case MOVED_TO_HOME_FROM_PIP:
					infoPnl.print("Moved checker from " + fro + " to home.");
					break;
				case MOVED_TO_HOME_FROM_BAR:
					infoPnl.print("Moved checker from bar to home.");
					break;
				default:
					infoPnl.print("Invalid move.", MessageType.ERROR);
			}
		// move from bar to point.
		} else if (fro.equals("white") || fro.equals("black")) {
			Color fromBar = parseColor(fro);
			int toPip = Integer.parseInt(to);
			
			moveResult = game.moveFromBar(fromBar, toPip);
			switch (moveResult) {
				case MOVED_FROM_BAR:
					infoPnl.print("Moving checker from bar to " + toPip + ".");
					break;
				case MOVE_TO_BAR:
					game.moveToBar(toPip);
					game.moveFromBar(fromBar, toPip);
					infoPnl.print("Moving checker from " + toPip + " to bar.");
					infoPnl.print("Moving checker from bar to " + toPip + ".");
					break;
				default:
					infoPnl.print("Invalid move.", MessageType.ERROR);
			}
		// move from point to point.
		} else {
			int fromPip = Integer.parseInt(fro);
			int toPip = Integer.parseInt(to);
			
			moveResult = game.moveCheckers(fromPip, toPip);
			switch (moveResult) {
				case MOVED_TO_PIP:
					infoPnl.print("Moving checker from " + fromPip + " to " + toPip + ".");
					break;
				case MOVE_TO_BAR:
					game.moveToBar(toPip);
					game.moveCheckers(fromPip, toPip);
					infoPnl.print("Moving checker from " + toPip + " to bar.");
					infoPnl.print("Moving checker from " + fromPip + " to " + toPip + ".");
					break;
				default:
					infoPnl.print("Invalid move.", MessageType.ERROR);
			}
		}
	}

	/**
	 * Command: /roll playerNumber
	 * 1 is the player with the perspective from the bottom, dices will be on the left.
	 * 2 is the player with the perspective from the top, dices will be on the right.
	 */
	public void runRollCommand(String[] args) {
		int playerNum;
		if (args.length == 1) {
			playerNum = 1;
		} else {
			playerNum = Integer.parseInt(args[1]);
		}
		
		// rollDices returns null if playerNum is invalid.
		int[] res = game.rollDices(playerNum);
		if (res != null) {
			infoPnl.print("Roll dice results: " + Arrays.toString(res));
		} else {
			infoPnl.print("Player number incorrect. It must be either 1 or 2.", MessageType.ERROR);
		}
	}

	/**
	 * Command: /save
	 * Saves game log (text on info panel) to text file .
	 */
	public void runSaveCommand() {
		infoPnl.saveToFile();
	}
	
	/**
	 * Command: /test
	 * Tests /move command, by moving checkers from 1-24, to hit, to bear off and bear on.
	 */
	private int checkerPos = 24;
	private int step = 1;
	private Timeline hitTl, bearOnTL, bearOffTL, traversalTl;
	public void runTestCommand() {
		// test hit.
		hitTl = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
			switch (step) {
				case 1:
					infoPnl.print("Testing hit.");
					runCommand("/move 1 2");
					break;
				case 2:
					runCommand("/move 6 2");
					break;
				default:
					infoPnl.print("Hit testing done.");
					infoPnl.printNewline(2);
					step = 1;
					bearOnTL.play();
					return;
			}
			step++;
		}));
		
		// test bear-on.
		bearOnTL = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
			switch (step) {
				case 1:
					infoPnl.print("Testing bear-on.");
					runCommand("/move black 2");
					break;
				default:
					infoPnl.print("Bear-on testing done.");
					infoPnl.printNewline(2);
					step = 1;
					bearOffTL.play();
					return;
			}
			step++;
		}));
		
		
		// test bear-off.
		bearOffTL = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
			switch (step) {
				case 1:
					infoPnl.print("Testing bear-off.");
					runCommand("/move 6 white");
					break;
				default:
					infoPnl.print("Bear-off testing done.");
					infoPnl.printNewline(2);
					infoPnl.print("Testing checkers traversal.");
					step = 1;
					traversalTl.play();
					return;
			}
			step++;
		}));
		
		// start from 24, go all the way until 1, white board.
		traversalTl = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
			if (checkerPos < 2) {
				infoPnl.print("Checkers traversal done.");
				infoPnl.printNewline(2);
				return;
			}
			runCommand("/move " + checkerPos + " " + (checkerPos-1));
			checkerPos--;
		}));
		hitTl.setCycleCount(3);
		bearOnTL.setCycleCount(2);
		bearOffTL.setCycleCount(2);
		traversalTl.setCycleCount(Settings.getNumberOfPoints());

		infoPnl.printNewline(2);
		hitTl.play();
	}

	/**
	 * Command: /quit
	 * Saves game log and prompts player to quit before quitting application.
	 */
	public void runQuitCommand() {
		stage.fireEvent(new WindowEvent(infoPnl.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	/**
	 * Check if the arguments of /move command is within bounds, i.e. 0-24.
	 * It ignores bar or homes, it only checks for pip indexes.
	 * 
	 * @param arg Argument of the /move command.
	 * @return boolean value indicating if the argument is out of bounds.
	 */
	private boolean isIndexOutOfBounds(String arg) {
		// pipIndex can be strings ("white" or "black"), so we deal with that.
		boolean isString = false;
		boolean isOutOfBounds = false;
		
		int pipNum = -1;
		try {
			pipNum = Integer.parseInt(arg);
		} catch (NumberFormatException e) {
			isString = true;
		}
		
		if (!isString) {
			if (!(pipNum >= 0 && pipNum <= Settings.getNumberOfPoints())) {
				isOutOfBounds = true;
			}
		}
		return isOutOfBounds;
	}
}
