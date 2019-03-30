package game;

import constants.GameConstants;
import constants.PlayerPerspectiveFrom;
import constants.Quadrant;
import game_engine.Player;
import game_engine.Settings;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * This class represents the quadrant of pip number labels and pips of the Board in the Backgammon game.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class BoardQuadrant extends VBox {
	private int startRange, endRange;	// NOTE: These are zero-based index.
	private HBox setOfLabels, setOfPoints;
	private PlayerPerspectiveFrom pov;
	private Pip[] pips;
	private boolean isWhiteInner, isBlackInner;
	
	public BoardQuadrant(int startRange, int endRange, PlayerPerspectiveFrom pov, Quadrant quadrant, Pip[] pips) {
		this.startRange = startRange-1;
		this.endRange = endRange-1;
		this.pov = pov;
		this.pips = pips;
		specifyHomes(quadrant);
		initQuadrant();
		drawQuadrant(setOfLabels);
	}
	
	// loop through the quadrant's pip to search if there is a checker of color 'color'.
	public boolean hasCheckerColor(Color color) {
		boolean hasCheckerColor = false;
		for (int pipNum = startRange; pipNum <= endRange; pipNum++) {
			if (!pips[pipNum].isEmpty()) {
				if (pips[pipNum].topCheckerColorEquals(color)) {
					hasCheckerColor = true;
					break;
				}
			}
		}
		return hasCheckerColor;
	}
	
	// Has better pips to bear off if there are pips
	// that are not empty and has the same number as the dice result.
	public boolean hasBetterPipsToBearOff(Player pCurrent, int fromPip, int diceResult) {
		boolean hasBetter = false;
		
		// loop range, pips further away from home (inclusive) - fromPip (exclusive).
		if (isWhiteInner) {
			for (int pipNum = endRange; pipNum > fromPip; pipNum--) {
				if (!pips[pipNum].isEmpty() && pips[pipNum].topCheckerColorEquals(pCurrent.getColor())) {
					hasBetter = true;
					break;
				}
				/*
				if (!pips[pipNum].isEmpty() && pipNum == (diceResult-1)) {
				*/
			}
		} else if (isBlackInner) {
			for (int pipNum = startRange; pipNum < fromPip; pipNum++) {
				if (!pips[pipNum].isEmpty() && pips[pipNum].topCheckerColorEquals(pCurrent.getColor())) {
					hasBetter = true;
					break;
				}
				/*
				if (!pips[pipNum].isEmpty() && (GameConstants.NUMBER_OF_PIPS-pipNum) == (diceResult-1)) {
				*/
			}
		}
		
		return hasBetter;
	}
	
	public HBox getLabels() {
		return setOfLabels;
	}
	
	private void specifyHomes(Quadrant quadrant) {
		if (quadrant == Settings.getWhiteHomeQuadrant()) {
			isWhiteInner = true;
			isBlackInner = false;
		} else if (quadrant == Settings.getBlackHomeQuadrant()) {
			isBlackInner = true;
			isWhiteInner = false;
		}
	}

	/**
	 * Creates a VBox with the points and their labels.
	 */
	private void initQuadrant() {
		setOfLabels = createSetOfLabels();
		setOfPoints = createSetOfPoints();
	}
	
	/**
	 * Swaps its current pip number labels with that of newSetLabels.
	 * @param newSetLabels new pip number labels.
	 */
	public void drawQuadrant(HBox newSetLabels) {
		setOfLabels = newSetLabels;
		getChildren().clear();
		
		if (pov == PlayerPerspectiveFrom.BOTTOM)
			getChildren().addAll(setOfPoints, setOfLabels);
		else {
			getChildren().addAll(setOfLabels, setOfPoints);
		}
	}

	/**
	 * Creates a HBox with the labels within the range of startRange and endRange.
	 * @return HBox with the labels as children.
	 */
	private HBox createSetOfLabels() {
		HBox set = new HBox();
		set.setPrefSize(GameConstants.getHalfBoardSize().getWidth(), GameConstants.getPipNumberLabelHeight());
		
		// Handles the evenly distributed spacings between the points.
		set.setAlignment(Pos.CENTER);
		// why by 5 again?
		double spacing = (GameConstants.getHalfBoardSize().getWidth()-6*(GameConstants.getPipSize().getWidth())) / 5;
		set.setSpacing(spacing);
		
		set.setBackground(GameConstants.getGameImage());
		
		// If bottom of board, points are numbered from smallest to highest from right to left.
		// Else, from smallest to highest from left to right.
		if (pov == PlayerPerspectiveFrom.BOTTOM)
			for (int i = endRange; i >= startRange; i--) {
				set.getChildren().add(new PipNumberLabel(i+1));
			}
		else {
			for (int i = startRange; i <= endRange; i++) {
				set.getChildren().add(new PipNumberLabel(i+1));
			}
		}
		return set;
	}
	
	/**
	 * Creates a HBox with the points within the range of startRange and endRange.
	 * @return HBox with the points as children.
	 */
	private HBox createSetOfPoints() {
		HBox set = new HBox();
		set.setPrefSize(GameConstants.getHalfBoardSize().getWidth(), GameConstants.getPipSize().getHeight());
		
		// Handles the evenly distributed spacings between the points.
		set.setAlignment(Pos.CENTER);
		// why by 5 again?
		double spacing = (GameConstants.getHalfBoardSize().getWidth()-6*(GameConstants.getPipSize().getWidth())) / 5;
		set.setSpacing(spacing);
		
		// If bottom of board, points are numbered from smallest to highest from right to left.
		// Else, from smallest to highest from left to right.
		if (pov == PlayerPerspectiveFrom.BOTTOM)
			for (int i = endRange; i >= startRange; i--) {
				set.getChildren().add(pips[i]);
			}
		else {
			for (int i = startRange; i <= endRange; i++) {
				set.getChildren().add(pips[i]);
			}
		}
		return set;
	}
	
	public String toString() {
		return "Quad with: " + (startRange+1) + "-" + (endRange+1);
	}
}
