package game_engine;

import javafx.scene.layout.VBox;

public class Home extends VBox {
	public Home() {
		super();
		double halfBoardHeight = Settings.getHalfBoardSize().getHeight();
		double pointWidth = Settings.getPointSize().getWidth();
		
		setPrefSize(pointWidth, halfBoardHeight);
	}
}
