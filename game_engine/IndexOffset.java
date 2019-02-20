package game_engine;

import constants.GameConstants;

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
	
	default String getTopPlayerOffset(String input) {
		String result = input;
		if (isNumber(input)) {
			int num = Integer.parseInt(input);
			result = Integer.toString(GameConstants.NUMBER_OF_PIPS-num-1);
		}
		return result;
	}
}
