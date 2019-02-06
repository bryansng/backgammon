package events;

import javafx.event.Event;
import javafx.event.EventType;

@SuppressWarnings("serial")
public abstract class CheckersStorerEvent extends Event {
	public static final EventType<CheckersStorerEvent> STORER = new EventType<>(ANY);

	public CheckersStorerEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public abstract void invokeHandler(CheckersStorerHandler handle);
}
