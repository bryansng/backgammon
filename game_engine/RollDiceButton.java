package game_engine;

import javafx.scene.control.Button;

public class RollDiceButton {
	private Button rollBtn;
	
	public RollDiceButton() {
		rollBtn = new Button("Roll Dice");
	}
	
	public Button getNode() {
		return rollBtn;
	}
}
