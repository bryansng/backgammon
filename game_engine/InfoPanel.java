package game_engine;

import javafx.scene.control.TextArea;

/**
 * A TextArea that displays game information
 * with settings
 * @author @LxEmily
 *
 */

public class InfoPanel extends TextArea {
	
	private final double HEIGHT = 500;
	
	public InfoPanel() {
		super("> " + "Welcome to Backgammon!\n");	
		setMinHeight(HEIGHT);
		setEditable(false);
		setWrapText(true);
	}
}
