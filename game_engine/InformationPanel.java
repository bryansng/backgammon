package game_engine;

import javafx.scene.control.TextArea;

public class InformationPanel {
	private TextArea infoPnl;
	
	public InformationPanel() {
		infoPnl = new TextArea();
		infoPnl.setEditable(false);
	}
	
	/**
	 * Prints the given text to the information panel.
	 * @param text - string to be printed
	 */
	public void print(String text) {
		// Appends text to information panel.
		infoPnl.appendText(text + "\n");
	}
	
	public TextArea getNode() {
		return infoPnl;
	}
}
