package game_engine;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {
	private Board board;
	private InformationPanel infoPnl;
	private RollDiceButton rollBtn;
	private CommandPanel cmdPnl;
	private HBox root;
	
	public MainController() {
		board = new Board();
		infoPnl = new InformationPanel();
		rollBtn = new RollDiceButton();
		cmdPnl = new CommandPanel();
		initLayout();

		cmdPnl.getNode().setOnAction((ActionEvent event) -> {
			String text = cmdPnl.getNode().getText();
			
			if (text.startsWith("/")) {
				parseCommand(cmdPnl.getNode().getText().split(" "));
			}
			
			cmdPnl.getNode().setText("");
		});

		rollBtn.getNode().setOnAction((ActionEvent event) -> {
			infoPnl.print("Rolling Dice.");
		});
	}
	
	public void initLayout() {
		VBox right = new VBox();
		right.getChildren().addAll(infoPnl.getNode(), rollBtn.getNode(), cmdPnl.getNode());
		
		root = new HBox();
		root.getChildren().addAll(board.getNode(), right);
	}
	
	public HBox getNode() {
		return root;
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
	 * Binds shortcut CTRL+R key combination to the roll dice button.
	 */
	public void setRollDiceAccelarator() {
		Scene scene = rollBtn.getNode().getScene();
		if (scene == null) {
			throw new IllegalArgumentException("Roll Dice Button not attached to a scene.");
		}
		
		scene.getAccelerators().put(
			new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN),
			new Runnable() {
				@Override
				public void run() {
					rollBtn.getNode().fire();
				}
			}
		);
	}
}
