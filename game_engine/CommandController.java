package game_engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import constants.GameConstants;
import constants.MessageType;
import constants.MoveResult;
import constants.PlayerPerspectiveFrom;
import game.DieResults;
import game.Pip;
import interfaces.ColorParser;
import interfaces.IndexOffset;
import interfaces.InputValidator;
import musicplayer.MusicPlayer;
import musicplayer.SoundEffectsPlayer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import ui.InfoPanel;

/**
 * This class handles all the commands that is entered by the user.
 * Sub-controller of MainController.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class CommandController implements ColorParser, InputValidator, IndexOffset {
	private Stage stage;
	private GameComponentsController game;
	private GameplayController gameplay;
	private InfoPanel infoPnl;
	private Player bottomPlayer, topPlayer;
	private MainController root;
	private MusicPlayer musicPlayer;
	private SoundEffectsPlayer soundFXPlayer;
	
	public CommandController(Stage stage, MainController root, GameComponentsController game,
			GameplayController gameplay, InfoPanel infoPnl, Player bottomPlayer, Player topPlayer, MusicPlayer musicPlayer) {
		this.bottomPlayer = bottomPlayer;
		this.topPlayer = topPlayer;
		this.stage = stage;
		this.root = root;
		this.game = game;
		this.gameplay = gameplay;
		this.infoPnl = infoPnl;
		this.musicPlayer = musicPlayer;
		soundFXPlayer = new SoundEffectsPlayer();
	}
	
	/**
	 * Parse the text variable and runs it as a command.
	 * @param text the string containing the command and its arguments.
	 * @param isPlayerInput, true (if 'text' is a user input), false (if some code uses this command) 
	 */
	public void runCommand(String text) {
		runCommand(text, false);
	}
	public void runCommand(String text, boolean isPlayerInput) {
		String[] args = text.split(" ");
		String command = args[0];
		
		if (command.equals("/move")) {
			runMoveCommand(args, isPlayerInput);
		} else if (command.equals("/roll")) {
			runRollCommand(args);
		} else if (command.equals("/start")) {
			runStartCommand();
		} else if (command.equals("/next")) {
			runNextCommand();
		} else if (command.equals("/help")) {
			runHelpCommand();
		} else if (command.equals("/name")) {
			runNameCommand(args);
		} else if (command.equals("/clear")) {
			runClearCommand();
		} else if (command.equals("/save")) {
			runSaveCommand();
		} else if (command.equals("/reset")) {
			runResetCommand();
		} else if (command.equals("/restart")) {
			runRestartCommand();
		} else if (command.equals("/quit")) {
			runQuitCommand();
		} else if (command.equals("/music")) {
			runMusicCommand(args);
		} else if (command.equals("/test")) {
			runTestCommand();
		} else if (command.equals("/cheat")) {
			runCheatCommand();
		} else {
			infoPnl.print("Unknown Command.", MessageType.ERROR);
		}
	}
	
	/**
	 * Command: /move fromPip toPip			//both numbers
	 * Command: /move fromBar toPip			//left is a color, right a number
	 * Command: /move fromPip/bar toHome	//left is a color or number, right is a color.
	 * where fromPip and toPip will be one-index number based.
	 * where fromBar is the bar color.
	 * where toHome is the home color.
	 */
	public void runMoveCommand(String[] args, boolean isPlayerInput) {
		// error checking.
		if (args.length != 3) {
			infoPnl.print("Incorrect syntax: expected /move fro to.", MessageType.ERROR);
			return;
		}
		
		// conversion from one-based index to zero-based.
		String fro, to;
		if (isPlayerInput) {
			fro = getZeroBasedIndex(args[1]);
			to = getZeroBasedIndex(args[2]);
		} else {
			fro = args[1];
			to = args[2];
		}
		
		// handle out of bounds input.
		if (isIndexOutOfBounds(fro) || isIndexOutOfBounds(to)) {
			infoPnl.print("Invalid range, must be between 1-" + GameConstants.NUMBER_OF_PIPS + ".", MessageType.ERROR);
			return;
		}

		// if it is the top player, then the player's perspective of the
		// pip number label will be different from code's as code's is
		// based on bottom player's, so we adjust accordingly.
		if (isPlayerInput && gameplay.isTopPlayer() && gameplay.isStarted()) {
			fro = getTopPlayerOffset(fro);
			to = getTopPlayerOffset(to);
		}
		
		// validate moves.
		// isRolled only if it started.
		if (gameplay.isRolled()) {
			if (!gameplay.isMoved()) {
				game.unhighlightAll();
				if (gameplay.isValidMove(fro, to)) {
					infoPnl.print("Moving...", MessageType.ANNOUNCEMENT);
				} else {
					game.getBoard().highlightFromPipsAndFromBarChecker(gameplay.getValidMoves());
					infoPnl.print("You can only move highlighted checkers to highlighted pips.", MessageType.ERROR);
					return;
				}
			} else {
				infoPnl.print("You have made your move.", MessageType.ERROR);
				return;
			}
		}
		
		MoveResult moveResult;
		// move from pip/bar to home.
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
					infoPnl.print("Moved checker from " + correct(Integer.parseInt(fro)) + " to home.");
					break;
				case MOVED_TO_HOME_FROM_BAR:
					infoPnl.print("Moved checker from bar to home.");
					break;
				case PIP_EMPTY:
					infoPnl.print("Starting pip has no checkers.", MessageType.ERROR);
					break;
				default:
					infoPnl.print("Invalid move.", MessageType.ERROR);
			}
		// move from bar to pip.
		} else if (fro.equals("white") || fro.equals("black")) {
			Color fromBar = parseColor(fro);
			int toPip = Integer.parseInt(to);
			
			moveResult = game.moveFromBar(fromBar, toPip);
			switch (moveResult) {
				case MOVED_FROM_BAR:
					infoPnl.print("Moved checker from bar to " + correct(toPip) + ".");
					break;
				case MOVE_TO_BAR:
					game.moveToBar(toPip);
					game.moveFromBar(fromBar, toPip);
					infoPnl.print("Moved checker from " + correct(toPip) + " to bar.");
					infoPnl.print("Moved checker from bar to " + correct(toPip) + ".");
					break;
				case PIP_EMPTY:
					infoPnl.print("Starting pip has no checkers.", MessageType.ERROR);
					break;
				default:
					infoPnl.print("Invalid move.", MessageType.ERROR);
			}
		// move from pip to pip.
		} else {
			int fromPip = Integer.parseInt(fro);
			int toPip = Integer.parseInt(to);
			
			moveResult = game.getBoard().moveCheckers(fromPip, toPip);
			switch (moveResult) {
				case MOVED_TO_PIP:
					infoPnl.print("Moved checker from " + correct(fromPip) + " to " + correct(toPip) + ".");
					break;
				case MOVE_TO_BAR:
					game.moveToBar(toPip);
					game.getBoard().moveCheckers(fromPip, toPip);
					infoPnl.print("Moved checker from " + correct(toPip) + " to bar.");
					infoPnl.print("Moved checker from " + correct(fromPip) + " to " + correct(toPip) + ".");
					break;
				case PIP_EMPTY:
					infoPnl.print("Starting pip has no checkers.", MessageType.ERROR);
					break;
				default:
					infoPnl.print("Invalid move.", MessageType.ERROR);
			}
		}
		
		if (gameplay.isRolled() && !gameplay.isMoved()) {
			gameplay.move();
		}
		if (gameplay.isMoved()) infoPnl.print("Move over.");
		gameplay.unhighlightPips();
		soundFXPlayer.playCheckerSound();
	}
	
	public String correct(int pipNum) {
		return gameplay.correct(pipNum);
	}
	
	/**
	 * Checks if the arguments of /move command is within bounds, i.e. 0-24.
	 * It ignores bar or homes, it only checks for pip indexes.
	 * @param arg Argument of /move command.
	 * @return boolean value indicating if the argument is out of bounds.
	 */
	private boolean isIndexOutOfBounds(String arg) {
		// arg can be strings ("white" or "black"), so we deal with that.
		boolean isOutOfBounds = false;
		if (isPip(arg)) {
			int pipNum = Integer.parseInt(arg);
			if (!game.getBoard().isPipNumberInRange(pipNum)) isOutOfBounds = true;
		}
		return isOutOfBounds;
	}

	/**
	 * Gameplay mode:
	 * Command: /roll
	 * 
	 * Free for all mode:
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
			DieResults res = game.getBoard().rollDices(pov);
			if (res != null) {
				infoPnl.print("Roll dice results: " + res);
			} else {
				infoPnl.print("Player number incorrect. It must be either 1 or 2.", MessageType.ERROR);
			}
		}
	}
	
	/**
	 * Returns player perspective based on player number.
	 * 1 is the player with the perspective from the bottom, dices will be on the left.
	 * 2 is the player with the perspective from the top, dices will be on the right.
	 * @param playerNum player number.
	 * @return player's perspective.
	 */
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
	 * Enters into gameplay mode, i.e. start game.
	 */
	public void runStartCommand() {
		if (!gameplay.isStarted()) {
			infoPnl.print("Starting game...", MessageType.ANNOUNCEMENT);
			root.restartGame();
		} else {
			infoPnl.print("Game already started.", MessageType.ERROR);
		}
	}
	
	/**
	 * Command: /next
	 * Move on to the next player's turn.
	 */
	public void runNextCommand() {
		if (gameplay.isStarted()) {
			if (gameplay.isMoved()) {
				infoPnl.print("Swapping turns...", MessageType.ANNOUNCEMENT);
				Player pOpponent = gameplay.getOpponent();
				infoPnl.print("It is now " + pOpponent.getName() + "'s (" + parseColor(pOpponent.getColor()) + ") move.");
				gameplay.next();
			} else {
				infoPnl.print("Allowed to swap turns only when you are done making your moves.", MessageType.ERROR);
			}
		} else {
			infoPnl.print("Game not started. Enter \"/start\" to start the game.", MessageType.ERROR);
		}
	}
	
	/**
	 * Command: /help
	 * Displays help commands on info panel.
	 */
	public void runHelpCommand() {
		String s = "\n";
		String line = null;
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("help.txt")));
			while((line = reader.readLine()) != null)
				s += line + "\n";
			s +="\n";
			infoPnl.print(s, MessageType.ANNOUNCEMENT);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Command: /name colorPlayerRepresents newName
	 * Changes player name.
	 */
	public void runNameCommand(String[] args) {
		if (args.length != 3) {
			infoPnl.print("Incorrect syntax: expected /name color newName.", MessageType.ERROR);
			return;
		}
		
		String color = args[1].toLowerCase();
		String playerName = args[2];
		if (color.equals("white")) {
			game.getBottomPlayerPanel().setPlayerName(bottomPlayer, playerName);
			infoPnl.print("Player with white checkers is now " + "\"" + playerName + "\".");
		} else if (color.equals("black")) {
			game.getTopPlayerPanel().setPlayerName(topPlayer, playerName);
			infoPnl.print("Player with black checkers is now " + "\"" + playerName + "\".");
		} else {
			infoPnl.print("Incorrect syntax: expected white or black color in /name color newName.", MessageType.ERROR);
		}
	}
	
	/**
	 * Command: /clear
	 * Appends newlines to infoPnl to "clear" it out.
	 */
	public void runClearCommand() {
		infoPnl.print("Clearing panel...");
		infoPnl.clear();
	}
	
	/**
	 * Command: /save
	 * Saves game log (text on info panel) to text file.
	 */
	public void runSaveCommand() {
		infoPnl.saveToFile();
	}
	
	/**
	 * Command: /reset
	 * Resets everything including player info.
	 */
	public void runResetCommand() {
		root.resetApplication();
	}	
	
	/**
	 * Command: /restart
	 * Starts a new instance of the game.
	 */
	public void runRestartCommand() {
		root.restartGame();
	}
	
	/**
	 * Command: /quit
	 * Saves game log and prompts player to quit before quitting application.
	 */
	public void runQuitCommand() {
		stage.fireEvent(new WindowEvent(infoPnl.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	private void runMusicCommand(String[] args) {
		if (args.length != 2) {
			infoPnl.print("Incorrect Syntax: Expected /music [play | next | prev | pause | stop | repeat | mute | unmute]", MessageType.ERROR);
			return;
		}
		
		if (args[1].equals("random"))
			musicPlayer.random();
		else if (args[1].equals("play"))
			musicPlayer.play();
		else if (args[1].equals("next"))
			musicPlayer.next();
		else if (args[1].equals("prev"))
			musicPlayer.prev();
		else if (args[1].equals("pause"))
			musicPlayer.pause();
		else if (args[1].equals("stop"))
			musicPlayer.stop();
		else if (args[1].equals("repeat"))
			musicPlayer.repeat();
		else if (args[1].equals("mute"))
			musicPlayer.muteVolume(true);
		else if (args[1].equals("unmute")) 
			musicPlayer.muteVolume(false);
		else
			infoPnl.print("Invalid command for /music", MessageType.ERROR);
		
		infoPnl.print(musicPlayer.getStatus(args[1]));
	}
	
	/**
	 * Command: /cheat
	 * Reorganizes the checkers at the checkersStorer based on assignment specification.
	 */
	public void runCheatCommand() {
		game.reset();
		initCheatCheckers();
		if (gameplay.isStarted()) gameplay.recalculateMoves();
		infoPnl.print("Cheat command ran.");
	}
	
	/**
	 * Initialize checkers for cheat (testing purposes)
	 */
	private void initCheatCheckers() {
		// Add checkers to pips.
		// Note that numbers start from bottom internally
		Pip[] pips = game.getBoard().getPips();		
		for (int i = 0; i < pips.length; i++) {
			switch (i) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					pips[i].initCheckers(2, Settings.getBottomPerspectiveColor());
					break;	
				case 23:
				case 21:
				case 20:
					pips[i].initCheckers(3, Settings.getTopPerspectiveColor());
					break;
			}
		}
		
		// Add checkers to homes.
		game.getMainHome().getHome(Settings.getTopPerspectiveColor()).initCheckers(3, Settings.getTopPerspectiveColor());
		game.getMainHome().getHome(Settings.getBottomPerspectiveColor()).initCheckers(2, Settings.getBottomPerspectiveColor());
		
		// Add checkers to bars.
		game.getBars().getBar(Settings.getTopPerspectiveColor()).initCheckers(3, Settings.getTopPerspectiveColor());
		game.getBars().getBar(Settings.getBottomPerspectiveColor()).initCheckers(3, Settings.getBottomPerspectiveColor());
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
					runCommand("/move 1 2", true);
					break;
				case 2:
					runCommand("/move 6 2", true);
					break;
				default:
					infoPnl.print("Hit testing done.");
					infoPnl.printNewlines(2);
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
					runCommand("/move black 2", true);
					break;
				default:
					infoPnl.print("Bear-on testing done.");
					infoPnl.printNewlines(2);
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
					runCommand("/move 6 white", true);
					break;
				default:
					infoPnl.print("Bear-off testing done.");
					infoPnl.printNewlines(2);
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
				infoPnl.printNewlines(2);
				return;
			}
			runCommand("/move " + checkerPos + " " + (checkerPos-1), true);
			checkerPos--;
		}));
		hitTl.setCycleCount(3);
		bearOnTL.setCycleCount(2);
		bearOffTL.setCycleCount(2);
		traversalTl.setCycleCount(GameConstants.NUMBER_OF_PIPS);

		infoPnl.printNewlines(2);
		hitTl.play();
	}
	
	public void reset() {
		checkerPos = 24;
		step = 1;
	}
}
