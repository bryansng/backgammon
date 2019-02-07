package game_engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
	public static void main(String[] args) {
		launch(args);	// calls start method.
	}
	
	@Override
	public void start(Stage stage) throws Exception {
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
		FileInputStream input = null;
		try {
			input = new FileInputStream("src/img/icon/icon.png");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		stage.getIcons().add(new Image(input));
	}
}