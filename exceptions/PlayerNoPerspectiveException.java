package exceptions;

/**
 * This class represents a player no perspective exception.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 * 
 */
@SuppressWarnings("serial")
public class PlayerNoPerspectiveException extends RuntimeException {
	public PlayerNoPerspectiveException() {super();}
	public PlayerNoPerspectiveException(String message) {super(message);}
	public PlayerNoPerspectiveException(String message, Throwable cause) {super(message, cause);}
	public PlayerNoPerspectiveException(Throwable cause) {super(cause);}
}
