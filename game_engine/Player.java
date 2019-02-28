package game_engine;

import constants.PlayerPerspectiveFrom;
import game.Home;
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
	private int score;
	private Color color;
	private Home home;
	private PlayerPerspectiveFrom pov;
	
	public Player(String name, int score, Color color, Home home, PlayerPerspectiveFrom pov) {
		this.name = name;
		this.score = score;
		this.color = color;
		this.home = home;
		this.pov = pov;
	}
	
	public Player getPlayer() {
		return this;
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
	
	public Home getHome() {
		return home;
	}
	
	public void setHome(Home home) {
		this.home = home;
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
} 
