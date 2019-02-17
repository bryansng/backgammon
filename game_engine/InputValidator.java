package game_engine;

/**
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public interface InputValidator {
	// check if input is a number, it is a pip if number.
	default boolean isPip(String input) {
		return isNumber(input);
	}
	
	// check if input is a string, it is a bar or home if string.
	default boolean isBarOrHome(String input) {
		return isString(input);
	}
	
	default boolean isNumber(String input) {
		boolean isNumber = true;
		try {
			Integer.parseInt(input);
		} catch (NumberFormatException e) {
			isNumber = false;
		}
		return isNumber;
	}
	
	default boolean isString(String input) {
		return !isNumber(input);
	}
}
