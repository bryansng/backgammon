package game;

import constants.GameConstants;
import game_engine.Player;
import game_engine.Settings;
import interfaces.ColorParser;
import javafx.application.Platform;
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
	private PlayerInfo playerTimer;
	private PlayerInfo moveTimer;
	private Clock timer;
	
	public PlayerPanel(double width, Player player) {
		super();
		this.player = player;
		timer = new Clock();
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
		playerScore.setFont(Font.loadFont(GameConstants.getFontInputStream(true, true), 20));
		
		moveTimer = new PlayerInfo("Safe Time: " + timer.getSeconds(), null);
		playerTimer = new PlayerInfo("Timer: " + player.formatTime(), null);
	}
	
	private void initLayout() {
		getChildren().addAll(playerName, playerColor, playerScore, moveTimer, playerTimer);
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
	
	public void startMoveTimer() {
		getChildren().remove(moveTimer);
		timer.countdown(this, this, player);
	}
	
	public void updateMoveTimer() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getChildren().remove(moveTimer);
				moveTimer = new PlayerInfo("Safe Time: " + timer.getSeconds(), null);
				getChildren().add(3, moveTimer);
			}
		});
	}
	
	public void stopMoveTimer() {
		timer.stopTimer(player);
	}
	
	public void updatePlayerTimer() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getChildren().remove(playerTimer);
				playerTimer = new PlayerInfo("Timer: " + player.formatTime(), null);
				getChildren().add(4, playerTimer);
			}
		});
	}
	
	public void resetTimer() {
		timer.restartTimer();
	}
	
	public void reset() {
		setPlayerName(player, player.getName());
		playerName.getEmoji().reset();
		unhighlightChecker();
		setPlayerScore(player, player.getScore());
	}
}
