package game_engine;

import constants.MessageType;
import java.util.Optional;
import events.CheckersStorerHandler;
import events.CheckersStorerSelectedEvent;
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
public class MainController extends GridPane implements ColorParser {
	private GameController game;
	private InfoPanel infoPnl;
	private RollDieButton rollDieBtn;
	private CommandPanel cmdPnl;
	private CommandController cmd;
	
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
		cmd = new CommandController(game, infoPnl);
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
							cmd.runCommand("/move " + fromPip + " " + toPip);
						} else if (isBarSelectionMode) {
							String fromBar = parseColor(((Bar) storerSelected).getColour());
							cmd.runCommand("/move " + fromBar + " " + toPip);
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
						String toHome = parseColor(((Home) object).getColour());
						
						if (isPointSelectionMode) {
							int fromPip = ((Point) storerSelected).getPointNumber() + 1;
							cmd.runCommand("/move " + fromPip + " " + toHome);
						} else if (isBarSelectionMode) {
							String fromBar = parseColor(((Bar) storerSelected).getColour());
							cmd.runCommand("/move " + fromBar + " " + toHome);
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
		
		//Main.getStage().setOnCloseRequest(onExitCheck);
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
				cmd.runCommand(cmdPnl.getText());
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
			cmd.runCommand("/roll " + new Integer(dieState).toString());
		});
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
		
		infoPnl.print("Trying to quit game.");
		cmd.runSaveCommand();

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
}
