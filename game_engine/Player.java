package game_engine;

/**
 * Class that stores player information
 * Player related features will be expanded in sprint 2
 * @author @LxEmily
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
