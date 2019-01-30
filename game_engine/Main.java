package game_engine;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.event.ActionEvent;


public class Main extends Application {
	private TextArea infoPnl;
	
	public static void main(String[] args) {
		launch(args);	// calls start method.
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		initUI(stage);
	}
	
	private void initUI(Stage stage) {
		// Board.
		Board board = new Board();
		
		// Information Panel.
		infoPnl = new TextArea();
		infoPnl.setEditable(false);
		
		// Command Panel.
		TextField cmdPnl = new TextField(); 
		cmdPnl.setOnAction((ActionEvent event) -> {
			String text = cmdPnl.getText();
			
			if (text.equals("")) {
				printToInfoPanel("Error: Unknown Command.");
			}
			else {
				printToInfoPanel("Running command: " + text + ".");
			}
			
			cmdPnl.setText("");
		});
				
		// Rolling Dice button.
		Button rollBtn = new Button("Roll Dice");
		rollBtn.setOnAction((ActionEvent event) -> {
			printToInfoPanel("Rolling Dice.");
		});

		// Information panel + roll dice button + command panel.
		VBox right = new VBox();
		right.getChildren().addAll(infoPnl, rollBtn, cmdPnl);
		
		// Game + right (info panel + roll dice button + command panel)
		HBox main = new HBox();
		main.getChildren().addAll(board.getBoard(), right);
		
		// root Layout.
		HBox root = new HBox();
		root.getChildren().add(main);
		
		Scene scene = new Scene(root);
		//Scene scene = new Scene(root, Settings.getScreenWidth(), Settings.getScreenHeight()); // width, height.

		stage.setScene(scene);	// add scene with layout.
		stage.setTitle("Backgammon");
		
		stage.setResizable(false);
		stage.show();
		
		setRollDiceAccelarator(rollBtn);
	}
	
	/**
	 * Prints the given text to the information panel.
	 * @param text - string to be printed
	 */
	private void printToInfoPanel(String text) {
		// Appends text to information panel.
		infoPnl.appendText(text + "\n");
	}
	
	/**
	 * Binds shortcut CTRL+R key combination to the roll dice button.
	 * @param button roll dice button.
	 */
	private void setRollDiceAccelarator(Button button) {
		Scene scene = button.getScene();
		if (scene == null) {
			throw new IllegalArgumentException("Roll Dice Button not attached to a scene.");
		}
		
		scene.getAccelerators().put(
			new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN),
			new Runnable() {
				@Override
				public void run() {
					printToInfoPanel("Rolling Dice.");
				}
			}
		);
	}
}
