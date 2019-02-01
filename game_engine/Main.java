package game_engine;

import javafx.scene.Scene;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
	public static void main(String[] args) {
		launch(args);	// calls start method.
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		initStage(stage);
	}
	
	private void initStage(Stage stage) {
		MainController root = new MainController();
		
		Scene scene = new Scene(root);

		stage.setScene(scene);	// add scene with layout.
		stage.setTitle("Backgammon");
		
		stage.setResizable(false);
		stage.show();
		
		// this must be set only after stage is shown.
		root.setRollDiceAccelarator();
	}
}
