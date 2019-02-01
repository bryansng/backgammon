package game_engine;

import javafx.scene.control.TextArea;

public class InfoPanel extends TextArea {
	public InfoPanel() {
		super();
		setEditable(false);
	}
	
	/**
	 * Prints the given text to the information panel.
	 * @param text - string to be printed
	 */
	public void print(String text) {
		// Appends text to information panel.
		appendText(text + "\n");
	}
}
