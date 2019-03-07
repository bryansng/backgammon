package interfaces;

import javafx.scene.paint.Color;

/**
 * This interface is used to parse colors,
 * implemented by classes that uses static constants from the Color library
 * but require the Colors in terms of string or vice versa.
 * i.e. Color.WHITE -> "white"
 * i.e. "white" -> Color.WHITE
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public interface ColorParser {
	/**
	 * Takes in a Color in terms of Color library's static constant and
	 * converts that into its string representation.
	 * @param color the color in terms of Color library's static constant.
	 * @return returns the color in terms of string.
	 */
	default String parseColor(Color color) {
		String colorString = null;
		
		if (color == Color.WHITE) {
			colorString = "white";
		} else if (color == Color.BLACK) {
			colorString = "black";
		} else if (color == Color.RED) {
			colorString = "red";
		}
		
		return colorString;
	}

	/**
	 * Takes in a Color in terms of its string representation and
	 * converts that into its Color library's static constant.
	 * @param color the color in terms of string.
	 * @return returns the color in terms of Color library's static constant.
	 */
	default Color parseColor(String colorString) {
		colorString = colorString.toLowerCase();
		Color color = null;
		
		if (colorString.equals("white")) {
			color = Color.WHITE;
		} else if (colorString.equals("black")) {
			color = Color.BLACK;
		} else if (colorString.equals("red")) {
			color = Color.RED;
		}
		
		return color;
	}
}
