package game_engine;

import javafx.geometry.Pos;

public class Bar extends CheckersStorer {
	private String checkerColoursToStore;
	
	public Bar(String colour) {
		super();
		checkerColoursToStore = colour;
		
		double pointWidth = Settings.getPointSize().getWidth();
		double pointHeight = Settings.getPointSize().getHeight();
		
		setMinSize(pointWidth, pointHeight);
		setMaxSize(pointWidth, pointHeight);
		setAlignment(Pos.BOTTOM_CENTER);
	}
	
	public String getColour() {
		return checkerColoursToStore;
	}
}
