package game_engine;

import javafx.scene.control.TextArea;

/**
 * A TextArea that displays game information
 * with settings
 * @author @LxEmily
 *
 */

public class InfoPanel {
	public TextArea infoPanel;
	
	private final double HEIGHT = 500;
	
	public InfoPanel() {
		setInfoPanel(new TextArea("> " + "Welcome to Backgammon!\n"));	
		infoPanel.setMinHeight(HEIGHT);
		infoPanel.setEditable(false);
		infoPanel.setWrapText(true);
	}
	
	public void setInfoPanel(TextArea InfoPanel) {
		this.infoPanel = InfoPanel;
	}
	
	public TextArea getInfoPanel() {
		return infoPanel;
	}
}
