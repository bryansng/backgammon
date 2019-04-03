package game;

import constants.GameConstants;
import game_engine.Player;
import interfaces.ColorParser;
import javafx.application.Platform;
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
		playerName = new PlayerInfo("Name: " + player.getName());
		playerScore = new PlayerInfo("Score: " + player.getScore());
		
		// TODO put a checker color beside instead of text, makes things more intuitive.
		playerColor = new PlayerInfo("Color: " + parseColor(player.getColor()));
		moveTimer = new PlayerInfo("Safe Time: " + timer.getSeconds());
		playerTimer = new PlayerInfo("Timer: " + player.formatTime());
	}
	
	private void initLayout() {
		getChildren().addAll(playerName, playerColor, playerScore, moveTimer, playerTimer);
	}
	
	public void setPlayerName(Player player, String name) {
		player.setName(name);
		playerName.setText("Name: " + player.getName());
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
				moveTimer = new PlayerInfo("Safe Time: " + timer.getSeconds());
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
				playerTimer = new PlayerInfo("Timer: " + player.formatTime());
				getChildren().add(4, playerTimer);
			}
		});
	}
	
	public void resetTimer() {
		timer.restartTimer();
	}
	
	public void reset() {
		playerName.setText("Name: " + player.getName());
		playerScore.setText("Score: " + player.getScore());
		playerColor.setText("Color: " + parseColor(player.getColor()));
	}
}
