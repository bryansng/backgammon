package game_engine;

import events.PointHandler;
import events.PointSelectedEvent;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController extends HBox {
	private Board board;
	private InfoPanel infoPnl;
	private RollDiceButton rollBtn;
	private CommandPanel cmdPnl;
	
	public MainController() {
		super();
		board = new Board();
		infoPnl = new InfoPanel();
		rollBtn = new RollDiceButton();
		cmdPnl = new CommandPanel();
		//initGameListeners();
		initUIListeners();
		initLayout();
		testPointListeners();
	}
	
	private void testPointListeners() {
		addEventHandler(PointSelectedEvent.POINT_SELECTED, new PointHandler() {
			@Override
			public void onClicked(int pointSelected) {
				infoPnl.print("Point clicked is: " + pointSelected + ".");
			}
		});
	}
	
	private void initUIListeners() {
		cmdPnl.setOnAction((ActionEvent event) -> {
			String text = cmdPnl.getText();
			
			if (text.startsWith("/")) {
				parseCommand(cmdPnl.getText().split(" "));
			}
			
			cmdPnl.setText("");
		});

		rollBtn.setOnAction((ActionEvent event) -> {
			infoPnl.print("Rolling Dice.");
		});
	}
	
	private void initLayout() {
		VBox right = new VBox();
		right.getChildren().addAll(infoPnl, rollBtn, cmdPnl);
		
		getChildren().addAll(board, right);
	}
	
	/**
	 * 
	 * @param args
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
				infoPnl.print("Unable to move checkers - there are no checkers to move from pipe " + fromPipe + ".");
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
