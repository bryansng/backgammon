
public class Point extends Stack<Checker> {
	public Point() {
		super();
	}
	
	public Point(int num, String colour) {
		for (int i = 0; i < num; i++) {
			push(new Checker(0, 0, colour));
		}
	}
}
