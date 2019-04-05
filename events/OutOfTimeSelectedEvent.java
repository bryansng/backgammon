package events;

import javafx.event.EventType;

/**
 * Custom Selected Event for out of time.
 * 
 * References:
 * https://stackoverflow.com/questions/27416758/how-to-emit-and-handle-custom-events
 * https://stackoverflow.com/questions/46649406/custom-javafx-events?noredirect=1&lq=1
 *
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 * 
 */
@SuppressWarnings("serial")
public class OutOfTimeSelectedEvent extends OutOfTimeEvent {
	public static final EventType<OutOfTimeEvent> OUTOFTIME_SELECTED = new EventType<>(OUTOFTIME, "OUTOFTIME_SELECTED");
	
	public OutOfTimeSelectedEvent() {
		super(OUTOFTIME_SELECTED);
	}
	
	@Override
	public void invokeHandler(OutOfTimeHandler handler) {
		handler.onOutOfTime();
	}
}
