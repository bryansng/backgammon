package interfaces;

import constants.PlayerPerspectiveFrom;
import game_engine.Settings;
import javafx.scene.paint.Color;

/**
 * This interface is used to convert color to player number and vice versa.
 * Added because some of Chris's API's players are in terms of Player object and sometimes integer.
 * i.e. Color.WHITE -> 2
 * i.e. 2 -> Color.WHITE
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public interface PlayerNumberParser extends ColorPerspectiveParser {
	// gets player number from its color.
	default int getPlayerNumber(Color color) {
		int num = 0;
		if (color.equals(getColor(PlayerPerspectiveFrom.BOTTOM))) {
			num = 2;
		} else if (color.equals(getColor(PlayerPerspectiveFrom.TOP))) {
			num = 1;
		}
		return num;
	}
	
	// gets player color from its player number.
	// this method relies on getPlayerNumber().
	default Color getColor(int playerNum) {
		Color color = null;
		if (getPlayerNumber(Settings.getBottomPerspectiveColor()) == playerNum) {
			color = Settings.getBottomPerspectiveColor();
		} else if (getPlayerNumber(Settings.getTopPerspectiveColor()) == playerNum) {
			color = Settings.getTopPerspectiveColor();
		}
		return color;
	}
}
