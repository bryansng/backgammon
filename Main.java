import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;

import javafx.application.Application;
import javafx.stage.Stage;


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
		
		/*
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

		// Information panel + roll dice button.
		VBox right = new VBox();
		right.getChildren().addAll(infoPnl, rollBtn);
		
		// Game + right (info panel + roll dice button)
		HBox top = new HBox();
		top.getChildren().addAll(board.getShape(), right);
		
		// Top + Bottom.
		VBox main = new VBox();
		main.getChildren().addAll(top, cmdPnl);
		*/
		
		// root Layout.
		HBox root = new HBox();
		//root.getChildren().add(main);
		root.getChildren().add(board.getBoard());
		
		Scene scene = new Scene(root);
		//Scene scene = new Scene(root, Settings.getScreenWidth(), Settings.getScreenHeight()); // width, height.
		//scene.setFill(Color.TRANSPARENT);

		stage.setScene(scene);	// add scene with layout.
		stage.setTitle("Backgammon");
		
		//stage.initStyle(StageStyle.TRANSPARENT); // could be used to make it full screen.
		stage.setResizable(false);
		// stage.setFullScreen(true);
		stage.show();
		
		//setRollDiceAccelarator(rollBtn, infoPnl);
	}
	
	
	// Print string to information panel.
	private void printToInfoPanel(String text) {
		// Appends text to information panel.
		infoPnl.appendText(text + "\n");
	}
	
	
	// Bind shortcut CTRL+R key combination to roll dice.
	private void setRollDiceAccelarator(Button button, TextArea infoPnl) {
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
