package exceptions;

/**
 * This class represents an stack overflow exception.
 * 
 * Source:
 * https://stackoverflow.com/questions/1754315/how-to-create-custom-exceptions-in-java
 * Consider whether to use RuntimeException or Exception.
 * Difference is RuntimeException throws exception during runtime,
 * while exception at compile time.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 * 
 */
@SuppressWarnings("serial")
public class StackOverflowException extends RuntimeException {
	public StackOverflowException() {super();}
	public StackOverflowException(String message) {super(message);}
	public StackOverflowException(String message, Throwable cause) {super(message, cause);}
	public StackOverflowException(Throwable cause) {super(cause);}
}
