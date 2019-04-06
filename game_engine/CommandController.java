package game_engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import constants.GameConstants;
import constants.MessageType;
import constants.MoveResult;
import constants.PlayerPerspectiveFrom;
import game.DieResults;
import game.DoublingCube;
import game.DoublingCubeHome;
import game.Home;
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
import ui.CommandPanel;
import ui.InfoPanel;

/**
 * This class handles all the commands that is entered by the user.
 * Sub-controller of MainController.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class CommandController implements ColorParser, InputValidator, IndexOffset {
	private Stage stage;
	private GameComponentsController game;
	private GameplayController gameplay;
	private EventController event;
	private InfoPanel infoPnl;
	private CommandPanel cmdPnl;
	private Player bottomPlayer, topPlayer;
	private MatchController root;
	private MusicPlayer musicPlayer;
	private SoundEffectsPlayer soundFXPlayer;
	
	public CommandController(Stage stage, MatchController root, GameComponentsController game,
			GameplayController gameplay, InfoPanel infoPnl, CommandPanel cmdPnl, Player bottomPlayer, Player topPlayer, MusicPlayer musicPlayer) {
		this.bottomPlayer = bottomPlayer;
		this.topPlayer = topPlayer;
		this.stage = stage;
		this.root = root;
		this.game = game;
		this.gameplay = gameplay;
		this.infoPnl = infoPnl;
		this.cmdPnl = cmdPnl;
		this.musicPlayer = musicPlayer;
		soundFXPlayer = new SoundEffectsPlayer();
	}
	public void setEventController(EventController event) {
		this.event = event;
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
		String command = args[0].toLowerCase();
		
		if (command.equals("/move")) {
			runMoveCommand(args, isPlayerInput);
		} else if (command.equals("/movecube")) {
			runMoveCubeCommand(args);
		} else if (command.equals("/roll")) {
			runRollCommand(args);
		} else if (command.equals("/start")) {
			runStartCommand();
		} else if (command.equals("/double")) {
			runDoubleCommand();
		} else if (command.equals("/accept")) {
			runDoubleAcceptCommand();
		} else if (command.equals("/decline")) {
			runDoubleDeclineCommand();
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
		/*
		} else if (command.equals("/light")) {
			Settings.useLightTheme();
			infoPnl.redraw();
			cmdPnl.redraw();
		*/
		} else if (command.equals("/dark")) {
			Settings.useDarkTheme();
			infoPnl.redraw();
			cmdPnl.redraw();
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
	private void runMoveCommand(String[] args, boolean isPlayerInput) {
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
		if (gameplay.isStarted()) {
			if (gameplay.isRolled()) {
				if (!gameplay.isMoved()) {
					game.unhighlightAll();
					if (gameplay.getGameplayMoves().isValidMove(fro, to)) {
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
			} else {
				infoPnl.print("You must roll the dice to move.", MessageType.ERROR);
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
					soundFXPlayer.playBearOffSound();
					infoPnl.print("Moved checker from " + correct(Integer.parseInt(fro)) + " to home.");
					break;
				case MOVED_TO_HOME_FROM_BAR:
					soundFXPlayer.playBearOffSound();
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
					soundFXPlayer.playBearOnSound();
					infoPnl.print("Moved checker from bar to " + correct(toPip) + ".");
					break;
				case MOVE_TO_BAR:
					game.moveToBar(toPip);
					if (gameplay.isStarted()) game.getEmojiOfPlayer(gameplay.getCurrent().getColor()).setHitFace();
					soundFXPlayer.playCheckerHitSound();
					game.moveFromBar(fromBar, toPip);
					soundFXPlayer.playBearOnSound();
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
					soundFXPlayer.playCheckerSound();
					infoPnl.print("Moved checker from " + correct(fromPip) + " to " + correct(toPip) + ".");
					break;
				case MOVE_TO_BAR:
					game.moveToBar(toPip);
					if (gameplay.isStarted()) game.getEmojiOfPlayer(gameplay.getCurrent().getColor()).setHitFace();
					soundFXPlayer.playCheckerHitSound();
					game.getBoard().moveCheckers(fromPip, toPip);
					soundFXPlayer.playCheckerSound();
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
		gameplay.unhighlightPips();
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
	private void runRollCommand(String[] args) {
		if (gameplay.isStarted()) {
			if (gameplay.isInTransition()) {
				handleInTransitionError();
				return;
			} else if (gameplay.isDoubling()) {
				handleDoublingError();
				return;
			}
			
			if (!gameplay.isRolled()) {
				infoPnl.print("Rolling...", MessageType.ANNOUNCEMENT);
				gameplay.roll();
				event.resetSelections();
				soundFXPlayer.playDiceSound();
			} else {
				infoPnl.print("Die has already been rolled.", MessageType.ERROR);
			}
		} else {
			event.resetSelections();
			
			PlayerPerspectiveFrom pov;
			if (args.length == 1) {
				pov = PlayerPerspectiveFrom.BOTTOM;
			} else {
				pov = parsePlayerPerspective(args[1]);
			}
			
			// check if cube on board.
			// if so, move it back to cube's box.
			if (game.getBoard().isCubeInBoard()) {
				runCommand("/movecube " + parseColor(game.getBoard().getHomeCubeIsIn().getColor()) + " box");
			}
			
			// rollDices returns null if playerNum is invalid.
			DieResults res = game.getBoard().rollDices(pov);
			if (res != null) {
				infoPnl.print("Roll dice results: " + res);
				soundFXPlayer.playDiceSound();
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
	private void runStartCommand() {
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
	private void runNextCommand() {
		if (gameplay.isStarted()) {
			if (gameplay.isMoved()) {
				gameplay.next();
			} else {
				infoPnl.print("Allowed to swap turns only when you are done making your moves.", MessageType.ERROR);
			}
		} else {
			infoPnl.print("Game not started. Enter \"/start\" to start the game.", MessageType.ERROR);
		}
	}
	
	/**
	 * Command: /double
	 * Throws the doubling cube on the board,
	 * Opponent player can choose to accept or decline.
	 */
	private void runDoubleCommand() {
		if (gameplay.isInTransition()) {
			handleInTransitionError();
			return;
		} else if (gameplay.isDoubling()) {
			handleDoublingError();
			return;
		}
		
		// allowed to double if started, but not rolled.
		if (gameplay.isStarted()) {
			if (!gameplay.isRolled()) {
				if (!gameplay.isMaxDoubling()) {
					if (!root.isCrawfordGame()) {
						if (!gameplay.isCurrentPlayerScoreCapped()) {
							// move cube from its box to player's board.
							if (!game.getCubeHome().isEmpty()) {
								game.getCube().setUsed(true);
								runCommand("/movecube box " + parseColor(gameplay.getCurrent().getColor()));
							// move cube from player's home to player's board.
							} else if (gameplay.getCurrent().hasCube()) {
								String pColorString = parseColor(gameplay.getCurrent().getColor());
								runCommand("/movecube " + pColorString + " " + pColorString);
								DoublingCube cube = game.getCube();
								cube.doubleDoublingCube();
								gameplay.setIsMaxDoubling(cube.isMaxDoubling());
							} else {
								infoPnl.print("Unable to propose doubling the stakes, you do not possess the doubling cube.", MessageType.ERROR);
								return;
							}
							gameplay.doubling();
							
							// swap turns.
							infoPnl.print(gameplay.getCurrent().getName() + " has proposed doubling the stakes.", MessageType.ANNOUNCEMENT);
							gameplay.nextFunction();
			
							// Opponent player can either choose to accept or decline doubling cube.
							infoPnl.print("Your mission " + gameplay.getCurrent().getName() + ", should you choose to accept it. Is to accept this doubling cube and defeat your enemies.");
							infoPnl.print("Enter yes/no");
						} else {
							infoPnl.print("Unable to propose doubling the stakes, you have no reason to propose.", MessageType.ERROR);
						}
					} else {
						infoPnl.print("Unable to propose doubling the stakes, in the middle of a Crawford game.", MessageType.ERROR);
					}
				} else {
					infoPnl.print("Unable to propose doubling the stakes, stakes is maxed.", MessageType.ERROR);
				}
			} else {
				infoPnl.print("Doubling allowed only before rolling.", MessageType.ERROR);
			}
		} else {
			infoPnl.print("Game not started. Enter \"/start\" to start the game.", MessageType.ERROR);
		}
	}
	
	private void handleDoublingError() {
		infoPnl.print("Game will not proceed until you accept or decline the doubling cube.", MessageType.ERROR);
	}
	
	public void handleInTransitionError() {
		infoPnl.print("Hold on cowboy, wait out the delay.", MessageType.ERROR);
	}
	
	/**
	 * Command: /accept
	 * Swap turns back to proposer.
	 * Move doubling cube from proposer's half board to opponent's home.
	 * Roll for the proposer.
	 */
	private void runDoubleAcceptCommand() {
		// checks are made to /accept beforehand.
		if (gameplay.isDoubling()) {
			infoPnl.print("Doubling cube accepted, game continues.", MessageType.ANNOUNCEMENT);
			gameplay.stopCurrentPlayerTimer();
			gameplay.doubling();
			runCommand("/movecube " + parseColor(gameplay.getOpponent().getColor()) + " " + parseColor(gameplay.getCurrent().getColor()));
			gameplay.getCurrent().setHasCube(true);
			gameplay.getOpponent().setHasCube(false);
			gameplay.nextFunction();
			if (Settings.ENABLE_AUTO_ROLL) gameplay.roll();
			else game.getCube().setNormalImage();
			
			// check if dead cube.
			// dead cube if currentPlayer.score + 1*cubeMultiplier >= totalGames.
			if (!gameplay.isMaxDoubling() && gameplay.isCurrentPlayerScoreCapped()) {
				infoPnl.print("Dead cube in play.", MessageType.DEBUG);
				gameplay.setIsMaxDoubling(true);
			}
		}
	}
	
	/**
	 * Command: /decline
	 * Places doubling cube back into its box.
	 * Allocates score to the proposer.
	 * Restarts the game. 
	 */
	private void runDoubleDeclineCommand() {
		// checks are made to /decline beforehand.
		if (gameplay.isDoubling()) {
			infoPnl.print("Doubling cube declined.", MessageType.ANNOUNCEMENT);
			game.getCube().setNormalImage();
			runCommand("/movecube " + parseColor(gameplay.getOpponent().getColor()) + " box");
			gameplay.handleGameOver(true);
		}
	}
	
	// used internally to move the doubling cube around.
	private void runMoveCubeCommand(String[] args) {
		// error checking.
		if (args.length != 3) {
			infoPnl.print("Incorrect syntax: expected /movecube fro to.", MessageType.ERROR);
			return;
		}
		
		String fro = args[1];
		String to = args[2];

		if (equalGameColors(fro)) {
			if (game.getBoard().isCubeInBoard()) {
				DoublingCubeHome fromCubeHome = game.getBoard().getCubeHomeOfPlayer(parseColor(fro));
				DoublingCube cube = fromCubeHome.popCube();
				// move cube from board to box.
				if (to.equals("box")) {
					DoublingCubeHome toCubeHome = game.getCubeHome();
					toCubeHome.addThisCube(cube);
					cube.resetRotation();
					infoPnl.print("(Cube Move) Is valid board to box.", MessageType.DEBUG);
					soundFXPlayer.playCheckerSound();
				// move cube from board to player's home.
				} else if (equalGameColors(to)) {
					Home toHome = game.getOtherHome().getHome(parseColor(to));
					toHome.addThisCube(cube);
					cube.resetRotation();
					infoPnl.print("(Cube Move) Is valid board to player's home.", MessageType.DEBUG);
					soundFXPlayer.playCheckerSound();
				}
			} else if (game.isCubeInHome()) {
				Home fromHome = game.getOtherHome().getHome(parseColor(fro));
				DoublingCube cube = fromHome.popCube();
				// move cube from player's home to board.
				if (equalGameColors(to)) {
					DoublingCubeHome toCubeHome = game.getBoard().getCubeHomeOfPlayer(parseColor(to));
					toCubeHome.addThisCube(cube);
					cube.rotateOnBoard();
					game.getBoard().drawCubeHome();
					infoPnl.print("(Cube Move) Is valid player's home to board.", MessageType.DEBUG);
					soundFXPlayer.playCheckerSound();
				// NOT TESTED SINCE NOT IN USE.
				// move cube from player's home to box.
				} else if (to.equals("box")) {
					DoublingCubeHome toCubeHome = game.getCubeHome();
					toCubeHome.addThisCube(cube);
					cube.resetRotation();
					infoPnl.print("(Cube Move) Is valid player's home to box", MessageType.DEBUG);
					soundFXPlayer.playCheckerSound();
				}
			}
		} else if (fro.equals("box")) {
			DoublingCubeHome fromCubeHome = game.getCubeHome();
			if (!fromCubeHome.isEmpty()) {
				DoublingCube cube = fromCubeHome.popCube();
				// move cube from box to board.
				if (equalGameColors(to)) {
					DoublingCubeHome toCubeHome = game.getBoard().getCubeHomeOfPlayer(parseColor(to));
					toCubeHome.addThisCube(cube);
					cube.rotateOnBoard();
					game.getBoard().drawCubeHome();
					infoPnl.print("(Cube Move) Is valid box to board.", MessageType.DEBUG);
					soundFXPlayer.playCheckerSound();
				}
				// NOT WRITTEN SINCE NOT IN USE,
				// plus finding a hard time to differentiate board/player's home's Color.
				// move cube from box to player's home.
			}
		}
	}
	
	private boolean equalGameColors(String colorString) {
		return colorString.equals(parseColor(Settings.getBottomPerspectiveColor())) || colorString.equals(parseColor(Settings.getTopPerspectiveColor()));
	}
	
	/**
	 * Command: /help
	 * Displays help commands on info panel.
	 */
	private void runHelpCommand() {
		String s = "\n";
		String line = null;
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("help.txt")));
			while((line = reader.readLine()) != null)
				s += line + "\n";
			s +="\n";
			infoPnl.print(s);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Command: /name colorPlayerRepresents newName
	 * Changes player name.
	 */
	private void runNameCommand(String[] args) {
		if (args.length < 3) {
			infoPnl.print("Incorrect syntax: expected /name color newName.", MessageType.ERROR);
			return;
		}
		String color = args[1].toLowerCase();
		String playerName = getFullName(args);
		if (parseColor(color).equals(Settings.getTopPerspectiveColor())) {
			game.getPlayerPanel(parseColor(color)).setPlayerName(topPlayer, playerName);
			infoPnl.print("Player with " + parseColor(topPlayer.getColor()) + " checkers is now " + "\"" + playerName + "\".");
		} else if (parseColor(color).equals(Settings.getBottomPerspectiveColor())) {
			game.getPlayerPanel(parseColor(color)).setPlayerName(bottomPlayer, playerName);
			infoPnl.print("Player with " + parseColor(bottomPlayer.getColor()) + " checkers is now " + "\"" + playerName + "\".");
		} else {
			infoPnl.print("Incorrect syntax: expected white or black color in /name color newName.", MessageType.ERROR);
		}
	}
	private String getFullName(String[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 2; i < args.length; i++) {
			sb.append(args[i]);
			
			if (i != args.length-1)
				sb.append(" ");
		}
		return sb.toString();
	}
	
	/**
	 * Command: /clear
	 * Appends newlines to infoPnl to "clear" it out.
	 */
	private void runClearCommand() {
		infoPnl.print("Clearing panel...");
		infoPnl.clear();
	}
	
	/**
	 * Command: /save
	 * Saves game log (text on info panel) to text file.
	 */
	public void runSaveCommand() {
		if (infoPnl.saveToFile());
			infoPnl.print("Game log saved to log.txt");
	}
	
	/**
	 * Command: /reset
	 * Resets everything including player info.
	 */
	private void runResetCommand() {
		root.resetApplication();
	}	
	
	/**
	 * Command: /restart
	 * Starts a new instance of the game.
	 */
	private void runRestartCommand() {
		root.restartGame();
	}
	
	/**
	 * Command: /quit
	 * Saves game log and prompts player to quit before quitting application.
	 */
	private void runQuitCommand() {
		stage.fireEvent(new WindowEvent(infoPnl.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	private void runMusicCommand(String[] args) {
		if (args.length != 2) {
			infoPnl.print("Incorrect Syntax: Expected /music [play | next | prev | pause | stop | repeat | mute | unmute]", MessageType.ERROR);
			return;
		}
		
		args[1] = args[1].toLowerCase();
		
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
	private void runCheatCommand() {
		game.removeCheckers();
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
					pips[i].initCheckers(2, Settings.getBottomPerspectiveColor());
					break;	
				case 23:
					pips[i].initCheckers(2, Settings.getTopPerspectiveColor());
					break;
			}
		}
		
		// Add checkers to homes.
		game.getMainHome().getHome(Settings.getTopPerspectiveColor()).initCheckers(13, Settings.getTopPerspectiveColor());
		game.getMainHome().getHome(Settings.getBottomPerspectiveColor()).initCheckers(13, Settings.getBottomPerspectiveColor());
	}
	
	/**
	 * Command: /test
	 * Tests /move command, by moving checkers from 1-24, to hit, to bear off and bear on.
	 */
	private int checkerPos = 24;
	private int step = 1;
	private Timeline hitTl, bearOnTL, bearOffTL, traversalTl;
	private void runTestCommand() {
		if (!gameplay.isStarted()) {
			game.reset();
			
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
		} else {
			infoPnl.print("Cannot test when game is started.", MessageType.ERROR);
		}
	}
	
	public void reset() {
		checkerPos = 24;
		step = 1;
	}
}
