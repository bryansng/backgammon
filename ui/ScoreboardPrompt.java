package ui;

import constants.GameConstants;
import game_engine.Player;
import game_engine.Settings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Class that handles layout of components in a dialog prompt.
 * Layout is made to look like a score board.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class ScoreboardPrompt extends GridPane {
	// Display fonts.
	Font score = Font.loadFont(GameConstants.getFontInputStream(true, true), 150);
	Font title = Font.loadFont(GameConstants.getFontInputStream(), 50);
	Font names = Font.loadFont(GameConstants.getFontInputStream(), 15);

	// Labels for player colors + total games.
	Labels bColor = new Labels("", getCheckerImg("black"));;
	Labels wColor = new Labels("", getCheckerImg("white"));;
	Labels totalGamesLabel = new Labels("MATCH\nTO");
	
	// TextFields for player names + total games.
	TextFields bNameField;
	TextFields wNameField;	
	TextFields totalGames;
	
	// Labels for player names.
	Labels bNameLabel;
	Labels wNameLabel;
	
	// ScoreCards to display player and match score.
	ScoreCard bScore;
	ScoreCard wScore;
	ScoreCard mScore;
	
	/**
	 * Constructs a GridPane with a given style, depending on context.
	 */
	public ScoreboardPrompt() {
		initStyle();
		addStartComponents();
	}
	public ScoreboardPrompt(Player bPlayer, Player wPlayer) {
		initStyle();
		addEndComponents(bPlayer, wPlayer);
	}
	
	public void initStyle() {
		setAlignment(Pos.CENTER);
		setVgap(5);
		setHgap(10);
	}
	
	/**
	 * Initializes and adds components of promptStartGame().
	 */
	public void addStartComponents() {
		// TextFields for player names + total games.
		bNameField = new TextFields("Default: Tea", false);
		wNameField = new TextFields("Default: Cup", false);
		totalGames = new TextFields("11", true);
		
		// Garbage collection for unused variables in this prompt.
		bNameLabel = null;
		wNameLabel = null;
		
		// ScoreCards to display initial player score.
		bScore = new ScoreCard("0", false);
		wScore = new ScoreCard("0", false);
		mScore = null;
		
		// Add components to pane
		add(bColor, 0, 0);
		add(bNameField, 1, 0);	
		add(bScore, 0, 1, 2, 1);
		add(totalGamesLabel, 2, 0);
		add(totalGames, 2, 1);
		add(wColor, 3, 0);
		add(wNameField, 4, 0);	
		add(wScore, 3, 1, 2, 1);
		
		// Ensures components are centered. 
		centering(totalGamesLabel, bScore, totalGames, wScore);
	}
	
	/**
	 * Initializes and adds components of onGameOver().
	 * @param bPlayer
	 * @param wPlayer
	 */
	public void addEndComponents(Player bPlayer, Player wPlayer) {
		// Garbage collection for unused variables in this prompt.
		bNameField = null;
		wNameField = null;
		totalGames = null;
		
		// Labels for player names.
		bNameLabel = new Labels(bPlayer.getName());
		wNameLabel = new Labels(wPlayer.getName());

		// Updated ScoreCards for players and current match.
		bScore = new ScoreCard(Integer.toString(bPlayer.getScore()), false);
		wScore = new ScoreCard(Integer.toString(wPlayer.getScore()), false);
		mScore = new ScoreCard(Integer.toString(Settings.TOTAL_GAMES_IN_A_MATCH), true);
				
		// Add components to pane
		add(bColor, 0, 0);
		add(bNameLabel, 1, 0);	
		add(bScore, 0, 1, 2, 1);
		add(totalGamesLabel, 2, 0);
		add(mScore, 2, 1);
		add(wColor, 3, 0);
		add(wNameLabel, 4, 0);	
		add(wScore, 3, 1, 2, 1);
		
		// Ensures components are centered. 
		centering(totalGamesLabel, bScore, mScore, wScore);
	}
	
	/**
	 * Centers certain components in the grid pane.
	 */
	private void centering(Labels totalGamesLabel, ScoreCard bScore, TextField totalGames, ScoreCard wScore) {		
		setHalignment(totalGamesLabel, HPos.CENTER);
		setHalignment(bScore, HPos.CENTER);
		setHalignment(wScore, HPos.CENTER);
		setHalignment(totalGames, HPos.CENTER);
		setValignment(totalGames, VPos.CENTER);
	}
	private void centering(Labels totalGamesLabel, ScoreCard bScore, ScoreCard mScore, ScoreCard wScore) {		
		setHalignment(totalGamesLabel, HPos.CENTER);
		setHalignment(bScore, HPos.CENTER);
		setHalignment(wScore, HPos.CENTER);
		setHalignment(mScore, HPos.CENTER);
		setValignment(mScore, VPos.CENTER);
	}
	
	/**
	 * Accesses contents of TextFields in this class.
	 * @param string determines which TextField
	 * @return contents of TextFields
	 */
	public String getPlayerInput(String string) {
		String contents = "";
		if (string.equals("black"))
			contents = bNameField.getText();
		else if (string.equals("white"))
			contents = wNameField.getText();
		else if (string.equals("score"))
			contents = totalGames.getText();
		return contents;
	}	
	
	/**
	 * Inner class to customize labels.
	 * 		- Constructor takes in string and ImageView to initialize.
	 */
	private class Labels extends Label {
		private Labels(String string) {
			super(string);
			initStyle();
		}
		
		private Labels(String string, ImageView img) {
			super(string, img);
			initStyle();
		}
		
		private void initStyle() {
			setFont(title);	
			setPadding(new Insets(5));
			setTextAlignment(TextAlignment.CENTER);
			setAlignment(Pos.CENTER);
			setMaxWidth(Double.MAX_VALUE);
		}
	}
	/**
	 * Method to customize image labels for checkers.
	 * @param string determines color of checkers
	 * @return ImageView of the checker
	 */
	private ImageView getCheckerImg(String string) {
		return new ImageView(this.getClass().getResource("/game/img/checkers/" 
														+ string + "_checkers.png").toString());
	}
	
	/**
	 * Inner class to customize TextField.
	 *		- Constructor takes in string to initialize prompt text.
	 */
	private class TextFields extends TextField {
		private TextFields(String string, boolean center) {
			super();
			setPromptText(string);
			setFont(names);
			setPadding(new Insets(5));
			setStyle("-fx-text-fill: #424949");
			setPrefWidth(GameConstants.getScreenSize().getWidth() * 0.07);
			if (center) setCenter();
		}
		
		// TextField for total games.
		private void setCenter() {
			setFont(score);
			setAlignment(Pos.CENTER);
			setBackground(GameConstants.getScoreboardImage("black"));
			setPrefHeight(GameConstants.getScreenSize().getHeight() * 0.3);
			setPrefWidth(GameConstants.getScreenSize().getWidth() * 0.13);
			setStyle("-fx-text-fill: LIGHTGRAY");
		}
	}	
	
	/**
	 * Inner class to make a score card.
	 * @author admin
	 *
	 */
	private class ScoreCard extends VBox {
		TextScore score;
		
		private ScoreCard(String string, boolean matchCard) {
			score = new TextScore(string);	
			getChildren().addAll(score);
			initStyle(matchCard);
		}
		
		private void initStyle(boolean matchCard) {
			setAlignment(Pos.CENTER);
			setPrefHeight(GameConstants.getScreenSize().getHeight() * 0.3);
			setPrefWidth(GameConstants.getScreenSize().getWidth() * 0.13);
			if (matchCard) setBackground(GameConstants.getScoreboardImage("black"));
			else setBackground(GameConstants.getScoreboardImage("white"));
		}
	}
	/**
	 * Inner class to customize text within a score board
	 */
	private class TextScore extends Text {
		private TextScore(String string) {
			super(string);
			setFont(score);
			setAlignment(Pos.CENTER);
		}
	}
}
