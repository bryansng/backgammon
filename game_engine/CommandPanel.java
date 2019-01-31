package game_engine;

import javafx.scene.control.TextArea;

/**
 * A TextArea that takes in player input
 * with settings
 * @author @LxEmily
 *
 */

public class CommandPanel {
	private TextArea commandPanel;
	
	public CommandPanel () {
		setCommandPanel(new TextArea());
		commandPanel.setWrapText(true);
		commandPanel.setPromptText("Player inputs text here, then hit Enter\n");
	}	
	
	public TextArea getCommandPanel() {
		return commandPanel;
	}
	
	public void setCommandPanel(TextArea commandPanel) {
		this.commandPanel = commandPanel;
	}
}
