package game;

import constants.GameConstants;
import game_engine.Player;
import interfaces.ColorParser;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * This class represents the panel that displays player info at the top/bottom of the board.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class PlayerPanel extends HBox implements ColorParser {
	/**
	 * This class represents labels that stores player info.
	 */
	private static class PlayerInfo extends Label {
		public PlayerInfo(String string) {
			super(string);
			initStyle();
		}
		
		public void initStyle() {
			setFont(Font.loadFont(GameConstants.getFontInputStream(), GameConstants.FONT_SIZE_PLAYER_PANEL));
			setTextFill(Color.WHITE);
			setWrapText(true);
		}
	}
	
	private Player player;
	private PlayerInfo playerName;
	private PlayerInfo playerScore;
	private PlayerInfo playerColor;
	
	public PlayerPanel(double width, Player player) {
		super();
		this.player = player;
		style(width);
		initComponents();
		initLayout();
	}
	
	private void style(double width) {
		setMinSize(width, GameConstants.getPlayerPanelHeight());
		setAlignment(Pos.CENTER);
		setSpacing(GameConstants.getPlayerLabelSpacing());
	}
	
	private void initComponents() {
		playerName = new PlayerInfo("Name: " + player.getName());
		playerScore = new PlayerInfo("Score: " + player.getScore());
		
		// TODO put a checker color beside instead of text, makes things more intuitive.
		playerColor = new PlayerInfo("Color: " + parseColor(player.getColor()));
	}
	
	private void initLayout() {
		getChildren().addAll(playerName, playerColor, playerScore);
	}
	
	public void setPlayerName(Player player, String name) {
		player.setName(name);
		playerName.setText("Name: " + player.getName());
	}
	
	public void setPlayerScore(Player player, int score) {
		player.setScore(score);
		playerScore.setText("Score: " + player.getScore());
	}
	
	public void reset() {
		playerName.setText("Name: " + player.getName());
		playerScore.setText("Score: " + player.getScore());
		playerColor.setText("Color: " + parseColor(player.getColor()));
	}
}
