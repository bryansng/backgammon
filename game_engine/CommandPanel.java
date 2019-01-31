package game_engine;

import javafx.scene.control.TextField;

public class CommandPanel {
	private TextField cmdPnl;

	public CommandPanel() {
		cmdPnl = new TextField();
	}
	
	public TextField getNode() {
		return cmdPnl;
	}
}
