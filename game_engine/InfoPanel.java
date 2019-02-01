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
		super("> " + "Welcome to Backgammon!\n");
		setPrefHeight(Settings.getTopBottomHeight()*2 + Settings.getHalfBoardSize().getHeight());	
		setEditable(false);
		setWrapText(true);
		setFocusTraversable(false);
	}
	
	/**
	 * Prints the given text to the information panel.
	 * @param text - string to be printed
	 */
	public void print(String text) {
		// Appends text to information panel.
		appendText("> " + text + "\n");
	}
}
