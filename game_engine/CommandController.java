package game_engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import constants.MessageType;
import constants.MoveResult;
import constants.PlayerPerspectiveFrom;
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
public class CommandController implements ColorParser, InputValidator {
	private Stage stage;
	private GameComponentsController game;
	private GameplayController gameplay;
	private InfoPanel infoPnl;
	private Player bottomPlayer, topPlayer;
	
	public CommandController(Stage stage, GameComponentsController game, GameplayController gameplay, InfoPanel infoPnl, Player bottomPlayer, Player topPlayer) {
		this.bottomPlayer = bottomPlayer;
		this.topPlayer = topPlayer;
		this.stage = stage;
		this.game = game;
		this.gameplay = gameplay;
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
		} else if (command.equals("/start")) {
			runStartCommand();
		} else if (command.equals("/save")) {
			runSaveCommand();
		} else if (command.equals("/next")) {
			runNextCommand();
		/**
		 * TODO /clear command, take the font size and height of info panel, calculate the number of lines.
		 * then print that amount of line with spaces.
		 */
		} else if (command.equals("/name")) {	
			runNameCommand(args);
		} else if (command.equals("/help")) {	
			runHelpCommand();
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
			infoPnl.print("Invalid range, must be between 1-" + Settings.NUMBER_OF_POINTS + ".", MessageType.ERROR);
			return;
		}
		
