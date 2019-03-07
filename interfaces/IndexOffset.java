package interfaces;

import constants.GameConstants;

/**
 * This interface is used to offset pip indexes.
 * Used by runMoveCommand() in CommandController.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public interface IndexOffset extends InputValidator {
	/**
	 * Returns a zero-based index number in terms of string.
	 * @param input, should be one-based index in terms of string.
	 * @return offset index in terms of string.
	 */
	default String getZeroBasedIndex(String input) {
		String result = input;
		if (isNumber(input)) {
			result = Integer.toString((Integer.parseInt(input)-1));
		}
		return result;
	}
	
	/**
	 * Converts pip numbers from one perspective to the other.
	 * 
	 * REASON: Code will always calculate moves from the perspective of the bottom player.
	 * If it is the top player's turn to move, the pip number labels are swapped,
	 * so the player will enter pip numbers from the perspective of the top player.
	 * @param input, should be zero-based index in terms of string.
	 * @return offset index in terms of string.
	 */
	default String getTopPlayerOffset(String input) {
		String result = input;
		if (isNumber(input)) {
			int num = Integer.parseInt(input);
			result = Integer.toString(GameConstants.NUMBER_OF_PIPS-num-1);
			// TOTAL pips = 24.
			// 23 should be mapped to 0.
			// 22 should be mapped to 1.
			// 21 should be mapped to 2.
			//
			// Proof:
			// Formula: 24 - 23 - 1 = 0, or 24 - 0 - 1 = 23.
			// Formula: 24 - 21 - 1 = 2, or 24 - 2 - 1 = 21.
		}
		return result;
	}
	
	/**
	 * Get the correct output pip number, based on perspective of player.
	 * @param pipNum pip number.
	 * @param isTopPlayer, boolean value indicating if its top player.
	 * @return correct pip number in terms of string.
	 */
	default String getOutputPipNumber(int pipNum, boolean isTopPlayer) {
		String correctNum;
		
		if (isTopPlayer) {
			correctNum = getOneBasedIndex(Integer.parseInt(getTopPlayerOffset(Integer.toString(pipNum))));
		} else {
			correctNum = getOneBasedIndex(pipNum);
		}
		
		return correctNum;
	}
	
	/**
	 * Shorter, less cluttering, manageable way of calling getOutputPipNumber.
	 * Abstract so that the controllers can implement and pass the isTopPlayer boolean.
	 * @param pipNum
	 * @return correct pip number in terms of string.
	 */
	String correct(int pipNum);

	/**
	 * Returns a one-based index number in terms of string.
	 * @param input, should be zero-based index.
	 * @return offset index in terms of string.
	 */
	default String getOneBasedIndex(int num) {
		return Integer.toString(num+1);
	}
}
