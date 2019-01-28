import java.awt.Dimension;
import java.awt.Toolkit;

public class Settings {
	/**
	 * Screen sizes - Used to set scene width and height.
	 */
	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static double width = screenSize.getWidth();
	private static double height = screenSize.getHeight();
	public static double getScreenWidth() {
		return width;
	}
	
	public static double getScreenHeight() {
		return height;
	}
	
	/**
	 * Scale - Used to scale size of images.
	 */
	private static double scale = 0.8;
	public static double getScale() {
		return scale;
	}
}
