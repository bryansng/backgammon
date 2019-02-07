package game_engine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);	// calls start method.
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		MainController root = new MainController();
		
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("Backgammon");
		//stage.setOnCloseRequest(onExitCheck);
		stage.show();
		
		// these must be set only after stage is shown.
		root.setRollDiceAccelarator();
		root.requestFocus();
	}
}
