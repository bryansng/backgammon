package game_engine;

import constants.MessageType;
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
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * 
 */
public class InfoPanel extends TextArea {
	public InfoPanel() {
		super();
		setPrefHeight(Settings.getHalfBoardSize().getHeight());
		setEditable(false);
		setWrapText(true);
		setFocusTraversable(false);
		print("Welcome to Backgammon!");
		print("Roll the die to decide which player begins.");
		print("Or enter \"/help\" below for a list of possible commands.");
	}
	
	/**
	 * Prints the given text to the information panel.
	 * @param text - string to be printed
	 * @param mtype - message type, (i.e., error or system message) 
	 */
	public void print(String text, MessageType mtype) {
		String type = "";
		switch (mtype) {
			case SYSTEM:
				type = "[System] ";
				break;
			case ERROR:
				type = "[Error] ";
				break;
			case CHAT:
				break;
		}
		appendText("> " + type + text + "\n");
	}
	public void print(String text) {
		print(text, MessageType.SYSTEM);
	}
	
	/**
	 * Print empty row to information panel.
	 * @param times, number of times to print newline.
	 */
	public void printNewline(int times) {
		for (int i = 0; i < times; i++) {
			appendText("\n");
		}
	}
	
	/**
	 * Saves everything on the information panel to a text file.
	 */
	public void saveToFile() {
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
		print("Game log saved to backgammonn.txt");	
	}
}
