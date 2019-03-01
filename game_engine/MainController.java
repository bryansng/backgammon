package game_engine;

import java.util.Optional;
import constants.GameConstants;
import constants.PlayerPerspectiveFrom;
import interfaces.ColorPerspectiveParser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import ui.CommandPanel;
import ui.InfoPanel;
import ui.RollDieButton;

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
public class MainController extends GridPane implements ColorPerspectiveParser {
	private Player bottomPlayer;
	private Player topPlayer;
	private GameComponentsController game;
	private InfoPanel infoPnl;
	private RollDieButton rollDieBtn;
	private CommandPanel cmdPnl;
	private CommandController cmd;
	private GameplayController gameplay;
	private EventController event;
	private Stage stage;
	private boolean playerInfosEnteredFirstTimeFlag, promptCancelFlag;
	
	/**
	 * Default Constructor
	 * 		- Initialize all the components (game, commandPanel, InfoPanel, RollDieButton, etc).
	 * 		- Initialize sub-controllers (Command, Gameplay, GameComponents, Event).
	 * 		- Initialize the layout of the components.
	 * 		- Style the application.
	 */
	public MainController(Stage stage) {
		super();
		this.stage = stage;
		resetApplication();
		style();
	}
	
	/**
	 * Initialize players and UI components.
	 */
	public void resetApplication() {
		bottomPlayer = new Player("Cup", 0, getColor(PlayerPerspectiveFrom.BOTTOM), null, PlayerPerspectiveFrom.BOTTOM);
		topPlayer = new Player("Tea", 0, getColor(PlayerPerspectiveFrom.TOP), null, PlayerPerspectiveFrom.TOP);	
		infoPnl = new InfoPanel();
		rollDieBtn = new RollDieButton();
		cmdPnl = new CommandPanel();
		playerInfosEnteredFirstTimeFlag = true;
		promptCancelFlag = false;
		initGame();
	}
	
	/**
	 * Initialize game components and sub-controllers.
	 */
	private void initGame() {
		game = new GameComponentsController(bottomPlayer, topPlayer);
		gameplay = new GameplayController(game, infoPnl, bottomPlayer, topPlayer);
		cmd = new CommandController(stage, this, game, gameplay, infoPnl, bottomPlayer, topPlayer);
		event = new EventController(stage, this, game, gameplay, cmdPnl, cmd, infoPnl, rollDieBtn);
		
		bottomPlayer.setHome(game.getHome(getColor(PlayerPerspectiveFrom.BOTTOM)));
		topPlayer.setHome(game.getHome(getColor(PlayerPerspectiveFrom.TOP)));
		
		initLayout();
	}
	
	/**
	 * Remove previous event listeners and start game.
	 * Called every /start.
	 */
	public void restartGame() {
		event.removeListeners();	// deactivate previous listeners.
		startGame();
	}
	
	/**
	 * Re-initialize the game components and start the game.
	 */
	public void startGame() {
		initGame();
		
		// prompt players for their infos only if it is their first time.
		if (playerInfosEnteredFirstTimeFlag) {
			promptPlayerInfos();
			playerInfosEnteredFirstTimeFlag = false;
		}
		
		if (!promptCancelFlag) gameplay.start();
	}
	
	/**
	 * Displays a dialog that prompts players to input names and choose checker colors.
	 * 
	 * NOTE:
	 * 		- Players can start the game with or without changing default names by clicking start.
	 * 		- Players can cancel the game by clicking cancel.
	 * 		- Players can NOT have empty names.
	 */
	private void promptPlayerInfos() {
		// Dialog to prompt player.
		Dialog<Pair<String, String>> dialog =  new Dialog<>();
		dialog.setTitle("Please enter players' names");
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(stage);
		
		// Start and cancel buttons for dialog.
		ButtonType button = new ButtonType("Start", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, button);
		
		// Layout for player name fields.
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(35, 55, 10, 55));
		pane.setHgap(20);
		pane.setVgap(10);
		
		// Player color labels.
		ImageView black = new ImageView(this.getClass().getResource("/game/img/checkers/black_checkers.png").toString());
		ImageView white = new ImageView(this.getClass().getResource("/game/img/checkers/white_checkers.png").toString());
		Label bLabel = new Label("", black);
		Label wLabel = new Label("", white);
		
		// Player name fields.
		Insets inset = new Insets(5);
		TextField bName = new TextField();
		bName.setPromptText("Default: Tea");
		bName.setMinHeight(GameConstants.getUIHeight()*0.85);
		bName.setPadding(inset);
		TextField wName =  new TextField();
		wName.setPromptText("Default: Cup");
		wName.setMinHeight(GameConstants.getUIHeight()*0.85);
		wName.setPadding(inset);
		
		// Add labels and name fields to pane.
		pane.add(bLabel, 0, 0);
		pane.add(bName, 1, 0);
		pane.add(wLabel, 0, 1);
		pane.add(wName, 1, 1);
		
		// Add pane to dialog.
		dialog.getDialogPane().setContent(pane);
		
		// On click start button, return player names as result.
		// Else result is null, cancel the game.
		dialog.setResultConverter(click -> {
			if (click == button)
				return new Pair<>(bName.getText(), wName.getText());
			return null;
		});
		
		// Show dialog to get player input.
		Optional<Pair<String, String>> result = dialog.showAndWait();
		
		// If result is present and name is not empty, change player names.
		// If result is null, cancel starting the game.
		if (result.isPresent()) {
			if (bName.getText().length() != 0)
				cmd.runCommand("/name black " + bName.getText());
			if (wName.getText().length() != 0)
				cmd.runCommand("/name white " + wName.getText());
		} else {
			promptCancelFlag = true;
			infoPnl.print("Game not started.");
		}
	}
	
	/**
	 * Style MainController (i.e. root).
	 */
	public void style() {
		setStyle("-fx-font-size: " + GameConstants.FONT_SIZE + "px; -fx-font-family: '" + GameConstants.FONT_FAMILY + "';");
		setPadding(new Insets(10));
		setVgap(GameConstants.getUIVGap());
		setHgap(5);
		setAlignment(Pos.CENTER);
		setMaxSize(GameConstants.getScreenSize().getWidth(), GameConstants.getScreenSize().getHeight());
	}

	/**
	 * Manages the layout of the children, then adds them as the child of MainController (i.e. root).
	 */
	public void initLayout() {
		VBox terminal = new VBox();
		terminal.getChildren().addAll(infoPnl, cmdPnl);
		terminal.setAlignment(Pos.CENTER);
		
		getChildren().clear();
		add(game, 0, 0, 1, 3);
		add(terminal, 1, 0);
		add(rollDieBtn, 1, 2);
	}
	
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
