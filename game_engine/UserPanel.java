package game_engine;

import javafx.scene.layout.HBox;

public class UserPanel extends HBox {
	public UserPanel(double width) {
		super();
		setPrefSize(width, Settings.getTopBottomHeight());
		setStyle("-fx-background-color: transparent;");
	}
}
