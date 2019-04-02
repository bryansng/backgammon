package ui;

import constants.GameConstants;
import game_engine.Settings;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * A TextField that takes in player input.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 * 
 */
public class CommandPanel extends TextField {
	private CommandHistory history;
	
	public CommandPanel () {
		super();
		history = new CommandHistory();
		
		setPromptText("Player inputs text here, then hit Enter\n");
		setMinHeight(GameConstants.getUIHeight());
		setFont(GameConstants.getFont());
		initListeners();
		redraw();
	}
	
	public void addHistory(String cmd) {
		history.addNewCommand(cmd);
	}
	
	// used to scroll up and down previous command histories.
	public void initListeners() {
		setOnKeyPressed((KeyEvent event) -> {
			if (event.getCode() == KeyCode.UP) {
				up();
			} else if (event.getCode() == KeyCode.DOWN) {
				down();
			}
		});
	}
	
	private void up() {
		String text = history.up(getText());
		setText(text);
		positionCaret(text.length());
	}
	
	private void down() {
		String text = history.down(getText());
		setText(text);
		positionCaret(text.length());
	}
	
	// checks if the given text is a command,
	// a command here starts with a '/'.
	public boolean isCommand(String text) {
		boolean isCommand = false;
		if (text.startsWith("/")) {
			isCommand = true;
		}
		return isCommand;
	}
	
	public void redraw() {
		if (Settings.DARK_THEME) {
			setBackground(GameConstants.getPanelImage());
			setStyle("-fx-text-inner-color: floralwhite;");
		}
	}
	
	public void reset() {
		history.reset();
		redraw();
	}
}
