package game_engine;

import java.util.Arrays;
import events.PointHandler;
import events.PointSelectedEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * This class represents the entire component of the application,
 * consisting of the game components and the UI components.
 * 
 * These components are children of this class, therefore
 * this class is the root in the layout structure/tree.
 * 
 * @author Bryan Sng
 * @author @LxEmily
 * @email sngby98@gmail.com
 *
 */
public class MainController extends GridPane {
	private Board board;
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
		board = new Board();
		infoPnl = new InfoPanel();
		rollDieBtn = new RollDieButton();
		cmdPnl = new CommandPanel();
		style();
		initLayout();
		initUIListeners();
		initPointListeners();
	}
	
	/**
	 * Style MainController (i.e. root).
	 */
	public void style() {
		setStyle("-fx-font-size: 14px; -fx-font-family: 'Consolas';");
		setPadding(new Insets(15));
		setVgap(5);
		setHgap(10);
		setAlignment(Pos.CENTER);
	}

	/**
	 * Manages the layout of the children, then adds them as the child of MainController (i.e. root).
	 */
	public void initLayout() {
		add(board, 0, 0, 1, 3);
		add(infoPnl, 1, 0);
		add(cmdPnl, 1, 1);
		add(rollDieBtn, 1, 2);
	}
	
	/**
	 * Manages points listeners
	 * 
	 * TODO moving checkers via mouse features and functions should be elaborated starting here.
	 */
	private void initPointListeners() {
		// upon mouse clicking, highlight all the points except for the point clicked.
		addEventHandler(PointSelectedEvent.POINT_SELECTED, new PointHandler() {
			@Override
			public void onClicked(int pointSelected) {
				infoPnl.print("Point clicked is: " + (pointSelected+1) + ".");
				
				Point[] points = board.getPoints();
				for (int i = 0; i < points.length; i++) {
					if (i == pointSelected) {
						points[i].setNormalImage();
					} else {
						points[i].setHighlightImage();
					}
				}
			}
		});
	}
	
	/**
	 * Manages all the UI (infoPnl, cmdPnl, rollDieBtn) listeners.
	 * 
	 * Methods that "listen" for actions on game components
	 * 	- Roll die with clicks or CTRL+R
	 * 	- Text commands
	 * 		- quit to terminate
	 * 		- echoes player input
	 * 		- potential: "help" for all commands, "save" to save to txt
	 */
	private void initUIListeners() {
		initCommandPanelListener();
		initRollDieButtonListener();
	}
	
	private void initCommandPanelListener() {
		/**
		 * Listens for certain text commands from player
		 * 	- quit to terminate game
		 * 	- echoes player input to infoPanel
		 */
		cmdPnl.setOnAction((ActionEvent event) -> {
			String text = cmdPnl.getText();
			
			if (text.startsWith("/")) {
				parseCommand(cmdPnl.getText().split(" "));
			} else if ("/quit".compareTo(text) == 0) {
				infoPnl.print("You have quitted the game. Bye bye!");
				Platform.exit();
			} else {
				infoPnl.print(text);
			}
			
			cmdPnl.setText("");
			
			// TODO add text to a txt file containing the history of commands entered.
			// TODO the up and down arrow should allow the user to navigate between commands.
			// TODO upon typing up or down, set the cmdPnl with the commands.
		});
	}
	
	private void initRollDieButtonListener() {
		/**
		 * Listens for actions (i.e. mouse clicks) that roll the die
		 * 	- Clicking on rollDie button
		 * 	- CTRL + R shortcut key on overall scene
		 */
		rollDieBtn.setOnAction((ActionEvent event) -> {
			infoPnl.print("Rolling Die.");
		});
		
		rollDieBtn.setOnMousePressed((MouseEvent event) -> {
			// make button distinct on click with shadow on click
			rollDieBtn.setEffect(new DropShadow());
		});
		
		rollDieBtn.setOnMouseReleased((MouseEvent event) -> {
			// remove shadow when click is released
			rollDieBtn.setEffect(new DropShadow(0, Color.BLACK));
		});
	}
	
	/**
	 * Takes in an array of strings,
	 * check if they are commands that are recognized by the application.
	 * 
	 * If they are, run them.
	 * 
	 * @param args the array of strings containing the command and its arguments.
	 */
	private void parseCommand(String[] args) {
		/*
		 * Command: /move fromPipe toPipe
		 * fromPipe and toPipe will be one-index number based.
		*/
		if (args[0].equals("/move")) {
			int fromPipe = Integer.parseInt(args[1]);
			int toPipe = Integer.parseInt(args[2]);
			if (board.moveCheckers(fromPipe, toPipe)) {
				infoPnl.print("Moving checker from " + fromPipe + " to " + toPipe + ".");
			} else {
				infoPnl.print("Error: Unable to move checkers - there are no checkers to move from pipe " + fromPipe + ".");
			}
		}
		/**
		 * Command: /roll playerNumber
		 * 1 is the player with the perspective from the bottom, dices will be on the left.
		 * 2 is the player with the perspective from the top, dices will be on the right.
		 */
		else if (args[0].equals("/roll")) {
			int playerNum;
			if (args.length == 1) {
				playerNum = 1;
			} else {
				playerNum = Integer.parseInt(args[1]);
			}
			
			// rollDices returns null if playerNum is invalid.
			int[] res = board.rollDices(playerNum);
			if (res != null) {
				infoPnl.print("Roll dice results: " + Arrays.toString(res));
			} else {
				infoPnl.print("Error: player number incorrect. It must be either 1 or 2.");
			}
		}
		else {
			infoPnl.print("Error: Unknown Command.");
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
		/*
		// CTRL+R
		scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			final KeyCombination keyComb = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
			public void handle(KeyEvent key) {
				if (keyComb.match(key)) {
					System.out.println("Key Pressed: " + keyComb); // for debug
					// inform player
					rolledDie(infoPanel);
					key.consume(); // <-- stops passing the event to next node
				}
			}
		});
		*/
	}
	
	/**
	 * TAKE YOUR DIRTY HANDS OFF THESE TWO METHODS.
	 * THESE TWO METHODS ARE THE "LISTEN AT ROOT AND GET POINT WAY."
	 * This will work, you just have to deal with the else case.
	 * 
	private void initGameListeners() {
		// upon clicking, highlight all the points except for the point clicked.
		setOnMouseClicked((MouseEvent event) -> {
			int pointNum = getPointNumber(event);
			
			Point[] points = board.getPoints();
			for (int i = 0; i < points.length; i++) {
				if (i == pointNum) {
					points[i].setNormalImage();
				} else {
					points[i].setHighlightImage();
				}
			}
		});
	}
	
	private int getPointNumber(MouseEvent event) {
		// returns an event type, all nodes implements event type.
		// so in theory, the event type is a node, that node is our object (point / checker).
		Object target = event.getTarget();
		// check if what we clicked is a point,
		// if not, it should be a checker, in which case we get its parent.
		if (target instanceof Point) {
			return ((Point) target).getPointNumber();
		} else if (target instanceof Checker) {
			return (((Point) ((Checker) target).getParent())).getPointNumber();
		}
		else {
			throw new targetNotPointException();
			// if use this, write code here to return a number to symbolize the unhighlighting of points.
		}
	}
	*/
}
