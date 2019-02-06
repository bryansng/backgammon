package game_engine;

import javafx.scene.control.TextField;

/**
 * A TextArea that takes in player input.
 * 
 * @author @LxEmily
 * @coauthor Bryan Sng
 * 
 */
public class CommandPanel extends TextField {
	public CommandPanel () {
		super();
		setPromptText("Player inputs text here, then hit Enter\n");
		setMinHeight(Settings.getTopBottomHeight());
	}
}
