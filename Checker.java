import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;

public class Checker extends Position {
	private Image img;
	
	public Checker() {
		this(0, 0, "WHITE");
	}
	
	public Checker(double x, double y, String colour) {
		//setPos(x, y);
		initImg(colour);
	}
	
	public void initImg(String color) {
		FileInputStream input = null;
		try {
			if (color.equals("BLACK")) {
				input = new FileInputStream("src/img/checkers/black_checkers.png");
			}
			else if (color.equals("WHITE")) {
				input = new FileInputStream("src/img/checkers/white_checkers.png");
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//img = Functions.scale(new Image(input), Settings.getScale());
		img = new Image(input);
	}
	
	public Image getImg() {
		return img;
	}
	
	public Position getPos() {
		return new Position(getX(), getY());
	}
	
	public void setPos(double x, double y) {
		setX(x);
		setY(y);
	}
}
