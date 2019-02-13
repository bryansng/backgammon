package game_engine;

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
	
	public Player(String name, double score, Color color) {
		this.name = name;
		this.score = score;
		this.color = color;
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
