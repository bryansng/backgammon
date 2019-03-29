package events;

import game.TouchablesStorer;
import javafx.event.EventHandler;

/**
 * Custom Event Handler for checkers storers.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public abstract class TouchablesStorerHandler implements EventHandler<TouchablesStorerEvent> {
	public abstract void onClicked(TouchablesStorer touchablesStorerSelected);
	
	@Override
	public void handle(TouchablesStorerEvent event) {
		event.invokeHandler(this);
	}
}
