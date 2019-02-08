package game_engine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
/**
 * Class that displays player info on the top/bottom of board
 * Player related features will be expanded in sprint 2
 * @author Bryan Sng
 * @author @LxEmily
 *
 */
public class PlayerPanel extends GridPane {
	
	/**
	 * Class that stores player info as Labels
	 * @author @LxEmily
	 */
	public static class PlayerLabel extends Label {
		
		public PlayerLabel(String string) {
			super(string);
			initStyle();
		}
		
		public void initStyle() {
			setFont(new Font("Consolas", 30));	
			setWrapText(true);
			setMaxWidth(Double.MAX_VALUE);
		}
	}
	
	private Player player;	
	private PlayerLabel playerName;
	private PlayerLabel playerScore;
	private PlayerLabel playerColour;
	
	public PlayerPanel(double width, Player player) {
		super();
		parsePlayer(player);
		initStyle(width);
		initComponents();
		initLayout();
	}
	
	public void parsePlayer(Player player) {
		this.player = player;
	}
	
	public void initStyle(double width) {
		setPrefSize(width, Settings.getTopBottomHeight());
		setStyle("-fx-background-color: transparent;");
		setPadding(new Insets(15));
		setHgap(50);
		setAlignment(Pos.CENTER);
	}
	
	public void initComponents() {			
		playerName = new PlayerLabel("PLAYER: " + player.getName());
		playerScore = new PlayerLabel("SCORE: " + Double.toString(player.getScore()));
		playerColour = new PlayerLabel("COLOUR: " + player.getColour().name());		
	}
	
	public void initLayout() {
		add(playerName, 0, 0);
		add(playerColour, 1, 0);
		add(playerScore, 2 , 0);
	}
}
