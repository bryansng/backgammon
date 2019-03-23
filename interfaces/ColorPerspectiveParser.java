package interfaces;

import constants.PlayerPerspectiveFrom;
import game_engine.Settings;
import javafx.scene.paint.Color;

/**
 * This interface is used to convert color to pov and vice versa,
 * implemented by classes that uses static constants from the Color library
 * but require its equivalence in terms of PlayerPerspectiveFrom or vice versa.
 * i.e. Color.WHITE -> PlayerPerspectiveFrom.BOTTOM
 * i.e. PlayerPerspectiveFrom.BOTTOM -> Color.WHITE
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public interface ColorPerspectiveParser {
	// get the color given its pov.
	// this method relies on getTopPerspectiveColor() && getBottomPerspectiveColor() in Settings.
	default Color getColor(PlayerPerspectiveFrom pov) {
		Color color = null;
		switch (pov) {
			case BOTTOM:
				color = Settings.getBottomPerspectiveColor();
				break;
			case TOP:
				color = Settings.getTopPerspectiveColor();
				break;
			default:
		}
		return color;
	}

	// get the pov given its color.
	// this method relies on getColor.
	default PlayerPerspectiveFrom getPOV(Color color) {
		PlayerPerspectiveFrom pov = null;
		if (color.equals(getColor(PlayerPerspectiveFrom.BOTTOM))) {
			pov = PlayerPerspectiveFrom.BOTTOM;
		} else if (color.equals(getColor(PlayerPerspectiveFrom.TOP))) {
			pov = PlayerPerspectiveFrom.TOP;
		}
		return pov;
	}
}
