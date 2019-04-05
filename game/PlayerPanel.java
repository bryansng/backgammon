package game;

import constants.GameConstants;
import game_engine.Player;
import game_engine.Settings;
import interfaces.ColorParser;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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
		public PlayerInfo(String string, ImageView icon) {
			super(string, icon);
			initStyle();
		}
		
		public void initStyle() {
			setFont(Font.loadFont(GameConstants.getFontInputStream(), GameConstants.FONT_SIZE_PLAYER_PANEL));
			setTextFill(Color.WHITE);
			setMaxWidth(GameConstants.getScreenSize().getWidth() * 0.15);
			setWrapText(true);
		}
		
		// used by instance variable playerColor.
		public Checker getChecker() {
			return (getGraphic() instanceof Checker) ? (Checker) getGraphic() : null;
		}
		
		// used by instance variable playerName.
		public Emoji getEmoji() {
			return (getGraphic() instanceof Emoji) ? (Emoji) getGraphic() : null;
		}
	}
	
	private Player player;
	private PlayerInfo playerName;
	private PlayerInfo playerColor;
	private PlayerInfo playerScore;
	private GameplayTimer timer;
	
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
		playerName = new PlayerInfo("", new Emoji());
		setPlayerName(player, player.getName());
		playerName.setContentDisplay(ContentDisplay.LEFT);
		playerName.setGraphicTextGap(10);
		
		playerColor = new PlayerInfo("", new Checker(player.getColor(), true));
		
		playerScore = new PlayerInfo("", null);
		setPlayerScore(player, player.getScore());
		playerScore.setFont(Font.loadFont(GameConstants.getFontInputStream(true, true), 24));
		
		timer = new GameplayTimer();
	}
	
	private void initLayout() {
		getChildren().addAll(playerName, playerColor, playerScore, timer);
	}
	
	public void setPlayerName(Player player, String name) {
		player.setName(name);
		playerName.setText(player.getName());
	}
	
	public void setPlayerScore(Player player, int score) {
		player.setScore(score);
		playerScore.setText(player.getScore() + " / " + Settings.TOTAL_GAMES_IN_A_MATCH);
	}
	
	public void updateTotalGames() {
		setPlayerScore(player, player.getScore());
	}
	
	public void highlightChecker() {
		playerColor.getChecker().setHighlightImage();
	}
	public void unhighlightChecker() {
		playerColor.getChecker().setNormalImage();
	}
	
	public Emoji getEmoji() {
		return playerName.getEmoji();
	}
	
	public GameplayTimer getTimer() {
		return timer;
	}
	
	public void reset() {
		setPlayerName(player, player.getName());
		playerName.getEmoji().reset();
		unhighlightChecker();
		setPlayerScore(player, player.getScore());
	}
	public void resetTimer() {
		timer.reset();
	}
}
