package game_engine;

import javafx.scene.control.Button;

/**
 * A button to roll the die
 * with settings
 * @author @LxEmily
 *
 */

public class RollDie {
	private Button button;
	
	public RollDie () {
		setButton(new Button("Roll Die"));
		setButtonSize(Double.MAX_VALUE, 40);
	}
	
	public void setButton (Button button) {
		this.button = button;
	} 
	
	public void setButtonSize(double width, double height) {
		button.setMinHeight(height);
		button.setMaxWidth(width);
	}
	
	public Button getButton () {
		return button;
	}
}
