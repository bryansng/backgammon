package events;

import javafx.event.EventHandler;

/**
 * Custom Event Handler for out of time.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public abstract class OutOfTimeHandler implements EventHandler<OutOfTimeEvent> {
	public abstract void onOutOfTime();
	
	@Override
	public void handle(OutOfTimeEvent event) {
		event.invokeHandler(this);
	}
}
