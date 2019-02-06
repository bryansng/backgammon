package game_engine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

public class HomePanel extends BorderPane {
	private Home top;
	private Home bottom;
	
	public HomePanel() {
		super();
		double halfBoardHeight = Settings.getHalfBoardSize().getHeight();
		double pointWidth = Settings.getPointSize().getWidth();
		
		setMinSize(pointWidth, halfBoardHeight);
		setStyle(Settings.getGameColour());
		initHomes();
	}
	
	public void initHomes() {
		top = new Home();
		bottom = new Home();
		
		double margin = Settings.getHomeMargin();
		
		setMargin(top, new Insets(margin));
		setAlignment(top, Pos.CENTER);
		setMargin(bottom, new Insets(margin));
		setAlignment(bottom, Pos.CENTER);
		
		setTop(top);
		setBottom(bottom);
	}
}
