package game_engine;

import constants.PlayerPerspectiveFrom;
import javafx.scene.paint.Color;

public interface ColorPerspectiveParser {
	// get the color given its pov.
	// this method relies on getTopPerspectiveColor && getBottomPerspectiveColor.
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
