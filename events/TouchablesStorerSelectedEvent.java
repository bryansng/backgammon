package events;

import game.TouchablesStorer;
import javafx.event.EventType;

/**
 * Custom Selected Event for checkers storers.
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
public class TouchablesStorerSelectedEvent extends TouchablesStorerEvent {
	public static final EventType<TouchablesStorerEvent> STORER_SELECTED = new EventType<>(STORER, "STORER_SELECTED");
	
	private final TouchablesStorer storerSelected;
	
	public TouchablesStorerSelectedEvent(TouchablesStorer touchablesStorerSelected) {
		super(STORER_SELECTED);
		this.storerSelected = touchablesStorerSelected;
	}
	
	@Override
	public void invokeHandler(TouchablesStorerHandler handler) {
		handler.onClicked(storerSelected);
	}
}
