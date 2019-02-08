package game_engine;

/**
 * This class represents the player in the Backgammon game.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Player {
	enum Colour {NONE, BLACK, WHITE};
	
	private String name;
	private double score;
	private Colour colour;
	
	public Player() {
		name = "";
		score = 0;
		colour = Colour.NONE;
	} 
	
	public Player(String name, double score, Colour colour) {
		this.name = name;
		this.score = score;
		this.colour = colour;
	}
	
	public String getName() {
		return name;
	}
	
	public double getScore() {
		return score;
	}
	
	public Colour getColour() {
		return colour;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public void setColour(Colour colour) {
		this.colour = colour;
	}
} 
