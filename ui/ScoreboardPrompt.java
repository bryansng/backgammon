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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
	private Font score = Font.loadFont(GameConstants.getFontInputStream(true, true), 150);
	private Font match = Font.loadFont(GameConstants.getFontInputStream(), 50);
	private Font label = Font.loadFont(GameConstants.getFontInputStream(), 20);
	private Font field = Font.loadFont(GameConstants.getFontInputStream(), 16);

	// Labels for player colors + total games.
	private Labels bColor = new Labels("", getCheckerImg("black"), false);
	private Labels wColor = new Labels("", getCheckerImg("white"), false);
	private Labels totalGamesLabel = new Labels("MATCH\nTO", true);
	
	// TextFields for player names + total games.
	private TextFields bNameField, wNameField, totalGames;
	
	// Labels for player names.
	private Labels bNameLabel, wNameLabel;
	
	// NameCards to display player colors and names.
	private NameCard black, white;
	
	// ScoreCards to display player and match score.
	private ScoreCard bScore, wScore, mScore;
	
	private boolean isForStart;
	
	/**
	 * Constructs a GridPane with a given style, 
	 * depending on context (start/end game).
	 * Note: End of game requires passing in the correct players for their scores.
	 */
	public ScoreboardPrompt() {
		initStyle();
		initStartComponents();
		isForStart = true;
		addComponents();
		centerComponents();
	}
	public ScoreboardPrompt(Player bPlayer, Player wPlayer) {
		initStyle();
		initEndComponents(bPlayer, wPlayer);
		isForStart = false;
		addComponents();
		centerComponents();
	}
	
	/**
	 * Styles the grid pane.
	 */
	private void initStyle() {
		setAlignment(Pos.CENTER);
		setVgap(5);
		setHgap(10);
	}
	
	/**
	 * Initializes and adds components of promptStartGame().
	 */
	private void initStartComponents() {
		// TextFields for player names + total games.
		bNameField = new TextFields("Default: Tea", false);
		wNameField = new TextFields("Default: Cup", false);
		totalGames = new TextFields("11", true);
		
		// Name  for player names + colors
		black = new NameCard(bColor, bNameField);
		white = new NameCard(wColor, wNameField);
		
		// Garbage collection for unused variables in this prompt.
		bNameLabel = null;
		wNameLabel = null;
		
		// ScoreCards to display initial player score.
		bScore = new ScoreCard("0", false);
		wScore = new ScoreCard("0", false);
		mScore = null;
	}
	
	/**
	 * Initializes and adds components of onGameOver().
	 * Note: End of game requires passing in the correct players.
	 */
	private void initEndComponents(Player bPlayer, Player wPlayer) {
		// Garbage collection for unused variables in this prompt.
		bNameField = null;
		wNameField = null;
		totalGames = null;
		
		// Labels for player names.
		bNameLabel = new Labels(bPlayer.getName(), false);
		wNameLabel = new Labels(wPlayer.getName(), false);
		
		// NameCards for player colors and names.
		black = new NameCard(bColor, bNameLabel);
		white = new NameCard(wColor, wNameLabel);

		// Updated ScoreCards for players and current match.
		bScore = new ScoreCard(Integer.toString(bPlayer.getScore()), false);
		wScore = new ScoreCard(Integer.toString(wPlayer.getScore()), false);
		mScore = new ScoreCard(Integer.toString(Settings.TOTAL_GAMES_IN_A_MATCH), true);
	}

	/**
	 * Adds components to grid pane.
	 */
	private void addComponents() {
		add(black, 0, 0);
		add(bScore, 0, 1);
		add(totalGamesLabel, 1, 0);
		if (isForStart)
			add(totalGames, 1, 1);
		else
			add(mScore, 1, 1);
		add(white, 2, 0);
		add(wScore, 2, 1);
	}
	
	/**
	 * Centers components in the grid pane.
	 */
	private void centerComponents() {
		setHalignment(totalGamesLabel, HPos.CENTER);
		setHalignment(bScore, HPos.CENTER);
		setHalignment(wScore, HPos.CENTER);
		if (isForStart) {
			setHalignment(totalGames, HPos.CENTER);
			setValignment(totalGames, VPos.CENTER);
		} else {
			setHalignment(mScore, HPos.CENTER);
			setValignment(mScore, VPos.CENTER);
		}
	}
	
	/**
	 * Accesses contents of TextFields in this class.
	 * @param string determining which TextField
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
	 * Constructor takes in
	 * 		- a string to initialize display label.
	 * 		- an ImageView to initialize display checker colors.
	 * 		- a boolean isMatch to determine if it's a match label.
	 */
	private class Labels extends Label {
		private Labels(String string, boolean isMatch) {
			super(string);
			initStyle(isMatch);
		}
		
		private Labels(String string, ImageView img, boolean isMatch) {
			super(string, img);
			initStyle(isMatch);
		}
		
		private void initStyle(boolean isMatch) {
			setFont(label);	
			setPadding(new Insets(5));
			setWrapText(true);
			setMaxWidth(Double.MAX_VALUE);
			
			if (isMatch) {
				setFont(match);	
				setTextAlignment(TextAlignment.CENTER);
				setAlignment(Pos.CENTER);
			}
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
	 * Constructor takes in
	 * 		- string to initialize prompt text.
	 * 		- boolean isMatch to determine if it's for total number of games.
	 */
	private class TextFields extends TextField {
		private TextFields(String string, boolean isMatch) {
			super();
			setPromptText(string);
			setFont(field);
			setPadding(new Insets(5));
			setStyle("-fx-text-fill: #424949");
			setMaxWidth(Double.MAX_VALUE);
			if (isMatch) setCenter();
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
	 * Inner class to make a name card, 
	 * consisting of player colors and names.
	 * 		- player names are TextFields/Labels, depending on start/end game.
	 */
	private class NameCard extends HBox {
		private NameCard(Labels color, Labels name) {
			getChildren().addAll(color, name);
			initStyle();
		}
		private NameCard(Labels color, TextFields name) {
			getChildren().addAll(color, name);
			initStyle();
		}
		
		private void initStyle() {
			setMinWidth(GameConstants.getScreenSize().getWidth() * 0.13);
			setMaxWidth(GameConstants.getScreenSize().getWidth() * 0.13);
			setMaxHeight(this.getMaxWidth() * 0.5);
			setAlignment(Pos.CENTER);
		}
	}
	
	/**
	 * Inner class to make a score card, consisting of a score against a background.
	 * Constructor takes in 
	 * 		- a string for the score.
	 * 		- a boolean isMatchCard to determine if it's a match card (background + text color).
	 */
	private class ScoreCard extends VBox {
		TextScore score;
		
		private ScoreCard(String string, boolean isMatchCard) {
			score = new TextScore(string);	
			getChildren().addAll(score);
			initStyle(isMatchCard);
		}
		
		private void initStyle(boolean isMatchCard) {
			setAlignment(Pos.CENTER);
			setMinHeight(GameConstants.getScreenSize().getHeight() * 0.3);
			setPrefWidth(GameConstants.getScreenSize().getWidth() * 0.13);
			setMaxWidth(GameConstants.getScreenSize().getWidth() * 0.13);
			if (isMatchCard) {
				setBackground(GameConstants.getScoreboardImage("black"));
				score.setFill(Color.LIGHTGRAY);
			}
			else setBackground(GameConstants.getScoreboardImage("white"));
		}
	}
	/**
	 * Inner class to customize text within a score board.
	 * 		- Constructor takes in a string to initialize the display text.
	 */
	private class TextScore extends Text {
		private TextScore(String string) {
			super(string);
			setFont(score);
			setAlignment(Pos.CENTER);
		}
	}
}