		// validate moves.
		// isRolled only if it started.
		if (gameplay.isRolled()) {
			if (!gameplay.isMoved()) {
				if (gameplay.isValidMove(fro, to)) {
					infoPnl.print("Moving...", MessageType.ANNOUNCEMENT);
					gameplay.move();
				} else {
					game.getBoard().highlightFromPipsChecker(gameplay.getValidMoves());
					infoPnl.print("You can only move pieces to highlighted pips.", MessageType.ERROR);
					return;
				}
			} else {
				infoPnl.print("You have made your move.", MessageType.ERROR);
				return;
			}
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
					infoPnl.print("Moved checker from " + (new Integer(fro).intValue()+1) + " to home.", MessageType.DEBUG);
					break;
				case MOVED_TO_HOME_FROM_BAR:
					infoPnl.print("Moved checker from bar to home.", MessageType.DEBUG);
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
					infoPnl.print("Moving checker from bar to " + (toPip+1) + ".", MessageType.DEBUG);
					break;
				case MOVE_TO_BAR:
					game.moveToBar(toPip);
					game.moveFromBar(fromBar, toPip);
					infoPnl.print("Moving checker from " + (toPip+1) + " to bar.", MessageType.DEBUG);
					infoPnl.print("Moving checker from bar to " + (toPip+1) + ".", MessageType.DEBUG);
					break;
				default:
					infoPnl.print("Invalid move.", MessageType.ERROR);
			}
		// move from point to point.
		} else {
			int fromPip = Integer.parseInt(fro);
			int toPip = Integer.parseInt(to);
			
			moveResult = game.getBoard().moveCheckers(fromPip, toPip);
			switch (moveResult) {
				case MOVED_TO_PIP:
					infoPnl.print("Moving checker from " + (fromPip+1) + " to " + (toPip+1) + ".", MessageType.DEBUG);
					break;
				case MOVE_TO_BAR:
					game.moveToBar(toPip);
					game.getBoard().moveCheckers(fromPip, toPip);
					infoPnl.print("Moving checker from " + (toPip+1) + " to bar.", MessageType.DEBUG);
					infoPnl.print("Moving checker from " + (fromPip+1) + " to " + (toPip+1) + ".", MessageType.DEBUG);
					break;
				default:
					infoPnl.print("Invalid move.", MessageType.ERROR);
			}
		}
		
		if (gameplay.isMoved()) infoPnl.print("Move over.");
	}
	
	/**
	 * Check if the arguments of /move command is within bounds, i.e. 0-24.
	 * It ignores bar or homes, it only checks for pip indexes.
	 * 
	 * @param arg Argument of the /move command.
	 * @return boolean value indicating if the argument is out of bounds.
	 */
	private boolean isIndexOutOfBounds(String arg) {
		// arg can be strings ("white" or "black"), so we deal with that.
		boolean isOutOfBounds = false;
		if (isPip(arg)) {
			int pipNum = Integer.parseInt(arg);
			if (!(pipNum >= 0 && pipNum <= Settings.NUMBER_OF_POINTS)) {
				isOutOfBounds = true;
			}
		}
		return isOutOfBounds;
	}

	/**
	 * Command: /roll playerNumber
	 * 1 is the player with the perspective from the bottom, dices will be on the left.
	 * 2 is the player with the perspective from the top, dices will be on the right.
	 */
	public void runRollCommand(String[] args) {
		if (gameplay.isStarted()) {
			if (!gameplay.isRolled()) {
				infoPnl.print("Rolling...", MessageType.ANNOUNCEMENT);
				gameplay.roll();
			} else {
				infoPnl.print("Die has already been rolled.", MessageType.ERROR);
			}
		} else {
			PlayerPerspectiveFrom pov;
			if (args.length == 1) {
				pov = PlayerPerspectiveFrom.BOTTOM;
			} else {
				pov = parsePlayerPerspective(args[1]);
			}
			
			// rollDices returns null if playerNum is invalid.
			int[] res = game.getBoard().rollDices(pov);
			if (res != null) {
				infoPnl.print("Roll dice results: " + Arrays.toString(res));
			} else {
				infoPnl.print("Player number incorrect. It must be either 1 or 2.", MessageType.ERROR);
			}
		}
	}
	
	private PlayerPerspectiveFrom parsePlayerPerspective(String playerNum) {
		PlayerPerspectiveFrom pov = null;
		if (playerNum.equals("1")) {
			pov = PlayerPerspectiveFrom.BOTTOM;
		} else if (playerNum.equals("2")) {
			pov = PlayerPerspectiveFrom.TOP;
		} else {
			pov = PlayerPerspectiveFrom.NONE;
		}
		return pov;
	}

	/**
	 * Command: /start
	 * Rolls the dice to see which player goes first.
	 */
	public void runStartCommand() {
		if (!gameplay.isStarted()) {
			infoPnl.print("Starting game...", MessageType.ANNOUNCEMENT);
			gameplay.start();
		} else {
			infoPnl.print("Game already started.", MessageType.ERROR);
		}
	}
	
	/**
	 * Command: /next
	 * Move on to the next player's turn.
	 */
	public void runNextCommand() {
		// isMoved only if it started and rolled, so this is suffice.
		if (gameplay.isMoved()) {
			infoPnl.print("Swapping turns...", MessageType.ANNOUNCEMENT);
			Player pCurrent = gameplay.next();
			infoPnl.print("It is now " + pCurrent.getName() + "'s turn.", MessageType.ANNOUNCEMENT);
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
	 * Command: /quit
	 * Saves game log and prompts player to quit before quitting application.
	 */
	public void runQuitCommand() {
		stage.fireEvent(new WindowEvent(infoPnl.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	/**
	 * Command: /help
	 * Displays help commands on info panel.
	 */
	public void runHelpCommand() {
		String s = "\n";
		String line = null;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("help.txt"));
			while((line = reader.readLine()) != null) {
				s += line + "\n";
				System.out.println(line);
			}
			s +="\n";
			infoPnl.print(s);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Command: /name
	 * Changes player name.
	 */
	public void runNameCommand(String [] args) {
		int playerNum = Integer.parseInt(args[1]);
		String playerName = args[2];
		
		switch(playerNum) {
			case 1:
				game.getBottomPlayerPanel().setPlayerName(bottomPlayer, playerName);
				infoPnl.print("Player One is now " + "\"" + playerName + "\"");
				break;
			case 2:
				game.getTopPlayerPanel().setPlayerName(topPlayer, playerName);
				infoPnl.print("Player Two is now " + "\"" + playerName + "\"");
				break;
			default:
				infoPnl.print("Unable to change player name. Please try again", MessageType.ERROR);
		}
		
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
		traversalTl.setCycleCount(Settings.NUMBER_OF_POINTS);

		infoPnl.printNewline(2);
		hitTl.play();
	}
}
