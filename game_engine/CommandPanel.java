package game_engine;

import javafx.scene.control.TextArea;

/**
 * A TextArea that takes in player input
 * with settings
 * @author @LxEmily
 *
 */

public class CommandPanel extends TextArea {
	
	public CommandPanel () {
		super();
		setWrapText(true);
		setPromptText("Player inputs text here, then hit Enter\n");
	}	
}
