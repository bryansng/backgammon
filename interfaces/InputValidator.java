package interfaces;

/**
 * This interface is used to validate player inputs.
 * Used by CommandController to parse player inputs.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public interface InputValidator {
	/**
	 * It is a pip if it is a number.
	 * @param input
	 * @return boolean value indicating if so.
	 */
	default boolean isPip(String input) {
		return isNumber(input);
	}

	/**
	 * It is a bar or home if it is a string.
	 * @param input
	 * @return boolean value indicating if so.
	 */
	default boolean isBarOrHome(String input) {
		return isString(input);
	}

	/**
	 * Check if input is a number, i.e. it is a pip if number.
	 * @param input
	 * @return boolean value indicating if so.
	 */
	default boolean isNumber(String input) {
		boolean isNumber = true;
		try {
			Integer.parseInt(input);
		} catch (NumberFormatException e) {
			isNumber = false;
		}
		return isNumber;
	}

	/**
	 * Check if input is a string, i.e. it is a bar or home if string.
	 * @param input
	 * @return boolean value indicating if so.
	 */
	default boolean isString(String input) {
		return !isNumber(input);
	}
}
