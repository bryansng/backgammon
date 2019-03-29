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
public abstract class TouchablesStorerEvent extends Event {
	public static final EventType<TouchablesStorerEvent> STORER = new EventType<>(ANY, "STORER");

	public TouchablesStorerEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public abstract void invokeHandler(TouchablesStorerHandler handle);
}
