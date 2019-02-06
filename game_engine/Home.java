package game_engine;

public class Home extends CheckersStorer {
	public Home() {
		super();
		double pointHeight = Settings.getPointSize().getHeight();
		double pointWidth = Settings.getPointSize().getWidth();
		
		setPrefSize(pointWidth, pointHeight);
		setStyle(Settings.getBoardColour());
	}
}
