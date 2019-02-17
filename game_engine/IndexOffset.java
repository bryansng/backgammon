package game_engine;

public interface IndexOffset extends InputValidator {
	default String getZeroBasedIndex(String input) {
		String result = input;
		if (isNumber(input)) {
			result = Integer.toString((Integer.parseInt(input)-1));
		}
		return result;
	}
	
	default String getOneBasedIndex(String input) {
		String result = input;
		if (isNumber(input)) {
			result = Integer.toString((Integer.parseInt(input)+1));
		}
		return result;
	}
}
