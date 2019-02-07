package game_engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javafx.collections.ObservableList;
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
			type = "[Error]";
		} else {
			type = "[System]";
		}
		appendText("> " + type + " " + text + "\n");
	}
	public void print(String text) {
		print(text, "system");
	}
	
	/**
	 * Outputs everything on the information panel to a text file
	 */
	public void txt() {
		ObservableList<CharSequence> paragraph = this.getParagraphs();
		Iterator<CharSequence> iterator = paragraph.iterator();
		try {
			BufferedWriter buffer = new BufferedWriter(new FileWriter(new File("backgammon.txt")));
			while(iterator.hasNext()) {
				CharSequence seq = iterator.next();
				buffer.append(seq);
				buffer.newLine();
			}
			buffer.flush();
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
