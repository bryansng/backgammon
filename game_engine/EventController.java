package game_engine;

import java.util.Optional;
import constants.MessageType;
import events.CheckersStorerHandler;
import events.CheckersStorerSelectedEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This class handles all the events that is triggered by the user.
 * Sub-controller of MainController.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class EventController implements ColorParser {
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
	
	/**
	 * Manages game listeners.
	 */
	private void initGameListeners() {
		// Exit point selection mode when any part of the game board is clicked.
		game.setOnMouseClicked((MouseEvent event) -> {
			game.getBoard().unhighlightPipsAndCheckers();
			isPointSelectionMode = false;
			isBarSelectionMode = false;
			
			// highlight again the possible moves if player hasn't move.
			if (gameplay.isStarted() && !gameplay.isMoved()) {
				game.getBoard().highlightFromPipsChecker(gameplay.getValidMoves());
			}
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
		root.addEventHandler(CheckersStorerSelectedEvent.STORER_SELECTED, new CheckersStorerHandler() {
			@Override	
			public void onClicked(CheckersStorer object) {
				// point selected, basis for fromPip or toPip selection.
				if (object instanceof Point) {
					// neither point nor bar selected, basis for fromPip selection.
					if (!isPointSelectionMode && !isBarSelectionMode) {
						storerSelected = object;
						int fromPip = ((Point) storerSelected).getPointNumber();
						highlightPips(fromPip);
						isPointSelectionMode = true;
						infoPnl.print("Point clicked is: " + (fromPip+1) + ".", MessageType.DEBUG);
					// either point or bar selected, basis for toPip or toBar selection.
					} else {
						// prevent moving checkers from point to bar.
						// i.e select point, to bar.
						int toPip = ((Point) object).getPointNumber();
						
						if (isPointSelectionMode) {
							int fromPip = ((Point) storerSelected).getPointNumber();
							cmd.runCommand("/move " + fromPip + " " + toPip);
						} else if (isBarSelectionMode) {
							String fromBar = parseColor(((Bar) storerSelected).getColour());
							cmd.runCommand("/move " + fromBar + " " + toPip);
						}
						unhighlightPips();
						isPointSelectionMode = false;
						isBarSelectionMode = false;
					}
				// bar selected, basis for fromBar selection.
				} else if (object instanceof Bar) {
					// prevent entering into both point and bar selection mode.
					if (!isPointSelectionMode) {
						storerSelected = object;
						game.getBoard().highlightAllPipsExcept(-1);
						isBarSelectionMode = true;
						infoPnl.print("Bar clicked.", MessageType.DEBUG);
					}
				// home selected, basis for toHome selection.
				} else if (object instanceof Home) {
					if (isPointSelectionMode || isBarSelectionMode) {
						String toHome = parseColor(((Home) object).getColour());
						
						if (isPointSelectionMode) {
							int fromPip = ((Point) storerSelected).getPointNumber();
							cmd.runCommand("/move " + fromPip + " " + toHome);
						} else if (isBarSelectionMode) {
							String fromBar = parseColor(((Bar) storerSelected).getColour());
							cmd.runCommand("/move " + fromBar + " " + toHome);
						}
						unhighlightPips();
						isPointSelectionMode = false;
						isBarSelectionMode = false;
					}
					infoPnl.print("Home clicked.", MessageType.DEBUG);
				} else {
					infoPnl.print("Other instances of checkersStorer were clicked.", MessageType.DEBUG);
				}
			}
		});
	}
	
	private void highlightPips(int fromPip) {
		if (gameplay.isRolled()) {
			game.getBoard().highlightToPips(gameplay.getValidMoves(), fromPip);
			/*
			PipMove aMove = gameplay.getMoveOf(fromPip);
			if (aMove != null) {
				game.getBoard().highlightToPips(aMove);
			} else {
				infoPnl.print("There is no possible moves related to fromPip: " + (fromPip+1), MessageType.DEBUG);
			}
			*/
		} else {
			game.getBoard().highlightAllPipsExcept(fromPip);
		}
	}
	
	private void unhighlightPips() {
		if (gameplay.isStarted()) {
			if (gameplay.isMoved()) game.getBoard().unhighlightPipsAndCheckers();
			else game.getBoard().highlightFromPipsChecker(gameplay.getValidMoves());
		} else {
			game.getBoard().unhighlightPipsAndCheckers();
		}
	}
	
	/**
	 * Manages all the UI (infoPnl, cmdPnl, rollDieBtn) listeners.
	 */
	private void initUIListeners() {
		initCommandPanelListeners();
		initRollDieButtonListeners();
		//initStageListeners();
	}

	/**
	 * Manages command panel listeners.
	 * 		- echoes player input to infoPanel.
	 * 		- does not echo empty strings/whitespace.
	 */
	private void initCommandPanelListeners() {
		cmdPnl.setOnAction((ActionEvent event) -> {
			String text = cmdPnl.getText();
			
			if (text.startsWith("/")) {
				cmd.runCommand(cmdPnl.getText(), true);
			} else if (text.equals("")) {
				// ignores if user types nothing.
			} else if (text.equals("quit")) {
				cmd.runCommand("/quit");
			} else if (text.equals("save")) {
				cmd.runCommand("/save");
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
	
	/**
	 * Checks if player really wants to exit game prevents accidental exits
	 */
	@SuppressWarnings("unused")
	private void initStageListeners() {
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
}
