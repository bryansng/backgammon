package game_engine;

import javafx.scene.layout.VBox;

public class Jail extends VBox {
	public Jail() {
		super();
		double halfBoardHeight = Settings.getHalfBoardSize().getHeight();
		double pointWidth = Settings.getPointSize().getWidth();
		
		// the jail for the checkers.
		setPrefSize(pointWidth, halfBoardHeight);
		setStyle("-fx-background-color: transparent;");
	}
}
