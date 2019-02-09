package game_engine;

import java.util.Arrays;
import constants.MessageType;
import java.util.Optional;

import constants.MoveResult;
import events.CheckersStorerHandler;
import events.CheckersStorerSelectedEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * This class represents the entire component of the application,
 * consisting of the game components and the UI components.
 * 
 * These components are children of this class, therefore
 * this class is the root in the layout structure/tree.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class MainController extends GridPane {
	private GameController game;
	private InfoPanel infoPnl;
	private RollDieButton rollDieBtn;
	private CommandPanel cmdPnl;
	
	/**
	 * Default Constructor
	 * 		- Initialize all the instance variables.
	 * 		- Initialize instance variables layouts.
	 * 		- Initialize instance variables listeners.
	 * 		- Initialize game components listeners (i.e. points, for now).
	 */
	public MainController() {
		super();
		game = new GameController();
		infoPnl = new InfoPanel();
		rollDieBtn = new RollDieButton();
		cmdPnl = new CommandPanel();
		style();
		initLayout();
		initUIListeners();
		initGameListeners();
	}
	
	/**
	 * Style MainController (i.e. root).
	 */
	public void style() {
		setStyle("-fx-font-size: 14px; -fx-font-family: 'Consolas';");
		setPadding(new Insets(10));
		setVgap(Settings.getUIVGap());
		setHgap(5);
		setAlignment(Pos.CENTER);
		setMaxSize(Settings.getScreenSize().getWidth(), Settings.getScreenSize().getHeight());
	}

	/**
	 * Manages the layout of the children, then adds them as the child of MainController (i.e. root).
	 */
	public void initLayout() {
		VBox terminal = new VBox();
		terminal.getChildren().addAll(infoPnl, cmdPnl);
		terminal.setAlignment(Pos.CENTER);
		
		add(game, 0, 0, 1, 3);
		add(terminal, 1, 0);
		add(rollDieBtn, 1, 2);
	}
	
	/**
	 * Manages game listeners.
	 */
	private void initGameListeners() {
		// Exit point selection mode when any part of the game board is clicked.
		game.setOnMouseClicked((MouseEvent event) -> {
			game.unhighlightPoints();
			isPointSelectionMode = false;
			isBarSelectionMode = false;
		});

		initCheckersStorersListeners();
	}
	
	/**
	 * Manages checkers storer listeners.
	 */
	private boolean isPointSelectionMode = false;
	private boolean isBarSelectionMode = false;
	private CheckersStorer storerSelected;
	private void initCheckersStorersListeners() {
		addEventHandler(CheckersStorerSelectedEvent.STORER_SELECTED, new CheckersStorerHandler() {
			@Override	
			public void onClicked(CheckersStorer object) {
				// point selected, basis for fromPip or toPip selection.
				if (object instanceof Point) {
					// neither point nor bar selected, basis for fromPip selection.
					if (!isPointSelectionMode && !isBarSelectionMode) {
						storerSelected = object;
						int fromPip = ((Point) storerSelected).getPointNumber() + 1;
						game.highlightPoints(fromPip-1);
						isPointSelectionMode = true;
						infoPnl.print("Point clicked is: " + fromPip + ".");
					// either point or bar selected, basis for toPip selection.
					} else {
						// prevent moving checkers from point to bar.
						// i.e select point, to bar.
						int toPip = ((Point) object).getPointNumber() + 1;
						
						if (isPointSelectionMode) {
							int fromPip = ((Point) storerSelected).getPointNumber() + 1;
							runCommand("/move " + fromPip + " " + toPip);
						} else if (isBarSelectionMode) {
							String fromBar = ((Bar) storerSelected).getColour();
							runCommand("/move " + fromBar + " " + toPip);
						}
						game.unhighlightPoints();
						isPointSelectionMode = false;
						isBarSelectionMode = false;
					}
				// bar selected, basis for fromBar selection.
				} else if (object instanceof Bar) {
					// prevent entering into both point and bar selection mode.
					if (!isPointSelectionMode) {
						storerSelected = object;
						game.highlightPoints(-1);
						isBarSelectionMode = true;
						infoPnl.print("Bar clicked.");
					}
				// home selected, basis for toHome selection.
				} else if (object instanceof Home) {
					if (isPointSelectionMode || isBarSelectionMode) {
						String toHome = ((Home) object).getColour();
						
						if (isPointSelectionMode) {
							int fromPip = ((Point) storerSelected).getPointNumber() + 1;
							runCommand("/move " + fromPip + " " + toHome);
						} else if (isBarSelectionMode) {
							String fromBar = ((Bar) storerSelected).getColour();
							runCommand("/move " + fromBar + " " + toHome);
						}
						game.unhighlightPoints();
						isPointSelectionMode = false;
						isBarSelectionMode = false;
					}
					infoPnl.print("Home clicked.");
				} else {
					infoPnl.print("Other instances of checkersStorer were clicked.");
				}
			}
		});
	}
	
	/**
	 * Manages all the UI (infoPnl, cmdPnl, rollDieBtn) listeners.
	 */
	private void initUIListeners() {
		initCommandPanelListener();
		initRollDieButtonListener();
		
		Main.getStage().setOnCloseRequest(onExitCheck);
	}

	/**
	 * Manages command panel listeners.
	 * 		- echoes player input to infoPanel.
	 * 		- does not echo empty strings/whitespace.
	 */
	private void initCommandPanelListener() {
		cmdPnl.setOnAction((ActionEvent event) -> {
			String text = cmdPnl.getText();
			
			if (text.startsWith("/")) {
				runCommand(cmdPnl.getText());
			} else if (text.equals("")) {
				// ignores if user types nothing.
			} else if (text.equals("quit")) {
				runCommand("/quit");
			} else if (text.equals("save")) {
				runCommand("/save");
			} else if (text.trim().isEmpty()) {
				// ignores if string empty or whitespace only.
			} else {
				// player chat, need to implement players to differentiate which player is which.
				// in the meantime, just add text to info panel.
				infoPnl.print(text, MessageType.CHAT);
			}
			cmdPnl.setText("");
			
			/*
			 * TODO add text to a txt file containing the history of commands entered.
			 * the up and down arrow should allow the user to navigate between commands.
			 * upon typing up or down, set the cmdPnl with the commands.
			 */
		});
	}
	
	/**
	 * Initialize roll die button listeners.
	 * 
	 * TODO remove the dieState variable, it is used to show that the roll dice button works
	 * and dices can be drawn to either side of the board.
	 * 
	 * When turns and players are implemented, then elaborate on this (i.e. display the
	 * dices on the side where it's the player's roll). 
	 */
	private int dieState = 2;
	private void initRollDieButtonListener() {
		rollDieBtn.setOnAction((ActionEvent event) -> {
			if (dieState == 1) {
				dieState = 2;
			} else {
				dieState = 1;
			}
			runCommand("/roll " + new Integer(dieState).toString());
		});
	}
	
	/**
	 * Parse the text variable and runs it as a command.
	 * 
	 * @param text the string containing the command and its arguments.
	 */
	private void runCommand(String text) {
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
	private void runMoveCommand(String[] args) {
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
				String fromBar = fro;
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
			String fromBar = fro;
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
	private void runRollCommand(String[] args) {
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
	private void runSaveCommand() {
		infoPnl.saveToFile();
	}
	
	/**
	 * Command: /test
	 * Tests /move command, by moving checkers from 1-24, to hit, to bear off and bear on.
	 */
	private int checkerPos = 24;
	private int step = 1;
	private Timeline hitTl, bearOnTL, bearOffTL, traversalTl;
	private void runTestCommand() {
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
	private void runQuitCommand() {
		infoPnl.saveToFile();
		//infoPnl.print("Trying to quit game. Game log autosaved as \"backgammon.txt\".");
		infoPnl.print("Trying to quit game.");
		Main.getStage().fireEvent(new WindowEvent(infoPnl.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	/**
	 * Checks if player really wants to exit game prevents accidental exits
	 */
	@SuppressWarnings("unused")
	private EventHandler<WindowEvent> onExitCheck = event -> {
		// Alert settings
		Alert exitCheck =  new Alert(Alert.AlertType.CONFIRMATION);
		exitCheck.setHeaderText("Do you really want to exit Backgammon?");
		exitCheck.initModality(Modality.APPLICATION_MODAL);
		exitCheck.initOwner(Main.getStage());		

		// Exit button
		Button exitBtn = (Button) exitCheck.getDialogPane().lookupButton(ButtonType.OK);
		exitBtn.setText("Exit");
		
		// Exit application
		Optional<ButtonType> closeResponse = exitCheck.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) 
            event.consume();        
	};
	
	/**
	 * DO NOT TOUCH THIS OR ADD THIS ANYWHERE ELSE,
	 * KEEP IN MIND THIS METHOD IS CALLED AFTER THE STAGE IS DONE SHOWING.
	 * ALTERNATIVE METHOD WHERE I DON'T HAVE TO DO THE ABOVE IS PREFERRED.
	 * 
	 * Binds shortcut CTRL+R key combination to the roll dice button.
	 */
	public void setRollDiceAccelarator() {
		Scene scene = rollDieBtn.getScene();
		if (scene == null) {
			throw new IllegalArgumentException("Roll Dice Button not attached to a scene.");
		}
		
		scene.getAccelerators().put(
			new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN),
			new Runnable() {
				@Override
				public void run() {
					rollDieBtn.fire();
				}
			}
		);
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
