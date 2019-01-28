import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Functions {
	/** METHOD NOT USED AT THE MOMENT AS SCALED SNAPSHOT IMAGES LOSES TRANSPARENCY.
	 * 
	 * Function takes in an image and scale, and returns the rescaled image.
	 * @param img Image to scale.
	 * @param scale Scale to be used, i.e. 0.5, 0.8, 1.5.
	 * @return Rescaled image.
	 */
	public static Image scale(Image img, double scale) {
		ImageView imgV = new ImageView(img);
		double width = img.getWidth() * scale;
		double height = img.getHeight() * scale;
		imgV.setPreserveRatio(true);
		imgV.setFitWidth(width);
		imgV.setFitHeight(height);
		return imgV.snapshot(null, null);
	}
}
