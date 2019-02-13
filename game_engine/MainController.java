package game_engine;

import constants.DieInstance;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
	private Player bottomPlayer;
	private Player topPlayer;
	private GameController game;
	private InfoPanel infoPnl;
	private RollDieButton rollDieBtn;
	private CommandPanel cmdPnl;
	private CommandController cmd;
	@SuppressWarnings("unused")
	private EventController event;
	
	/**
	 * Default Constructor
	 * 		- Initialize all the instance variables.
	 * 		- Initialize instance variables layouts.
	 * 		- Initialize instance variables listeners.
	 * 		- Initialize game components listeners (i.e. points, for now).
	 */
	public MainController(Stage stage) {
		super();
		bottomPlayer = new Player("Tea", 0, Color.WHITE);
		topPlayer = new Player("Cup", 0, Color.BLACK);
		game = new GameController(bottomPlayer, topPlayer);
		infoPnl = new InfoPanel();
		rollDieBtn = new RollDieButton();
		cmdPnl = new CommandPanel();
		cmd = new CommandController(stage, this, game, infoPnl);
		event = new EventController(stage, this, game, cmdPnl, cmd, infoPnl, rollDieBtn);
		style();
		initLayout();
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
	
	// should activate by /start.
	public void startGameLoop() {
		// get which player starts first.
		Player firstPlayer;
		firstPlayer = getFirstPlayerToRoll();
		infoPnl.print("First player to move is: " + firstPlayer.getName() + ".");
	}
	
	// auto roll die to see which player first.
	// if draw, roll again.
	private Player getFirstPlayerToRoll() {
		int[] res = null;
		res = game.rollDices(DieInstance.SINGLE);
		int bottomPlayerRoll = res[0];
		int topPlayerRoll = res[1];
		
		if (bottomPlayerRoll > topPlayerRoll) {
			return bottomPlayer;
		} else if (topPlayerRoll > bottomPlayerRoll) {
			return topPlayer;
		} else {
			return getFirstPlayerToRoll();
		}
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
