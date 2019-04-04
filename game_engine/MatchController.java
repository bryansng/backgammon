package game_engine;

import java.util.Optional;
import constants.GameConstants;
import constants.MessageType;
import constants.PlayerPerspectiveFrom;
import interfaces.ColorPerspectiveParser;
import interfaces.InputValidator;
import musicplayer.MusicPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
public class MatchController extends GridPane implements ColorPerspectiveParser, InputValidator {
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
	private boolean isPlayerInfosEnteredFirstTime, isPromptCancel, hadCrawfordGame, isCrawfordGame;
	
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
		isPlayerInfosEnteredFirstTime = true;
		isPromptCancel = false;
	}
	
	/**
	 * Initialize game components and sub-controllers.
	 */
	private void initGame() {
		game = new GameComponentsController(bottomPlayer, topPlayer);
		gameplay = new GameplayController(stage, this, game, infoPnl, bottomPlayer, topPlayer);
		cmd = new CommandController(stage, this, game, gameplay, infoPnl, cmdPnl, bottomPlayer, topPlayer, musicPlayer);
		gameplay.setCommandController(cmd);
		event = new EventController(stage, this, game, gameplay, cmdPnl, cmd, infoPnl, rollDieBtn);
		cmd.setEventController(event);
		initLayout();
	}
	
	public void resetApplication() {
		cmdPnl.reset();
		musicPlayer.reset();
		bottomPlayer.reset();
		topPlayer.reset();
		infoPnl.reset();
		resetGame();
		isPlayerInfosEnteredFirstTime = true;
		isPromptCancel = false;
		hadCrawfordGame = false;
		isCrawfordGame = false;
		Settings.setTotalGames(Settings.DEFAULT_TOTAL_GAMES);
	}
	
	public void resetGame() {
		bottomPlayer.setHasCube(false);
		topPlayer.setHasCube(false);
		game.reset();
		gameplay.reset();
		cmd.reset();
		event.reset();
		
		/* Handle Crawford Game */
		// if did not have a crawford game,
		// we check if next game is crawford game.
		if (!hadCrawfordGame && checkIsCrawfordGame()) {
			isCrawfordGame = true;
			hadCrawfordGame = true;
			infoPnl.print("New game is a Crawford game.", MessageType.DEBUG);
		// if had crawford game,
		// we check if the current game is a crawford game,
		// if it is, we reset.
		// giving us:
		// isCrawfordGame = false, hadCrawfordGame = true.
		} else if (isCrawfordGame) {
			isCrawfordGame = false;
			infoPnl.print("New game is not a Crawford game.", MessageType.DEBUG);
		}
		
		if (!hadCrawfordGame && GameConstants.FORCE_TEST_AFTER_CRAWFORD_RULE) {
			isCrawfordGame = true;
			hadCrawfordGame = true;
			infoPnl.print("New game is a Crawford game.", MessageType.DEBUG);
		}
	}
	// Checks if next game is crawford game.
	// is crawford game either winner match score, i.e. TOTAL_GAMES_IN_A_MATCH-1.
	private boolean checkIsCrawfordGame() {
		return topPlayer.getScore() == Settings.TOTAL_GAMES_IN_A_MATCH-1 || bottomPlayer.getScore() == Settings.TOTAL_GAMES_IN_A_MATCH-1;
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
		if (isPlayerInfosEnteredFirstTime) {
			promptStartGame();
		}
		
		if (!isPromptCancel) {
			isPlayerInfosEnteredFirstTime = false;
			gameplay.start();
		}
	}
		
	/**
	 * Returns true if match over.
	 * Match over if player score greater or equal than total games.
	 * @return boolean value indicating if match is over.
	 */
	public boolean isMatchOver() {
		return (topPlayer.getScore() >= Settings.TOTAL_GAMES_IN_A_MATCH) || (bottomPlayer.getScore() >= Settings.TOTAL_GAMES_IN_A_MATCH);
	}
	
	/**
	 * If a player has a score equal to the maximum score,
	 * then announce the winner and ask if they want to play again.
	 */
	public void handleMatchOver() {
		Player winner = gameplay.getCurrent();
		Alert dialog = new Alert(Alert.AlertType.INFORMATION);
		dialog.setTitle("Congratulations!");
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(stage);
		dialog.setGraphic(null);
		dialog.setContentText("Play again?");
		dialog.setHeaderText("The winner of the match is " + winner.getName());
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		
		// Restart game if player wishes,
		// else exit gameplay mode and enter free-for-all mode.
		if (ButtonType.OK.equals(result.get())) {
			resetApplication();
			cmd.runCommand("/start");
		} else {
			resetApplication();
			infoPnl.print("Enter /start if you wish to play again.", MessageType.ANNOUNCEMENT);
			infoPnl.print("Enter /quit if you wish to quit.", MessageType.ANNOUNCEMENT);
		}
	}
	
	/**
	 * Displays a dialog that prompts players to input
	 * 		- Player names and colors.
	 * 		- Score limit for this match.
	 * 
	 * NOTE: 
	 * 		- Players can start the game with or without changing default round by clicking start.
	 * 		- Players can cancel starting the game by clicking cancel.
	 * 		- Players can NOT have empty names.
	 * 		- Players can NOT have negative or even totalGames.
	 */
	private void promptStartGame() {
		// Dialog to prompt player.
		Dialog<promptResults> dialog = new Dialog<>();
		dialog.setTitle("Please enter player names and number of games to play");
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
		
		// Player color + total games labels.
		ImageView black = new ImageView(this.getClass().getResource("/game/img/checkers/black_checkers.png").toString());
		ImageView white = new ImageView(this.getClass().getResource("/game/img/checkers/white_checkers.png").toString());
		Label bLabel = new Label("", black);
		Label wLabel = new Label("", white);
		Label totalGamesLabel = new Label("Total number of games");
		
		// Player name + total games fields.
		Insets inset = new Insets(5);
		TextField bName = new TextField();
		bName.setPromptText("Default: Tea");
		bName.setMinHeight(GameConstants.getUIHeight()*0.85);
		bName.setPadding(inset);
		TextField wName =  new TextField();
		wName.setPromptText("Default: Cup");
		wName.setMinHeight(GameConstants.getUIHeight()*0.85);
		wName.setPadding(inset);
		TextField totalGames = new TextField();
		totalGames.setPromptText("Default: 11");
		totalGames.setMinHeight(GameConstants.getUIHeight()*0.85);
		totalGames.setPadding(inset);
		
		// Add labels and name fields to pane.
		pane.add(bLabel, 0, 0);
		pane.add(bName, 1, 0);
		pane.add(wLabel, 0, 1);
		pane.add(wName, 1, 1);
		pane.add(totalGamesLabel, 0, 2);
		pane.add(totalGames, 1, 2);
		
		// Add pane to dialog.
		dialog.getDialogPane().setContent(pane);
		
		// On click start button, return player names as result.
		// Else result is null, cancel the game.
		dialog.setResultConverter(click -> {
			if (click == button)
				return new promptResults(bName.getText(), wName.getText(), totalGames.getText());
			return null;
		});
		
		// Show dialog to get player input.
		Optional<promptResults> result = dialog.showAndWait();
		
		// If result is present and name is not empty, change player names.
		// If result is null, cancel starting the game.
		result.ifPresent((promptResults results) -> {
			if (results.bName.length() != 0)
				cmd.runCommand("/name black " + bName.getText());
			if (results.wName.length() != 0)
				cmd.runCommand("/name white " + wName.getText());			

			String userInput = results.totalGames;
			if (userInput.length() == 0 || isValidInput(userInput)) {
				if (userInput.length() != 0) Settings.setTotalGames(Integer.parseInt(userInput));
				infoPnl.print("Max totalGames per game set to " + Settings.TOTAL_GAMES_IN_A_MATCH + ".", MessageType.DEBUG);
			} else {
				infoPnl.print("You must play to a positive odd number. Please try again.", MessageType.ERROR);
				promptStartGame();
			}
			
			isPromptCancel = false;
        });
		if (!result.isPresent()) {
			isPromptCancel = true;
			infoPnl.print("Game not started.");
		}
	}
	/**
	 * Inner class that stores results of promptStartGame() so we can process user input.
	 * 		- Player names + total games they play to
	 * NOTE: totalGames is a string here so we can validate player input before processing it
	 */
	private static class promptResults {
		String bName;
		String wName;
		String totalGames;
		
		promptResults(String bName, String wName, String totalGames) {
			this.bName = bName;
			this.wName = wName;
			this.totalGames = totalGames;
		}
	}
	// valid input if:
	// - is a number.
	// - positive.
	// - odd.
	private boolean isValidInput(String userInput) {
		boolean isValidInput = false;
		// is a number.
		if (isNumber(userInput)) {
			int num = Integer.parseInt(userInput);
			
			// positive and odd.
			if (num > 0 && num % 2 != 0) {
				isValidInput = true;
			}
		}
		return isValidInput;
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
	
	public boolean isCrawfordGame() {
		return isCrawfordGame;
	}
}
