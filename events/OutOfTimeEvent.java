package events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Custom Event for out of time.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
@SuppressWarnings("serial")
public abstract class OutOfTimeEvent extends Event {
	public static final EventType<OutOfTimeEvent> OUTOFTIME = new EventType<>(ANY, "OUTOFTIME");

	public OutOfTimeEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public abstract void invokeHandler(OutOfTimeHandler handle);
}
