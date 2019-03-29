package events;

import game.Touchable;
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
public abstract class TouchableHandler implements EventHandler<TouchableEvent> {
	public abstract void onClicked(Touchable touchableSelected);
	
	@Override
	public void handle(TouchableEvent event) {
		event.invokeHandler(this);
	}
}
