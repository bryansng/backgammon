package game_engine;

import java.util.concurrent.TimeUnit;
import constants.GameConstants;
import constants.PlayerPerspectiveFrom;
import interfaces.ColorPerspectiveParser;
import javafx.scene.paint.Color;

/**
 * This class represents the player in the Backgammon game.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Player implements ColorPerspectiveParser {
	private String name;
	private int score;
	private Color color;
	private boolean hasCube;
	private PlayerPerspectiveFrom pov;
	private long time; // in seconds
	
	public Player(PlayerPerspectiveFrom pov) {
		this.pov = pov;
		this.color = getColor(pov);
		reset();
	}
	
	public void reset() {
		name = Settings.getDefaultPlayerName(pov);
		hasCube = false;
		if (GameConstants.FORCE_TEST_DEAD_CUBE)
			score = Settings.TOTAL_GAMES_IN_A_MATCH-3;
		else if (color.equals(Settings.getTopPerspectiveColor()) && (GameConstants.FORCE_TEST_CRAWFORD_RULE || GameConstants.FORCE_TEST_AFTER_CRAWFORD_RULE))
			score = Settings.TOTAL_GAMES_IN_A_MATCH-7;
		else if (color.equals(Settings.getBottomPerspectiveColor()) && GameConstants.FORCE_TEST_CRAWFORD_RULE)
			score = Settings.TOTAL_GAMES_IN_A_MATCH-3;
		else if (color.equals(Settings.getBottomPerspectiveColor()) && GameConstants.FORCE_TEST_AFTER_CRAWFORD_RULE)
			score = Settings.TOTAL_GAMES_IN_A_MATCH-1;
		else
			score = 0;
		time = 2280;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}
	
	public Color getColor() {
		return color;
	}
	
	public PlayerPerspectiveFrom getPOV() {
		return pov;
	}
	
	public boolean hasCube() {
		return hasCube;
	}
	
	public long getTime() {
		return time;
	}
	
	public String formatTime() {
		long minutes = TimeUnit.SECONDS.toMinutes(time);
		long seconds = time - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(time));
		return String.format("%02d:%02d", minutes, seconds); // Print 2 decimal places
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setHasCube(boolean hasCube) {
		this.hasCube = hasCube;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
} 
