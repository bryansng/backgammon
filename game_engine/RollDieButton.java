package game_engine;

import javafx.scene.control.Button;

/**
 * A button to roll the die
 * with settings
 * @author @LxEmily
 * @coauthor Bryan Sng
 */
public class RollDieButton extends Button {
	public RollDieButton () {
		super("Roll Die");
		setMinHeight(Settings.getTopBottomHeight());
		setMaxWidth(Double.MAX_VALUE);
	}
}
