package game_engine;

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
	private PlayerPerspectiveFrom pov;
	
	public Player(PlayerPerspectiveFrom pov) {
		this.name = Settings.getDefaultPlayerName(pov);
		this.score = 0;
		this.color = getColor(pov);
		this.pov = pov;
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
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void reset() {
		name = Settings.getDefaultPlayerName(pov);
		score = 0;
	}
} 
