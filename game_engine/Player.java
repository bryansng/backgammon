package game_engine;

import constants.PlayerPerspectiveFrom;
import javafx.scene.paint.Color;

/**
 * This class represents the player in the Backgammon game.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Player {
	private String name;
	private double score;
	private Color color;
	private PlayerPerspectiveFrom pov;
	
	public Player(String name, double score, Color color, PlayerPerspectiveFrom pov) {
		this.name = name;
		this.score = score;
		this.color = color;
		this.pov = pov;
	}
	
	public Player getPlayer() {
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public double getScore() {
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
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
} 
