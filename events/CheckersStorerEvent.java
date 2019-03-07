package events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Custom Event for checkers storers.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
@SuppressWarnings("serial")
public abstract class CheckersStorerEvent extends Event {
	public static final EventType<CheckersStorerEvent> STORER = new EventType<>(ANY);

	public CheckersStorerEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public abstract void invokeHandler(CheckersStorerHandler handle);
}
