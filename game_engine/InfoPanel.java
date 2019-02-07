package game_engine;

import javafx.scene.control.TextArea;

/**
 * A TextArea that displays game information.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * 
 */
public class InfoPanel extends TextArea {
	public InfoPanel() {
		super();
		setPrefHeight( Settings.getHalfBoardSize().getHeight());
		setEditable(false);
		setWrapText(true);
		setFocusTraversable(false);
		print("Welcome to Backgammon!");
	}
	
	/**
	 * Prints the given text to the information panel.
	 * @param text - string to be printed
	 * @param type - type of string, (i.e., error or system message) 
	 */
	public void print(String text, String type) {
		type.toLowerCase();
		if (type.equals("error")) {
			type = "[Error] ";
		} else if (type.equals("chat")) {
			type = "";
		} else {
			type = "[System] ";
		}
		appendText("> " + type + text + "\n");
	}
	public void print(String text) {
		print(text, "system");
	}
}
