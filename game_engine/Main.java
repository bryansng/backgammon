package game_engine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		initUI(stage);
	}
	
	public static void main (String[] args) {
		launch(args);
	}
	
	private void initUI(Stage stage) {
		
		// Components
		VBox board = new Board().getBoard();
		TextArea infoPanel = new InfoPanel();
		Button rollDie = new RollDie();
		TextArea commandPanel = new CommandPanel();
		
		// Pane
		GridPane layout = new Layout(board, infoPanel, rollDie, commandPanel);
				
		// Scene
		Scene scene = new Scene(layout);
		
		// User commands
		Listener listen = new Listener();
		listen.rollDieListener(rollDie, infoPanel, scene);
		listen.commandPanelListener(commandPanel, infoPanel);
		
		// Stage
		stage.setTitle("Backgammon");
		stage.setScene(scene);
		stage.show();
	}

}
