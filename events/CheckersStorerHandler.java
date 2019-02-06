package events;

import game_engine.CheckersStorer;
import javafx.event.EventHandler;

public abstract class CheckersStorerHandler implements EventHandler<CheckersStorerEvent> {
	public abstract void onClicked(CheckersStorer checkersStorerSelected);
	
	@Override
	public void handle(CheckersStorerEvent event) {
		event.invokeHandler(this);
	}
}
