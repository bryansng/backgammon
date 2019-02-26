package game_engine;

import java.util.Optional;

import constants.GameConstants;
import constants.MessageType;
import events.CheckersStorerHandler;
import events.CheckersStorerSelectedEvent;
import game.Bar;
import game.CheckersStorer;
import game.Home;
import game.Pip;
import interfaces.ColorParser;
import interfaces.InputValidator;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ui.CommandPanel;
import ui.InfoPanel;
import ui.RollDieButton;

/**
 * This class handles all the events that is triggered by the user.
 * Sub-controller of MainController.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class EventController implements ColorParser, InputValidator {
	private Stage stage;
	private MainController root;
	private GameComponentsController game;
	private GameplayController gameplay;
	private InfoPanel infoPnl;
	private RollDieButton rollDieBtn;
	private CommandPanel cmdPnl;
	private CommandController cmd;
	
	public EventController(Stage stage, MainController root, GameComponentsController game, GameplayController gameplay, CommandPanel cmdPnl, CommandController cmd, InfoPanel infoPnl, RollDieButton rollDieBtn) {
		this.stage = stage;
		this.root = root;
		this.game = game;
		this.gameplay = gameplay;
		this.cmdPnl = cmdPnl;
		this.cmd = cmd;
		this.infoPnl = infoPnl;
		this.rollDieBtn = rollDieBtn;
		initGameListeners();
		initUIListeners();
	}
	
	private void initGameListeners() {
		// Exit pip and bar selection mode when any part of the game board is clicked.
		game.setOnMouseClicked((MouseEvent event) -> {
			game.unhighlightAll();
			isPipSelectionMode = false;
			isBarSelectionMode = false;
			
			// highlight the possible moves if player hasn't move.
			if (gameplay.isStarted() && !gameplay.isMoved()) {
				game.getBoard().highlightFromPipsAndFromBarChecker(gameplay.getValidMoves());
			}
		});
		
		initCheckersStorersListeners();
	}
	
	private void initCheckersStorersListeners() {
		root.addEventHandler(CheckersStorerSelectedEvent.STORER_SELECTED, checkersStorerHandler);
	}
	
	/**
	 * Event handler for all checker storers (pips, bars, homes).
	 * Separated from initCheckersStorersListeners() for easier removal.
	 */
	private boolean isPipSelectionMode = false;
	private boolean isBarSelectionMode = false;
	private CheckersStorer storerSelected;
	CheckersStorerHandler checkersStorerHandler = new CheckersStorerHandler() {
		@Override	
		public void onClicked(CheckersStorer object) {
			// pip selected, basis for fromPip or toPip selection.
			if (object instanceof Pip) {
				// neither pip nor bar selected, basis for fromPip selection.
				if (!isPipSelectionMode && !isBarSelectionMode) {
					storerSelected = object;
					int fromPip = ((Pip) storerSelected).getPipNumber();
					// same as ((gameplay.isStarted() && gameplay.isValidFro(fromPip)) || (!gameplay.isStarted()))
					if (!gameplay.isStarted() || gameplay.isValidFro(fromPip)) {
						gameplay.highlightPips(fromPip);
						isPipSelectionMode = true;
						infoPnl.print("Pip clicked is: " + gameplay.correct(fromPip) + ".", MessageType.DEBUG);
					} else {
						infoPnl.print("You can only move from highlighted checkers.", MessageType.ERROR);
					}
				// either pip or bar selected, basis for toPip or toBar selection.
				} else {
					// prevent moving checkers from pip to bar.
					// i.e select pip, to bar.
					int toPip = ((Pip) object).getPipNumber();
					
					if (isPipSelectionMode) {
						int fromPip = ((Pip) storerSelected).getPipNumber();
						cmd.runCommand("/move " + fromPip + " " + toPip);
					} else if (isBarSelectionMode) {
						String fromBar = parseColor(((Bar) storerSelected).getColor());
						cmd.runCommand("/move " + fromBar + " " + toPip);
					}
					gameplay.unhighlightPips();
					isPipSelectionMode = false;
					isBarSelectionMode = false;
				}
			// bar selected, basis for fromBar selection.
			} else if (object instanceof Bar) {
				// prevent entering into both pip and bar selection mode.
				if (!isPipSelectionMode) {
					storerSelected = object;
					game.getBoard().highlightAllPipsExcept(-1);
					isBarSelectionMode = true;
					infoPnl.print("Bar clicked.", MessageType.DEBUG);
				}
			// home selected, basis for toHome selection.
			} else if (object instanceof Home) {
				if (isPipSelectionMode || isBarSelectionMode) {
					String toHome = parseColor(((Home) object).getColor());
					
					if (isPipSelectionMode) {
						int fromPip = ((Pip) storerSelected).getPipNumber();
						cmd.runCommand("/move " + fromPip + " " + toHome);
					} else if (isBarSelectionMode) {
						String fromBar = parseColor(((Bar) storerSelected).getColor());
						cmd.runCommand("/move " + fromBar + " " + toHome);
					}
					gameplay.unhighlightPips();
					isPipSelectionMode = false;
					isBarSelectionMode = false;
				}
				infoPnl.print("Home clicked.", MessageType.DEBUG);
			} else {
				infoPnl.print("Other instances of checkersStorer were clicked.", MessageType.DEBUG);
			}
		}
	};
	
	/**
	 * Manages all the UI (infoPnl, cmdPnl, rollDieBtn) listeners.
	 */
	private void initUIListeners() {
		initCommandPanelListeners();
		initRollDieButtonListeners();
		
		if (!GameConstants.DEBUG_MODE)
			initStageListeners();
	}

	/**
	 * Manages command panel listeners.
	 * 		- if its a command (i.e. start with '/'), run it.
	 * 		- echoes player input to infoPanel.
	 * 		- does not echo empty strings/whitespace.
	 */
	private void initCommandPanelListeners() {
		cmdPnl.setOnAction((ActionEvent event) -> {
			String text = cmdPnl.getText();
			String[] args = text.split(" ");
			
			if (text.startsWith("/")) {
				cmd.runCommand(cmdPnl.getText(), true);
			} else if (args.length == 2 && isPip(args[0]) && isPip(args[1])) {
				cmd.runCommand("/move " + text, true);
			} else if (text.equals("quit")) {
				cmd.runCommand("/quit");
			} else if (text.equals("save")) {
				cmd.runCommand("/save");
			} else if (text.equals("next")) {
				cmd.runCommand("/next");
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
			 * Consider using linked list to store the strings of commands entered.
			 */
		});
	}
	
	private int dieState = 2;
	private void initRollDieButtonListeners() {
		rollDieBtn.setOnAction((ActionEvent event) -> {
			if (dieState == 1) {
				dieState = 2;
			} else {
				dieState = 1;
			}
			cmd.runCommand("/roll " + Integer.toString(dieState));
		});
	}
	
	private void initStageListeners() {
		// checks if player really wants to exit game prevents accidental exits
		stage.setOnCloseRequest((WindowEvent event) -> {
			// Alert settings.
			Alert exitCheck =  new Alert(Alert.AlertType.CONFIRMATION);
			exitCheck.setHeaderText("Do you really want to exit Backgammon?");
			exitCheck.initModality(Modality.APPLICATION_MODAL);
			exitCheck.initOwner(stage);
			
			infoPnl.print("Trying to quit game.");
			cmd.runSaveCommand();
			
			// Exit button.
			Button exitBtn = (Button) exitCheck.getDialogPane().lookupButton(ButtonType.OK);
			exitBtn.setText("Exit");
			
			// Exit application.
			Optional<ButtonType> closeResponse = exitCheck.showAndWait();
			if (!ButtonType.OK.equals(closeResponse.get())) {
				event.consume();
			}
		});
	}
	
	public void removeListeners() {
		root.removeEventHandler(CheckersStorerSelectedEvent.STORER_SELECTED, checkersStorerHandler);
		game.setOnMouseClicked(null);
		rollDieBtn.setOnAction(null);
		cmdPnl.setOnAction(null);
		stage.setOnCloseRequest(null);
	}
}
