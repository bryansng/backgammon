package events;

import game.CheckersStorer;
import javafx.event.EventHandler;

/**
 * Custom Event Handler for checkers storers.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public abstract class CheckersStorerHandler implements EventHandler<CheckersStorerEvent> {
	public abstract void onClicked(CheckersStorer checkersStorerSelected);
	
	@Override
	public void handle(CheckersStorerEvent event) {
		event.invokeHandler(this);
	}
}
