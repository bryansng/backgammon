package game_engine;

import constants.PlayerPerspectiveFrom;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BoardQuadrant extends VBox {
	private HBox setLabels, setPoints;
	
	public BoardQuadrant(int startRange, int endRange, PlayerPerspectiveFrom pov, Pip[] pips) {
		initQuadrant(startRange, endRange, pov, pips);
	}

	/**
	 * Creates a VBox with the points and their labels.
	 * @param startRange One-based index, starting index.
	 * @param endRange One-based index, ending index.
	 * @param pov - player's point of view. (i.e. TOP or BOTTOM).
	 * @return VBox with HBox of points and HBox of labels as children.
	 */
	private void initQuadrant(int startRange, int endRange, PlayerPerspectiveFrom pov, Pip[] pips) {
		setLabels = createSetOfLabels(startRange, endRange, pov);
		setPoints = createSetOfPoints(startRange, endRange, pov, pips);

		if (pov == PlayerPerspectiveFrom.BOTTOM)
			getChildren().addAll(setPoints, setLabels);
		else {
			getChildren().addAll(setLabels, setPoints);
		}
	}

	/**
	 * Creates a HBox with the labels within the range of startRange and endRange.
	 * @param startRange One-based index, starting index.
	 * @param endRange One-based index, ending index.
	 * @param pov - player's point of view. (i.e. TOP or BOTTOM).
	 * @return HBox with the labels as children.
	 */
	private HBox createSetOfLabels(int startRange, int endRange, PlayerPerspectiveFrom pov) {
		HBox set = new HBox();
		set.setPrefSize(Settings.getHalfBoardSize().getWidth(), Settings.getPipNumberLabelHeight());
		
		// Handles the evenly distributed spacings between the points.
		set.setAlignment(Pos.CENTER);
		// why by 5 again?
		double spacing = (Settings.getHalfBoardSize().getWidth()-6*(Settings.getPipSize().getWidth())) / 5;
		set.setSpacing(spacing);
		
		set.setStyle(Settings.getGameColour());
		
		// If bottom of board, points are numbered from smallest to highest from right to left.
		// Else, from smallest to highest from left to right.
		if (pov == PlayerPerspectiveFrom.BOTTOM)
			for (int i = endRange-1; i >= startRange-1; i--) {
				set.getChildren().add(new PipNumberLabel(i+1));
			}
		else {
			for (int i = startRange-1; i < endRange; i++) {
				set.getChildren().add(new PipNumberLabel(i+1));
			}
		}
		return set;
	}
	
	/**
	 * Creates a HBox with the points within the range of startRange and endRange.
	 * @param startRange One-based index, starting index.
	 * @param endRange One-based index, ending index.
	 * @param pov - player's point of view. (i.e. TOP or BOTTOM).
	 * @return HBox with the points as children.
	 */
	private HBox createSetOfPoints(int startRange, int endRange, PlayerPerspectiveFrom pov, Pip[] pips) {
		HBox set = new HBox();
		set.setPrefSize(Settings.getHalfBoardSize().getWidth(), Settings.getPipSize().getHeight());
		
		// Handles the evenly distributed spacings between the points.
		set.setAlignment(Pos.CENTER);
		// why by 5 again?
		double spacing = (Settings.getHalfBoardSize().getWidth()-6*(Settings.getPipSize().getWidth())) / 5;
		set.setSpacing(spacing);
		
		// If bottom of board, points are numbered from smallest to highest from right to left.
		// Else, from smallest to highest from left to right.
		if (pov == PlayerPerspectiveFrom.BOTTOM)
			for (int i = endRange-1; i >= startRange-1; i--) {
				set.getChildren().add(pips[i]);
			}
		else {
			for (int i = startRange-1; i < endRange; i++) {
				set.getChildren().add(pips[i]);
			}
		}
		return set;
	}
}
