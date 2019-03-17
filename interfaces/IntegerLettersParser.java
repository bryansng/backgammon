package interfaces;

// original idea was to store integers in hash map,
// but due to limitations in current parser,
// hash map still store letters, but this time
// there is a better method to convert integers to letters.
public interface IntegerLettersParser {
	// able to convert up to and including "ZZ".
	// NOTE: Not in use because need check if within 'A' to 'ZZ'. 
	default int toInteger(String letters) {
		int value = 0;
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		int size = alphabet.length();
		
		// Start with character from the back,
		// i.e. "BZ", start with character Z.
		int charInd = letters.length()-1;
		int i = 0;
		for (; i < letters.length(); i++, charInd--) {
			if (i == 0) value += alphabet.indexOf(letters.charAt(charInd));
			else if (i > 0) value += size * i * alphabet.indexOf(letters.charAt(charInd));
		}
		return value;
	}
	
	// able to convert up to and including 702.
	default String toLetters(int value) {
		String letters = null;
		
		// limit checking.
		if (value >= 1 && value <= 702) {
			letters = "";
			String alphabet = "abcdefghijklmnopqrstuvwxyz";
			int size = alphabet.length();
			
			if (value/(size+1) > 0)
				letters += alphabet.charAt((value-1)/size-1);
			letters += alphabet.charAt((value-1)%size);
		}
		return letters.toUpperCase();
	}
}
