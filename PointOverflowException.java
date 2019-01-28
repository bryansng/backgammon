
/* Source:
 * https://stackoverflow.com/questions/1754315/how-to-create-custom-exceptions-in-java
 * Consider whether to use RuntimeException or Exception.
 * Difference is RuntimeException throws exception during runtime,
 * while exception at compile time.
 */

@SuppressWarnings("serial")
public class PointOverflowException extends RuntimeException {
	public PointOverflowException() {super();}
	public PointOverflowException(String message) {super(message);}
	public PointOverflowException(String message, Throwable cause) {super(message, cause);}
	public PointOverflowException(Throwable cause) {super(cause);}
}
