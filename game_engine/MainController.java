package game_engine;

import constants.PlayerPerspectiveFrom;
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
public class MainController extends GridPane {
	private Player bottomPlayer;
	private Player topPlayer;
	private GameComponentsController game;
	private InfoPanel infoPnl;
	private RollDieButton rollDieBtn;
	private CommandPanel cmdPnl;
	private CommandController cmd;
	@SuppressWarnings("unused")
	private EventController event;
	private GameplayController gameplay;
	
	/**
	 * Default Constructor
	 * 		- Initialize all the instance variables.
	 * 		- Initialize instance variables layouts.
	 * 		- Initialize instance variables listeners.
	 * 		- Initialize game components listeners (i.e. points, for now).
	 */
	public MainController(Stage stage) {
		super();
		bottomPlayer = new Player("Tea", 0, Color.WHITE, PlayerPerspectiveFrom.BOTTOM);
		topPlayer = new Player("Cup", 0, Color.BLACK, PlayerPerspectiveFrom.TOP);
		game = new GameComponentsController(bottomPlayer, topPlayer);
		infoPnl = new InfoPanel();
		rollDieBtn = new RollDieButton();
		cmdPnl = new CommandPanel();
		gameplay = new GameplayController(game, infoPnl, bottomPlayer, topPlayer);
		cmd = new CommandController(stage, game, gameplay, infoPnl);
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
