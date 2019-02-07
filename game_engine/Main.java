package game_engine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Stage stage;
	
	public static void main(String[] args) {
		launch(args);	// calls start method.
	}
	
	@Override
	public void start(Stage pStage) throws Exception {
		setStage(pStage);
		MainController root = new MainController();
		
		Scene scene = new Scene(root);

		pStage.setScene(scene);
		pStage.setTitle("Backgammon");
		pStage.show();
		
		// these must be set only after stage is shown.
		root.setRollDiceAccelarator();
		root.requestFocus();
	}
	
	public static Stage getStage () {
		return stage;
	}
	
	private void setStage (Stage newStage) {
		Main.stage = newStage;
	}
}
