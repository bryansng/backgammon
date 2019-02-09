package game_engine;

import java.io.InputStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This class runs the entire application.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Main extends Application {
	private static Stage stage;
	
	public static void main(String[] args) {
		launch(args);	// calls start method.
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Main.stage = stage;
		MainController root = new MainController();
		
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("Backgammon");
		stage.show();
		setStageIcon(stage);
		
		// these must be set only after stage is shown.
		root.setRollDiceAccelarator();
		root.requestFocus();
	}
	
	/**
	 * Set the application's icon.
	 * @param stage, the stage of the application.
	 */
	public void setStageIcon(Stage stage) {
		InputStream input = getClass().getResourceAsStream("img/icon/icon.png");
		stage.getIcons().add(new Image(input));
	}
	
	/**
	 * Returns the stage of the application.
	 * Used to create the quit prompt.
	 * 
	 * @return the main stage.
	 */
	public static Stage getStage () {
		return stage;
	}
}
