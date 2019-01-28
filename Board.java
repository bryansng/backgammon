import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;

public class Board extends Position{
	private final int MAXSIZE = 24;
	private Point[] points;
	private Image img;
	
	/**
	 * Default Constructor - Initialize points with their initial checkers.
	 */
	public Board() {
		points = new Point[MAXSIZE];
		initImg();
		initCheckers();
	}
	
	/**
	 * Function initializes board image.
	 */
	public void initImg() {
		FileInputStream input = null;
		try {
			 input = new FileInputStream("src/img/board/board.png");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//img = Functions.scale(new Image(input), Settings.getScale());
		img = new Image(input);
	}
	
	/**
	 * Function returns Image object of board.
	 * @return Image object of board.
	 */
	public Image getImg() {
		return img;
	}
	
	public Point[] getPoints() { 
		return points;
	}
	
	private void initCheckers() {
		/* Format:
		 * Points - NumberOfCheckers CheckersColor
		 * 1 - 2 B
		 * 6 - 5 W
		 * 8 - 3 W
		 * 12 - 5 B
		 * 13 - 5 W
		 * 17 - 3 B
		 * 19 - 5 B
		 * 24 - 2 W
		 */
		for (int i = 0; i < MAXSIZE; i++) {
			switch (i) {
				case 0:
					points[i] = new Point(2, "BLACK");
					break;
				case 5:
					points[i] = new Point(5, "WHITE");
					break;
				case 7:
					points[i] = new Point(3, "WHITE");
					break;
				case 11:
					points[i] = new Point(5, "BLACK");
					break;
				case 12:
					points[i] = new Point(5, "WHITE");
					break;
				case 16:
					points[i] = new Point(3, "BLACK");
					break;
				case 18:
					points[i] = new Point(5, "BLACK");
					break;
				case 23:
					points[i] = new Point(2, "WHITE");
					break;
				default:
					points[i] = new Point();
			}
		}
	}
}
