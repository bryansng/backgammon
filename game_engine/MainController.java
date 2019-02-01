package game_engine;

import java.util.Arrays;

import events.PointHandler;
import events.PointSelectedEvent;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This class represents the entire component of the application,
 * consisting of the game components and the UI components.
 * 
 * These components are children of this class, therefore
 * this class is the root in the layout structure/tree.
 * 
 * @author Bryan Sng
 * @email sngby98@gmail.com
 *
 */
public class MainController extends HBox {
	private Board board;
	private InfoPanel infoPnl;
	private RollDiceButton rollBtn;
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
		rollBtn = new RollDiceButton();
		cmdPnl = new CommandPanel();
		initLayout();
		initUIListeners();
		initPointListeners();
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
	 * Manages all the UI (infoPnl, cmdPnl, rollBtn) listeners.
	 */
	private void initUIListeners() {
		// Listen for entering of commands at the command panel.
		cmdPnl.setOnAction((ActionEvent event) -> {
			String text = cmdPnl.getText();
			if (text.startsWith("/")) {
				parseCommand(cmdPnl.getText().split(" "));
			}
			cmdPnl.setText("");
			
			// TODO add text to a txt file containing the history of commands entered.
			// TODO the up and down arrow should allow the user to navigate between commands.
			// TODO upon typing up or down, set the cmdPnl with the commands.
		});
		
		// Listen for mouse click at roll dice button.
		rollBtn.setOnAction((ActionEvent event) -> {
			infoPnl.print("Rolling Dice.");
		});
	}
	
	/**
	 * Manages the layout of the children, then adds them as the child of MainController (i.e. root).
	 */
	private void initLayout() {
		VBox UI = new VBox();
		UI.getChildren().addAll(infoPnl, rollBtn, cmdPnl);
		
		getChildren().addAll(board, UI);
	}
	
	/**
	 * Takes in an array of strings,
	 * check if they are commands that are recognized by the application.
	 * 
	 * If they are, run them.
	 * 
	 * @param args the array of strings containing the command and its arguments.
	 */
	public void parseCommand(String[] args) {
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
		Scene scene = rollBtn.getScene();
		if (scene == null) {
			throw new IllegalArgumentException("Roll Dice Button not attached to a scene.");
		}
		
		scene.getAccelerators().put(
			new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN),
			new Runnable() {
				@Override
				public void run() {
					rollBtn.fire();
				}
			}
		);
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
