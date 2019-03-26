package game_engine;

import java.util.Optional;
import constants.GameConstants;
import constants.MessageType;
import constants.PlayerPerspectiveFrom;
import interfaces.ColorPerspectiveParser;
import musicplayer.MusicPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
 * @author Braddy Yeoh, 17357376
 *
 */
public class MatchController extends GridPane implements ColorPerspectiveParser {
	private Player bottomPlayer;
	private Player topPlayer;
	private GameComponentsController game;
	private InfoPanel infoPnl;
	private RollDieButton rollDieBtn;
	private CommandPanel cmdPnl;
	private CommandController cmd;
	private GameplayController gameplay;
	private EventController event;
	private MusicPlayer musicPlayer;
	private Stage stage;
	private boolean playerInfosEnteredFirstTimeFlag, promptCancelFlag, invalidRoundInput;
	private int maxRoundPerGame;
	
	/**
	 * Default Constructor
	 * 		- Initialize all the components (game, commandPanel, InfoPanel, RollDieButton, etc).
	 * 		- Initialize sub-controllers (Command, Gameplay, GameComponents, Event).
	 * 		- Initialize the layout of the components.
	 * 		- Style the application.
	 */
	public MatchController(Stage stage) {
		super();
		this.stage = stage;
		maxRoundPerGame = 11;
		initApplication();
		initGame();
		style();
	}
	
	/**
	 * Initialize players and UI components.
	 */
	private void initApplication() {
		bottomPlayer = new Player(PlayerPerspectiveFrom.BOTTOM);
		topPlayer = new Player(PlayerPerspectiveFrom.TOP);
		infoPnl = new InfoPanel();
		rollDieBtn = new RollDieButton();
		cmdPnl = new CommandPanel();
		musicPlayer = new MusicPlayer();
		playerInfosEnteredFirstTimeFlag = true;
		promptCancelFlag = false;
		invalidRoundInput = true;
	}
	
	/**
	 * Initialize game components and sub-controllers.
	 */
	private void initGame() {
		game = new GameComponentsController(bottomPlayer, topPlayer);
		gameplay = new GameplayController(stage, this, game, infoPnl, bottomPlayer, topPlayer);
		cmd = new CommandController(stage, this, game, gameplay, infoPnl, bottomPlayer, topPlayer, musicPlayer);
		event = new EventController(stage, this, game, gameplay, cmdPnl, cmd, infoPnl, rollDieBtn);
		initLayout();
	}
	
	public void resetApplication() {
		cmdPnl.reset();
		musicPlayer.reset();
		bottomPlayer.reset();
		topPlayer.reset();
		infoPnl.reset();
		resetGame();
		playerInfosEnteredFirstTimeFlag = true;
		promptCancelFlag = false;
	}
	
	public void resetGame() {
		game.reset();
		gameplay.reset();
		cmd.reset();
		event.reset();
	}

	/**
	 * Remove previous event listeners and start game.
	 * Called every /start.
	 */
	public void restartGame() {
		resetGame();
		startGame();
	}
	
	/**
	 * Asks for player name and start the game.
	 */
	private void startGame() {
		// prompt players for their infos only if it is their first time.
		if (playerInfosEnteredFirstTimeFlag) {
			promptRoundLimit();
			if (!invalidRoundInput)
				return;
			promptPlayerInfos();
			playerInfosEnteredFirstTimeFlag = false;
		}
		
		if (!promptCancelFlag) gameplay.start();
	}
	
	/**
	 * Displays a dialog that prompts players to input names and choose a score limit
	 * 
	 *  NOTE:
	 * 		- Players can start the game with or without changing default round by clicking start.
	 * 		- Players can cancel the game by clicking cancel.
	 * 		- Players can NOT have negative, even rounds.
	 * 
	 */
	private void promptRoundLimit() {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Enter Round Limit");
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(stage);
		
		ButtonType button = new ButtonType("Next", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, button);
		
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(35, 55, 10, 55));
		pane.setHgap(20);
		pane.setVgap(10);
		
		TextField rounds = new TextField();
		rounds.setPromptText("Default: 11 - Odd Rounds Only");
		rounds.setMinHeight(GameConstants.getUIHeight()*0.85);
		rounds.setPadding(new Insets(5));
		
		pane.add(rounds, 0, 0);
		
		dialog.getDialogPane().setContent(pane);
		
		dialog.setResultConverter(click -> {
			if (click == button)
				return new Pair<>(rounds.getText(), rounds.getText());
			return null;
		});
		
		Optional<Pair<String, String>> result = dialog.showAndWait();
		
		if (result.isPresent()) {
			if (isDefault(rounds.getText())) {
				infoPnl.print("Max rounds per game set to " + maxRoundPerGame + ".", MessageType.ANNOUNCEMENT);
				invalidRoundInput = true;
			} else if (isInputValid(rounds.getText())) {
				maxRoundPerGame = Integer.parseInt(rounds.getText());
				infoPnl.print("Max rounds per game set to " + maxRoundPerGame + ".", MessageType.ANNOUNCEMENT);
				invalidRoundInput = true;
			} else {
				infoPnl.print("Input must be a positive, odd number. Please try again.", MessageType.ERROR);
				invalidRoundInput = false;
			}
		} else {
			promptCancelFlag = true;
			infoPnl.print("Game not started");
			invalidRoundInput = false;
		}
	}
	
	/**
	 * 
	 * If user decides to change the max rounds per game, check if it is a positive, odd number
	 * It has to be odd for a winner to be decided
	 * 
	 * @param rounds String that is entered by the user to indicate how many rounds to play to
	 * @return boolean value of whether input is valid or not
	 */
	private boolean isInputValid(String rounds) {
		if (Integer.parseInt(rounds) > 0)
			return (Integer.parseInt(rounds) % 2 == 1);
			
		return false;
	}
	
	/**
	 * 
	 * If user does not enter anything, and wants to go with the default of 11 rounds
	 * 
	 * @param rounds String that is entered by the user to indicate how many rounds to play to
	 * @return boolean value of whether input is valid or not
	 */
	private boolean isDefault(String rounds) {
		return rounds.length() == 0;
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
		//setStyle("-fx-font-size: " + GameConstants.FONT_SIZE + "px; -fx-font-family: '" + GameConstants.FONT_FAMILY + "';");
		setBackground(GameConstants.getTableImage());
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
		terminal.setEffect(new DropShadow(20, 0, 0, Color.BLACK));
		
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
