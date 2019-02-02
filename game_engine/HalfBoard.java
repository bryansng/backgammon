package game_engine;

import javafx.scene.layout.BorderPane;

public class HalfBoard extends BorderPane {
	public HalfBoard() {
		super();
		double halfBoardWidth = Settings.getHalfBoardSize().getWidth();
		double halfBoardHeight = Settings.getHalfBoardSize().getHeight();
		
		setPrefSize(halfBoardWidth, halfBoardHeight);
		setStyle("-fx-background-color: forestgreen;");
	}
}
