package events;

import javafx.event.Event;
import javafx.event.EventType;

@SuppressWarnings("serial")
public abstract class PointEvent extends Event {
	public static final EventType<PointEvent> POINT = new EventType<>(ANY);

	public PointEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public abstract void invokeHandler(PointHandler handle);
}
