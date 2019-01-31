package game_engine;

import javafx.scene.control.Button;

/**
 * A button to roll the die
 * with settings
 * @author @LxEmily
 *
 */

public class RollDie extends Button {
	
	public RollDie () {
		super("Roll Die");
		setMinHeight(40);
		setMaxWidth(Double.MAX_VALUE);
	}
}
