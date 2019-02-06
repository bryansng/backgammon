package game_engine;

import javafx.scene.control.TextArea;

/**
 * A TextArea that displays game information.
 * 
 * @author @LxEmily
 * @coauthor Bryan Sng
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
